package com.company;

import java.util.LinkedList;
import java.util.Vector;
import java.util.List;


public class numericintegration {
    public static double Simpsonrule_final(List<Float> messwerte, float dt){
        LinkedList<Vector<Double>> mess_final = new LinkedList<Vector<Double>>();
        for(int i = 0; i < messwerte.size();i++){
            Vector<Double> vec = new Vector<>();
            vec.add((double) messwerte.get(i));
            vec.add((double) i*dt);
            mess_final.add(vec);
        }
        return Simpsonrule_weg(mess_final);


    }
    public static double Simpsonrule_weg(LinkedList<Vector<Double>> messwerte){
        double sum = 0;
        int n = messwerte.size();
        for (int i =0; i < n-2;i++){
            sum += ((messwerte.get(i+1).get(1)-messwerte.get(i).get(1)))*((messwerte.get(i).get(0)+4*messwerte.get(i+1).get(0)+messwerte.get(i+2).get(0)));
        }
        return (1/6.0)*sum;
    }
    public static double naive_weg(LinkedList<Vector<Double>> messwerte) {
        double sum = 0;
        for( Vector<Double> wert : messwerte){
            sum += wert.get(0)*wert.get(1);
        }
        return sum;
    }
    public static double trapez_weg(LinkedList<Vector<Double>> messwerte) {
        double sum = 0;
        int n = messwerte.size();
        for (int i =0; i < n-1;i++){
            sum+= ((messwerte.get(i+1).get(1)-messwerte.get(i).get(1))*(messwerte.get(i).get(0)+messwerte.get(i+1).get(0)));
        }
        return (1/2.0)*sum;
    }
    public static LinkedList<Vector<Double>>trapez_liste(LinkedList<Vector<Double>> messwerte) {
        LinkedList<Vector<Double>> list = new LinkedList<Vector<Double>>();
        int n = messwerte.size();
        for (int i =0; i < n-1;i++){
            Vector<Double> vec = new Vector <Double>();
            vec.add((1/2.0)*(messwerte.get(i+1).get(1)-messwerte.get(i).get(1))*(messwerte.get(i).get(0)+messwerte.get(i+1).get(0)));
            vec.add(messwerte.get(i).get(1));
            list.add(vec);
        }
        return list;
    }

}

