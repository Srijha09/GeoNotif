package edu.northeastern.numadsp23_team20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Constraints;

import android.os.Bundle;
import android.util.LayoutDirection;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class StickItToEm extends AppCompatActivity implements SelectStickerDialog.SelectStickerListener {

    SelectStickerDialog selectStickerDialog;
    LinearLayout linearChatLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stick_it_to_em);
        this.linearChatLayout = findViewById(R.id.LinearChatLayout);
    }

    public void onSendStickerButtonClick(View view) {
        this.selectStickerDialog = new SelectStickerDialog();
        this.selectStickerDialog.show(getSupportFragmentManager(), "SelectStickerDialog");
    }

    @Override
    public void applyInformation(String selectedStickerName, int selectedStickerId) {
        this.selectStickerDialog.dismiss();
        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 200
        );
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(this.getDrawable(selectedStickerName));
        LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(
                200, 200
        );
        imageView.setLayoutParams(imageViewParams);
        linearLayout.addView(imageView);
        linearLayout.setGravity(Gravity.END);
        linearLayout.setLayoutParams(linearLayoutParams);
        this.linearChatLayout.addView(linearLayout);
    }

    private int getDrawable(String sticker) {
        switch (sticker) {
            case "pikachu":
                return R.drawable.pikachu;
            case "mewtwo":
                return R.drawable.mewtwo;
            case "charmander":
                return R.drawable.charmander;
            case "squirtle":
                return R.drawable.squirtle;
            case "groudon":
                return R.drawable.groudon;
            case "jolteon":
                return R.drawable.jolteon;
        }
        return 0;
    }
}