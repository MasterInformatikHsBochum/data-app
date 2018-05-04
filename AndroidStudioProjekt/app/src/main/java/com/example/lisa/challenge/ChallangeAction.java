package com.example.lisa.challenge;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.Log;

import java.util.Collections;
import java.util.List;

public class ChallangeAction {

       final ToneGenerator sound = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

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

}
