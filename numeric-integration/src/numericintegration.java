import java.util.LinkedList;
import java.util.Vector;

public class numericintegration {
    public static double Simpsonrule_weg(LinkedList<Vector<Double>> messwerte){
        double sum=0;
        for(int i= 0; i< messwerte.size()-2;i++){
            sum += (messwerte.get(i).get(0)+4*messwerte.get(i+1).get(0)+messwerte.get(i+2).get(0));

            }
        return ((messwerte.get(i).get(1)-messwerte.get(i).get(0))/3)*sum;
    }

}
