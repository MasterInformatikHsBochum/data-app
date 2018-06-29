package com.example.lisa.challenge;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

import java.util.LinkedList;
import java.util.Vector;

public class ChallangeAction {

    final ToneGenerator sound = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

    /**
     * Die Abtastrate, die zum Aufzeichnen und Berechnen von Distanzen verwendet wird.
     */
    private static final int abtastrate = 100; //Hz


    /**
     * Wenn der Lichtwert unter 10 ist (es ist Dunkel) wird true zurückgegeben, sonst false
     */
    public boolean isDark(float aktuellerLichtwert) {
        return aktuellerLichtwert < 80 ? true : false;
    }

    public float getDiffenenzOfStartValueAndMax(float startValue, List<Float> messwerte) {
        if (messwerte == null || messwerte.isEmpty()) {
            return 0;
        }
        //Maximum ermitteln
        Float max = Collections.max(messwerte);
        Log.d("runnable", "Maximaler Messwert " + max + "- startwert" + startValue);

        if (max < startValue) {
            return 0;
        }
        return max - startValue;
    }


    public double simpsonrule_weg(LinkedList<Vector<Double>> messwerte){
        double sum = 0;
        int n = messwerte.size();
        for (int i =0; i < n-2;i++){
            sum += (messwerte.get(i+1).get(1)-messwerte.get(i).get(1))*(messwerte.get(i).get(0)+4*messwerte.get(i+1).get(0)+messwerte.get(i+2).get(0));
        }
        return (1/6.0)*sum;
    }

    public double simpsonrule_final(List<Float> messwerte, float dt) {
        LinkedList<Vector<Double>> mess_final = new LinkedList<Vector<Double>>();
        for (int i = 0; i < messwerte.size(); i++) {
            Vector<Double> vec = new Vector<>();
            vec.add((double) messwerte.get(i));
            vec.add((double) i * dt);
            mess_final.add(vec);
        }
        return simpsonrule_weg(mess_final);
    }

    public float wegMitDurschnittsberechnung(List<Float> messwerte){

        float summe = 0;

        for (int i = 0; i < messwerte.size(); i++){
            summe += messwerte.get(i);
        }

        float average = summe/messwerte.size();

        return (average * 3 * 3);
    }


    public float doubleIntegrationLinearAccerlaration(List<Float> messwerte) {


        int aKleinerMinus0_5 = 0;
        int indexStart = 0;
        int indexGroeßtesA = 0;
        float valueGroeßtesA = -100;
        for (int i = 0; i < messwerte.size(); i++) {
            if (messwerte.get(i) < -0.5) {
                aKleinerMinus0_5 = i;
            }
        }
        Log.d("runnable", "aKleinerMinus0.5: " + aKleinerMinus0_5 + " beschleunigung: " + messwerte.get(aKleinerMinus0_5));

        for (int i = aKleinerMinus0_5; i < messwerte.size(); i++) {
            Log.d("runnable", "Messwert " + i + ": " + messwerte.get(i));
            if (messwerte.get(i) > -0.01 && messwerte.get(i) < 0.01) {
                indexStart = i;
            }
        }


        indexStart = aKleinerMinus0_5;
        int i = indexStart;
        while ((i < messwerte.size() - 1) && (messwerte.get(i) >= valueGroeßtesA)) {

            indexGroeßtesA = i;
            valueGroeßtesA = messwerte.get(i);
            i++;
        }

        Log.d("runnable", "indexGroeßtesA: " + indexGroeßtesA + " beschleunigung: " + valueGroeßtesA);

        int indexWiederNull = indexGroeßtesA;   //mindestens größer als groeßte
        i = indexGroeßtesA;
        while ((i < messwerte.size() - 1) && (messwerte.get(i) > 0)) {
            indexWiederNull = i;
            i++;
        }

        LinearFunction linearFunction = makeLinearFunctionOhneB(
                (float) indexStart / (float) abtastrate, (float) indexGroeßtesA / (float) abtastrate, messwerte.get(indexStart), messwerte.get(indexGroeßtesA));


        float t = (float) (indexGroeßtesA - indexStart) / (float) abtastrate;
        Log.d("runnable", "t: " + t);
        //float strecke = ((float)1/(float)6)*linearFunction.getM() * (float)Math.pow(t,3);

        float x = 0;
        float v = 0;
        float dt = 1 / (float) abtastrate;
        Log.d("runnable", "dt: " + dt);

        for (int j = indexStart; j < indexWiederNull; j++) {
            float a = messwerte.get(j);
            x = a * dt * dt + v * dt + x;
            v = a * dt + v;
        }


        return x;
    }

    /**
     * Einfache doppelte Rechteckintegration mit einer großen Abweichung
     * (Werte sind deutlich zu klein)
     *
     * @param messwerte
     * @return
     */
    public float numIntegrationStandard(List<Float> messwerte) {

        float periodendauer = (float) 1.0 / (float) abtastrate;
        Log.d("runnable", "Periodendauer " + periodendauer);
        List<Float> listFlaeche = new ArrayList<>();

        for (int i = 0; i < messwerte.size(); i++) {
            if (i % 20 == 0) {
                Log.d("runnable", "Messwert " + i + ": " + messwerte.get(i));
            }
            //if(messwerte.get(i) > 0.1 && messwerte.get(i) < (-0.1)) {
            listFlaeche.add(messwerte.get(i) * periodendauer * periodendauer);
            //Log.d("runnable", "Messwert " + i + ": " + messwerte.get(i));

            //}
        }
//        Log.d("runnable", "listFlaeche(0) " + listFlaeche.get(0));

        float flaeche = 0;

        for (int i = 0; i < listFlaeche.size(); i++) {
            if (i % 20 == 0) {
                Log.d("runnable", "listFlaeche abs " + i + ": " + Math.abs(listFlaeche.get(i)));
            }
            //if(listFlaeche.get(i)>=0){
            flaeche = flaeche + Math.abs(listFlaeche.get(i));
            //}
            //Log.d("runnable","zurückgelegter weg: "+ flaeche);
        }
        return flaeche;
    }


    public float doppeltIntegration(List<Float> messwerte) {

        float periodendauer = (float) 1.0 / (float) abtastrate;
        List<Float> listFlaeche = new ArrayList<>();

        for (int i = 0; i < (messwerte.size() - 1); i++) {
            //if(i%20 == 0){
            if (i > 100 && i < 200) {
                Log.d("runnable", "Messwert " + i + ": " + messwerte.get(i));
            }


            LinearFunction linearFunction = makeLinearFunction(
                    (float) i / (float) abtastrate, (float) (i + 1) / (float) abtastrate, messwerte.get(i), messwerte.get(i + 1), i);

            PolynomGradDrei polynomGradDrei = berechneDoppeltesUnbestimmtesIntegral(linearFunction, i);

            float flaeche = berechneBestimmtesIntegralFunktionGradDrei(polynomGradDrei, (float) i / (float) abtastrate, (float) (i + 1) / (float) abtastrate);

            listFlaeche.add(flaeche);

        }


        float flaeche = 0;
        for (int i = 0; i < listFlaeche.size(); i++) {
            if (i % 20 == 0) {
                Log.d("runnable", "listFlaeche " + i + ": " + listFlaeche.get(i));
            }

            //flaeche = flaeche + Math.abs(listFlaeche.get(i));
            flaeche = flaeche + listFlaeche.get(i);
        }
        return flaeche;
    }

    public LinearFunction makeLinearFunctionOhneB(float t1, float t2, float y1, float y2) {
        float m = (y2 - y1) / (t2 - t1);
        return (new LinearFunction(m, 0));
    }


    public LinearFunction makeLinearFunction(float t1, float t2, float y1, float y2, int i) {
        /* define gradient */
        float m = (y2 - y1) / (t2 - t1);
        float b = y1 - m * t1;
        if (i > 125 && i < 175) {
            Log.d("runnable", "Steigung " + i + ": " + m);
            Log.d("runnable", "Achsenabschnitt " + i + ": " + b);
        }
        return (new LinearFunction(m, b));
    }


    public PolynomGradDrei berechneDoppeltesUnbestimmtesIntegral(LinearFunction lf, int i) {
        float a = ((float) 1 / (float) 6) * lf.getM();
        float b = ((float) 1 / (float) 2) * lf.getB();
        if (i > 125 && i < 175) {
            Log.d("runnable", "a vor t^3 " + i + ": " + a);
            Log.d("runnable", "b vor t^2 " + i + ": " + b);
        }
        return (new PolynomGradDrei(a, b));
    }

    public float berechneBestimmtesIntegralFunktionGradDrei(PolynomGradDrei pn, float t_begin, float t_end) {
        float integral = 0;

        integral = pn.getA() * (float) Math.pow(t_end, 3) + pn.getB() * (float) Math.pow(t_end, 2)
                - (pn.getA() * (float) Math.pow(t_begin, 3) + pn.getB() * (float) Math.pow(t_begin, 2));


        return integral;
    }

    public String createHiscoreString(int platzEins, int platzZwei, int platzDrei,String einheit) {
        return createHiscoreString(Integer.toString(platzEins), Integer.toString(platzZwei), Integer.toString(platzDrei),einheit);
    }

    public String createHiscoreString(double platzEins, double platzZwei, double platzDrei,String einheit) {
        return createHiscoreString(String.format("%.2f", platzEins), String.format("%.2f", platzZwei), String.format("%.2f", platzDrei),einheit);
    }

    public String createHiscoreString(float platzEins, float platzZwei, float platzDrei,String einheit) {
        return createHiscoreString((double) platzEins, (double) platzZwei, (double) platzDrei,einheit);
    }

    private String createHiscoreString(String platzEins, String platzZwei, String platzDrei, String einheit) {
        return "        Highscore \n   1. " + platzEins + " " + einheit + " \n   2. "
                + platzZwei + " " + einheit + " \n   3. " + platzDrei  + " " + einheit + " ";
    }


    public int getAbtastrate() {
        return abtastrate;
    }


    class LinearFunction {
        private float m;
        private float b;

        LinearFunction(float steigung, float yAchsenabschnitt) {
            this.m = steigung;
            this.b = yAchsenabschnitt;
        }

        public float getB() {
            return b;
        }

        public float getM() {
            return m;
        }

        public void setB(float b) {
            this.b = b;
        }

        public void setM(float m) {
            this.m = m;
        }
    }

    class PolynomGradDrei {
        private float a;
        private float b;

        PolynomGradDrei(float a, float b) {
            this.a = a;
            this.b = b;
        }

        public float getB() {
            return b;
        }

        public float getA() {
            return a;
        }

        public void setB(float b) {
            this.b = b;
        }

        public void setA(float a) {
            this.a = a;
        }
    }

}
