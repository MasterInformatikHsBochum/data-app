package com.example.lisa.challenge;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.TimeUnit;

class RotationBerechnenThread extends AsyncTask {

    private Rotate rotate;
    ChallangeAction challangeAction;

    public RotationBerechnenThread(Rotate rotate, ChallangeAction challangeAction) {
        this.rotate = rotate;
        this.challangeAction = challangeAction;
    }

    /**
     * Warte so lange bis das Smartphone in der Hosentasche ist und die Messung läuft
     */
    private void waitToBeDark() {
        while (!challangeAction.isDark(rotate.getAktuellerLichwert()) && rotate.getStartMeasure()) {

            try {
                Log.d("runnable", "Warte das Handy in die Hosentesche gesteckt wird. Aktueller Lichtewert = " + rotate.getAktuellerLichwert());
                TimeUnit.SECONDS.sleep(1);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d("runnable", " >>>>> Aktueller Lichtewert = " + rotate.getAktuellerLichwert());
    }

    /**
     * Wenn das Handy in die Hosentasche gesteckt wird, warte x Millisekunden und gebe dann den Starton wieder
     */
    public void generateTone(int timeToSleep) {

        final ToneGenerator sound = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

        try {
            Thread.sleep(timeToSleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sound.startTone(ToneGenerator.TONE_PROP_BEEP, 2000);
        Log.d("beep", "beep");
        rotate.beep();
    }

    private boolean isAktuellerWertnNahmeKontrollpunk(int aktuellerWert, int kontrollpunkt, int varianz) {
        //Ein Kreis hat 360 Grad
        int kontrollZoneBeginn = kontrollpunkt;
        int kontrollZoneEnde = kontrollpunkt + varianz % 360;

        if (kontrollZoneEnde < kontrollZoneBeginn) {
            //Der aktuelle Wert ist immer zwischen 0 und 360
            //Die 360 Grad wurden überschritten, dann kann der neue Wert zwischen 0 und den berechneten bereich
            if (aktuellerWert > kontrollZoneBeginn || (aktuellerWert > 0 && aktuellerWert < kontrollZoneEnde)) {
                Log.d("runnable", "Ergebnis = true + aktuellerWert: " + aktuellerWert + " kontrollZoneBeginn "+ kontrollZoneBeginn + "kontrollzoneEnde" + kontrollZoneEnde);
                return true;
            } else {
                Log.d("runnable", "Ergebnis = false + aktuellerWert: " + aktuellerWert + " kontrollZoneBeginn "+ kontrollZoneBeginn + "kontrollzoneEnde" + kontrollZoneEnde);
                return false;
            }
        } else {
            if (kontrollZoneBeginn < aktuellerWert && aktuellerWert < kontrollZoneEnde) {
                Log.d("runnable", "Ergebnis2 = true + aktuellerWert: " + aktuellerWert + " kontrollZoneBeginn "+ kontrollZoneBeginn + "kontrollzoneEnde" + kontrollZoneEnde);
                return true;
            } else {
                Log.d("runnable", "Ergebnis2 = false + aktuellerWert: " + aktuellerWert + " kontrollZoneBeginn "+ kontrollZoneBeginn + "kontrollzoneEnde" + kontrollZoneEnde);
                return false;
            }
        }
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        waitToBeDark();

        //Die Messung wurde abgebrochen
        if (rotate.getStartMeasure() == false) {
            return null;
        }

        //Gebe 3 Sekunden nachdem das Handy in der Hosentasche ist das Startgeräusch
        generateTone(3000);

        //Bestimme die Ausgangsposition der Drehung
        int gradzahlStart = rotate.getAktuelleGradZahl();
        Log.d("runnable", "Der Startwert lautet " + gradzahlStart + "Grad");

        long t = System.currentTimeMillis();
        long end = t + 10000;
        //Berechne Gradzahl für 2. Kontrollpunkte. Diese sollen nicht exakt mit der Ausgangslage übereinstimmen. Messung soll nicht zu früh beginnen
        final int kontrollpunkt1 = (gradzahlStart + 30) % 360;
        final int kontrollpunkt2 = (kontrollpunkt1 + 180) % 360;
        int kontrollpunkt = kontrollpunkt2;

        int drehung = -1; //Beim Ersten druchlauf muss 0 erreicht werden

        while (challangeAction.isDark(rotate.getAktuellerLichwert()) && rotate.getStartMeasure() && (System.currentTimeMillis() < end)) {
            // Solange es dunkel ist, und maximal 10 Sekunden Werte messen.
            //Der Sensor kann immer nur Wert zwischen 0 und +-360 zurück geben

            int aktuellerWert = rotate.getAktuelleGradZahl();

            //Abwechselnd an 2 sich gegenüberliegenden Punkten auf den Kreis Prüfen, ob die Person sich dreht
            //Hierfür wird der Bezugspunk (Kontrollpunk) beim erreichen immer um 180 Grad %360 verschoben. Wenn Ausgangspunkt erreicht ist zähle hoch
            if (isAktuellerWertnNahmeKontrollpunk(aktuellerWert, kontrollpunkt, 40)) {
                Log.d("runnable", "Kontrollzone erreicht");
                if (kontrollpunkt == kontrollpunkt1) //Ausgangspunkt
                {
                    Log.d("runnable", "Kontrollpunkt 1: Die aktuelle Gradzal lautet " + aktuellerWert);
                    drehung++;
                    kontrollpunkt = kontrollpunkt2;
                    continue;
                }
                if (kontrollpunkt == kontrollpunkt2) //halbe Drehung
                {
                    Log.d("runnable", "Kontrollpunkt 2: Die aktuelle Gradzal lautet " + aktuellerWert);
                    kontrollpunkt = kontrollpunkt1;
                    continue;
                }
            }
            //Warte ein paar millisekunden, bis zur Auswertung mit dem nächsten Wert
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Wir haben versetzt um 30 Grad angefanden. Daher müssen wir prüfen ob zusammen mit den 30 GRad eine weitere Drehung geschafft wurde
        int letzeGradzahl = rotate.getAktuelleGradZahl() + 30;
        if (letzeGradzahl > 360) {
            drehung++;
            //berechne Restwert des Kreises
            letzeGradzahl = letzeGradzahl % 360;
        }


        //Singnalisiere Ende dem Anwender duch einen erneuten Ton
        if (!(challangeAction.isDark(rotate.getAktuellerLichwert()) && rotate.getStartMeasure())) {
            Log.d("runnable", "Messung abgebrochen. Der Lichwert ist " + rotate.getAktuellerLichwert());
            rotate.setStartMeasure(false);
            return null;
        }

        generateTone(0);
        Log.d("runnable", "Der Endwert lautet " + letzeGradzahl + "Grad");
        rotate.setAnzahlDrehungen(drehung);
        rotate.setErgebnisGradzahl(drehung * 360 + letzeGradzahl);
        Log.d("runnable", "Du hast dich um " + rotate.getErgebnisGradzahl() + "Grad gedreht. Das sind " + drehung + " Drehungen");
        rotate.setStartMeasure(false);
        rotate.ueberTrageMessergebnis();
        return rotate.getErgebnisGradzahl();

    }
}
