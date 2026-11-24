package com.dsi3.isetkb.tp4application;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AcceuilAct extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView text = new TextView(this);
        text.setText("Bienvenue ! Connexion réussie.");
        text.setTextSize(24);
        text.setPadding(30, 100, 30, 30);

        setContentView(text);
    }
}