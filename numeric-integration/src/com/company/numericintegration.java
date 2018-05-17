package com.company;

import java.util.LinkedList;
import java.util.Vector;

public class numericintegration {
    public static double Simpsonrule_weg(LinkedList<Vector<Double>> messwerte){
        double sum = 0;
        int n = messwerte.size();
        for (int i =0; i < n-2;i++){
            sum += (messwerte.get(i+1).get(1)-messwerte.get(i).get(1))*(messwerte.get(i).get(0)+4*messwerte.get(i+1).get(0)+messwerte.get(i+2).get(0));
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
            sum+= (messwerte.get(i+1).get(1)-messwerte.get(i).get(1))*(messwerte.get(i).get(0)+messwerte.get(i+1).get(0));
        }
        return (1/2.0)*sum;
    }

}

