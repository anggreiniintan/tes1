package com.example.pdrtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanCode extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
    }

    @Override
    public void handleResult(Result result) {
        Scann.resulttextView.setText(result.getText());
        onBackPressed();
        if(Scann.resulttextView.getText().toString().equals("D205") ||
                Scann.resulttextView.getText().toString().equals("D201") ||
                Scann.resulttextView.getText().toString().equals("D208")||
                Scann.resulttextView.getText().toString().equals("D209")||
                Scann.resulttextView.getText().toString().equals("D212")||
                Scann.resulttextView.getText().toString().equals("D213")||
                Scann.resulttextView.getText().toString().equals("D214")||
                Scann.resulttextView.getText().toString().equals("D215")||
                Scann.resulttextView.getText().toString().equals("Toilet")){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.startCamera();
        scannerView.setResultHandler(this);

    }
}
