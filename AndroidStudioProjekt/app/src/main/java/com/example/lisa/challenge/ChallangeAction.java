package com.example.lisa.challenge;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.CountDownTimer;
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

        if(max<startValue)
        {
            return 0;
        }
        return startValue-max;
    }

    /**
     * Startet einen Countdown und gibt jede Sekunge einen Sound wieder
     * @param seconds Zu wartende Zeit in Sekunden
     */
    public void startCountdown(int seconds)
    {

        new CountDownTimer(seconds*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.d("runnable","Countdown läuft");
                sound.startTone(ToneGenerator.TONE_PROP_BEEP);

            }

            public void onFinish() {
                Log.d("runnable","Countdown beendet");
                sound.startTone(ToneGenerator.TONE_PROP_BEEP2);
            }


        }.start();
    }


}
