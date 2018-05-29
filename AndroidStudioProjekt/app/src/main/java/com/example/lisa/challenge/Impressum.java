package com.example.lisa.challenge;

import android.os.Bundle;

public class Impressum extends MyNavigation {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Navigation erzeugen. Die aufrufende Aktivitaet muss fuer die Abstracte Klasse gesetzt werden
        setActivity(Impressum.this, R.layout.impressum);
        super.onCreate(savedInstanceState);
    }
}
