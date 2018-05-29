package com.example.lisa.challenge;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;


public class ChallangeAction {

       final ToneGenerator sound = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

    /**
     * Die Abtastrate, die zum Aufzeichnen und Berechnen von Distanzen verwendet wird.
     */
    private static final int abtastrate=100;


    /**
     * Wenn der Lichtwert unter 10 ist (es ist Dunkel) wird true zurückgegeben, sonst false
     */
    public boolean isDark(float aktuellerLichtwert)
    {
            return aktuellerLichtwert<10? true: false;
    }

    public float getDiffenenzOfStartValueAndMax(float startValue, List<Float> messwerte)
    {
        if(messwerte==null || messwerte.isEmpty())
        {
            return 0;
        }
        //Maximum ermitteln
        Float max = Collections.max(messwerte);
        Log.d("runnable","Maximaler Messwert "+ max + "- startwert"+ startValue);

        if(max<startValue)
        {
            return 0;
        }
        return max- startValue;
    }

    /**
     * Einfache doppelte Rechteckintegration mit einer großen Abweichung
     * (Werte sind deutlich zu klein)
     * @param messwerte
     * @return
     */
    public float numIntegrationStandard(List<Float> messwerte){

        float periodendauer = (float)1.0/(float)abtastrate;
        Log.d("runnable", "Periodendauer " + periodendauer);
        List<Float> listFlaeche = new ArrayList<>();

        for(int i = 0; i < messwerte.size(); i++){
            if(i%20 == 0){
                Log.d("runnable", "Messwert " + i + ": " + messwerte.get(i));
            }
            //if(messwerte.get(i) > 0.1 && messwerte.get(i) < (-0.1)) {
                listFlaeche.add(messwerte.get(i) * periodendauer * periodendauer);
                //Log.d("runnable", "Messwert " + i + ": " + messwerte.get(i));

            //}
        }
//        Log.d("runnable", "listFlaeche(0) " + listFlaeche.get(0));

        float flaeche = 0;

        for(int i = 0; i < listFlaeche.size(); i++){
            if(i%20 == 0){
                Log.d("runnable", "listFlaeche " + i + ": " + listFlaeche.get(i));
            }
            //if(listFlaeche.get(i)>=0){
                flaeche = flaeche + Math.abs(listFlaeche.get(i));
            //}
            //Log.d("runnable","zurückgelegter weg: "+ flaeche);
        }
        return flaeche;
    }



    public float doppeltIntegration(List<Float> messwerte){

        float periodendauer = (float)1.0/(float)abtastrate;
        List<Float> listFlaeche = new ArrayList<>();

        for(int i = 0; i < messwerte.size(); i++){
            if(i%20 == 0){
                Log.d("runnable", "Messwert " + i + ": " + messwerte.get(i));
            }


            LinearFunction linearFunction = makeLinearFunction(
                    (float)i/(float)100.0, (float)(i+1)/(float)100.0, messwerte.get(i), messwerte.get(i+1) );

            PolynomGradDrei polynomGradDrei = berechneDoppeltesUnbestimmtesIntegral(linearFunction);

            float flaeche = berechneBestimmtesIntegralFunktionGradDrei(polynomGradDrei,(float)i/(float)100.0, (float)(i+1)/(float)100.0 );

            listFlaeche.add(flaeche);

        }


        float flaeche = 0;
        for(int i = 0; i < listFlaeche.size(); i++){
            if(i%20 == 0){
                Log.d("runnable", "listFlaeche " + i + ": " + listFlaeche.get(i));
            }

            flaeche = flaeche + Math.abs(listFlaeche.get(i));
        }
        return flaeche;
    }


    public LinearFunction makeLinearFunction(float t1, float t2, float y1, float y2) {
        /* define gradient */
        float m = (y2 - y1) / (t2 - t1);
        float b = y1 - m * t1;
        return (new LinearFunction(m,b));
    }


    public PolynomGradDrei berechneDoppeltesUnbestimmtesIntegral(LinearFunction lf){
        float a = (1/6)*lf.getM();
        float b = (1/2)*lf.getB();
        return (new PolynomGradDrei(a,b));
    }

    public float berechneBestimmtesIntegralFunktionGradDrei(PolynomGradDrei pn, float t_begin, float t_end){
        float integral = 0;

        integral = pn.getA()*(float)Math.pow(t_begin,3) + pn.getB()*(float)Math.pow(t_begin, 2)
                 - (pn.getA()*(float)Math.pow(t_end,3) + pn.getB()*(float)Math.pow(t_end, 2));


        return integral;
    }





    public int getAbtastrate() {
        return abtastrate;
    }




    class LinearFunction {
        private float m;
        private float b;

        LinearFunction(float steigung, float yAchsenabschnitt){
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

    class PolynomGradDrei{
        private float a;
        private float b;

        PolynomGradDrei(float a, float b){
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
