package edu.northeastern.numadsp23_team20;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StickItToEm extends AppCompatActivity implements SelectStickerDialog.SelectStickerListener {

    SelectStickerDialog selectStickerDialog;
    ScrollView scrollableChatContainer;
    LinearLayout linearChatLayout;
    List<Message> history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stick_it_to_em);
        this.linearChatLayout = findViewById(R.id.LinearChatLayout);
        this.scrollableChatContainer = findViewById(R.id.ScrollableChatContainer);
        this.history = new ArrayList<>();
        Message m1 = new Message(2, "pikachu");
        this.history.add(m1);
        Message m2 = new Message(2, "mewtwo");
        this.history.add(m2);
        Message m3 = new Message(1, "charmander");
        this.history.add(m3);
        Message m4 = new Message(1, "squirtle");
        this.history.add(m4);
        Message m5 = new Message(2, "groudon");
        this.history.add(m5);
        Message m6 = new Message(1, "jolteon");
        this.history.add(m6);
        for (Message message: this.history) {
            this.addToChatWindow(message);
        }
    }

    public void onSendStickerButtonClick(View view) {
        this.selectStickerDialog = new SelectStickerDialog();
        this.selectStickerDialog.show(getSupportFragmentManager(), "SelectStickerDialog");
    }

    @Override
    public void applyInformation(String selectedStickerName, int selectedStickerId) {
        this.selectStickerDialog.dismiss();
        this.addToChatWindow(new Message(1, selectedStickerName));
    }

    private String getTime() {
        Calendar calendar = Calendar.getInstance();
        int hour12hrs = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);
        String time = String.format("%02d", Integer.valueOf(hour12hrs))
                .concat(":")
                .concat(String.format("%02d", Integer.valueOf(minutes)));
        if(calendar.get(Calendar.AM_PM) == Calendar.AM)
            time += " AM";
        else {
            time += " PM";
        }
        return time;
    }

    private void addToChatWindow(Message message) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        linearLayout.setPadding(10,10,10,10);
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(this.getDrawable(message.getStickerName()));
        LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(
                300,  300
        );
        imageView.setPadding(20,20,20,20);
        imageView.setLayoutParams(imageViewParams);
        linearLayout.addView(imageView);
        TextView textView = new TextView(this);
        textView.setPadding(20,0,20,0);
        textView.setText(getTime());
        textView.setTextColor(Color.GRAY);
        textView.setTextSize(10);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                300,  ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(textViewParams);
        linearLayout.addView(textView);
        if (message.getUserId() == 1) {
            linearLayout.setGravity(Gravity.END);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        } else {
            linearLayout.setGravity(Gravity.START);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }
        linearLayout.setLayoutParams(linearLayoutParams);
        this.linearChatLayout.addView(linearLayout);
        this.scrollableChatContainer.post(() -> scrollableChatContainer.fullScroll(ScrollView.FOCUS_DOWN));;
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