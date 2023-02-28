package edu.northeastern.numadsp23_team20;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class StickItToEm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stick_it_to_em);
    }

    public void onSendStickerButtonClick(View view) {
        SelectStickerDialog selectStickerDialog = new SelectStickerDialog();
        selectStickerDialog.show(getSupportFragmentManager(), "SelectStickerDialog");
    }
}