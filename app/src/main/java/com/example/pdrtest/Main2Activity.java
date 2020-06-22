package com.example.pdrtest;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {
    String x,y;
    TextView textViewContent2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String ruangan = intent.getStringExtra("ruangan");
        x = "0";
        y = "0";
        if(ruangan.equals("D201")){
            x="120";
            y = "120";
        }
        else{
            x = "-99";
            y = "-99";
        }
        textViewContent2 = findViewById(R.id.textViewContent2);
        textViewContent2.setText("X : "+x+" Y "+y);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "X : "+x+" Y "+y, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
