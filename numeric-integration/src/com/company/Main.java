package com.company;

import java.util.LinkedList;
import java.util.Vector;
public class Main {
    public static double f(double x){
        return x*x+4*x;
    }
    public static double f2(double x){
        return x+1;
    }

    public static void main(String[] args) {
        // write your code here
        LinkedList<Vector<Double>> liste = new LinkedList<Vector<Double>>();
        LinkedList<Vector<Double>> liste2 = new LinkedList<Vector<Double>>();

        for(double i=0;i < 5;i+=0.02) {
            Vector<Double> vec = new Vector <Double>();
            vec.add(f(i));
            vec.add(i);
            liste.add(vec);
        }
        for(double i=0;i < 5;i+=0.02) {
            Vector<Double> vec = new Vector <Double>();
            vec.add(f2(i));
            vec.add(i);
            liste2.add(vec);
        }


        //System.out.println((((20*20*20)/3.0)+((4*20*20)/2))-(((0*0*0)/3)+(4*20*20)/2));
        System.out.println((((5*5*5)/3.0)+((4*5*5)/2)));
        System.out.println(numericintegration.Simpsonrule_weg(liste));
        System.out.println(numericintegration.naive_weg(liste));
        System.out.println(numericintegration.trapez_weg(liste));

        System.out.println(((5*5)/2)+5);
        System.out.println(numericintegration.Simpsonrule_weg(liste2));
        System.out.println(numericintegration.naive_weg(liste2));
        System.out.println(numericintegration.trapez_weg(liste2));


    }
}
