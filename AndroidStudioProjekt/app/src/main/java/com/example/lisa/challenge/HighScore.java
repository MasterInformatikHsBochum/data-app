package com.example.lisa.challenge;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Context;

import java.util.Vector;

public class HighScore {
    public float HS_1;
    public float HS_2;
    public float HS_3;

    public float WS_1;
    public float  WS_2;
    public float WS_3;

    public float R_1;
    public float R_2;
    public float R_3;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    HighScore(Activity activity){
        sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        HS_1 = sharedPref.getFloat("HS_1",0);
        HS_2 = sharedPref.getFloat("HS_2",0);
        HS_3 = sharedPref.getFloat("HS_3",0);

        WS_1 = sharedPref.getFloat("WS_1",0);
        WS_2 = sharedPref.getFloat("WS_2",0);
        WS_3 = sharedPref.getFloat("WS_3",0);

        R_1 = sharedPref.getFloat("R_1",0);
        R_2 = sharedPref.getFloat("R_2",0);
        R_3 = sharedPref.getFloat("R_3",0);
    }
    public float[] get_HS_score(){
        return new  float[]{HS_1,HS_2,HS_3};
    }
    public float[] get_WS_score(){
        return new float  []{WS_1,WS_2,WS_3};
    }
    public float[] get_R_score(){
        return new float[]{R_1,R_2,R_3};
    }
    public void new_HS_score(float wert){
        if (wert > HS_3){
            if (wert >= HS_2){
                if(wert >= HS_1){
                    HS_1=wert;
                    editor.putFloat("HS_1",HS_1);
                }
                else{
                    HS_2=wert;
                    editor.putFloat("HS_2",HS_2);
                }
            }
            else{
                HS_3=wert;
                editor.putFloat("HS_3",HS_3);
            }
        }
        editor.apply();
    }
    public void new_WS_score(float wert){
        if (wert > WS_3){
            if (wert >= WS_2){
                if(wert >= WS_1){
                    WS_1=wert;
                    editor.putFloat("WS_1",WS_1);
                }
                else{
                    WS_2=wert;
                    editor.putFloat("WS_2",WS_2);
                }
            }
            else{
                WS_3=wert;
                editor.putFloat("WS_3",WS_3);
            }
        }
        editor.apply();
    }
    public void new_R_score(float  wert){
        if (wert > R_3){
            if (wert >= R_2){
                if(wert >= R_1){
                    R_1=wert;
                    editor.putFloat("R_1",R_1);
                }
                else{
                    R_2=wert;
                    editor.putFloat("R_2",R_2);
                }
            }
            else{
                R_3=wert;
                editor.putFloat("R_3",R_3);
            }
        }
        editor.apply();
    }
    public void clear_all(){
        editor.clear();
        HS_1 = 0;
        HS_2 = 0;
        HS_3 = 0;

        WS_1 = 0;
        WS_2 = 0;
        WS_3 = 0;

        R_1 = 0;
        R_2 = 0;
        R_3 = 0;
        editor.apply();
    }

}
