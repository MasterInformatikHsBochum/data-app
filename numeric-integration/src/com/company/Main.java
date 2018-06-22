package com.company;

import java.util.LinkedList;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
public class Main {
    public static double f(double x){
        return x*x+4*x;
    }
    public static double f2(double x){
        return 1*x;
    }
    public double v =0;
    public double f2(double t,double a){return (1/2)*t*t+v*t;}

    public static void main(String[] args) {
        // write your code here
        List <Float> liste = new ArrayList<Float>() ;

        for(double i=0;i < 3.0;i+=0.001) {
            liste.add((float) f(i));
        }
        //System.out.println((((20*20*20)/3.0)+((4*20*20)/2))-(((0*0*0)/3)+(4*20*20)/2));
        //System.out.println((((5*5*5)/3.0)+((4*5*5)/2)));
        //System.out.println((((5*5*5*5)/12.0)+((4*5*5*5)/6)));
        System.out.println(numericintegration.Simpsonrule_final(liste, (float)0.001));
        //System.out.println(numericintegration.Simpsonrule_weg(liste));
        //System.out.println(numericintegration.Simpsonrule_weg(liste2));
        //System.out.println(numericintegration.trapez_weg(liste)*2);
       /*
        System.out.println(numericintegration.Simpsonrule_weg(liste));
        System.out.println(numericintegration.naive_weg(liste));
        System.out.println(numericintegration.trapez_weg(liste));
        */
        //System.out.println(((5*5)/2)+5);
        //System.out.println(((5*5*5)/6.0)+((5*5)/2.0));
        //System.out.println(numericintegration.Simpsonrule_weg(liste2));
        //System.out.println(numericintegration.naive_weg(liste2));
        //System.out.println(numericintegration.trapez_weg(liste2));


    }
}
