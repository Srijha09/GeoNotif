package edu.northeastern.numadsp23_team20;

import androidx.annotation.NonNull;
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
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StickItToEm extends AppCompatActivity implements SelectStickerDialog.SelectStickerListener {

    SelectStickerDialog selectStickerDialog;
    ScrollView scrollableChatContainer;
    LinearLayout linearChatLayout;
    List<Message> history;
    private FirebaseDatabase mDatabase;

    String chosenUser = "user3";
    String username = "user1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stick_it_to_em);
        this.linearChatLayout = findViewById(R.id.LinearChatLayout);
        this.scrollableChatContainer = findViewById(R.id.ScrollableChatContainer);
        this.history = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance();

        mDatabase.getReference().child("Users/" + username + "/messages/" + chosenUser).addChildEventListener(
                new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        showMessage(dataSnapshot);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                        showMessage(dataSnapshot);
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        showMessage(dataSnapshot);
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                        showMessage(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext()
                                , "DBError: " + databaseError, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public void onSendStickerButtonClick(View view) {
        this.selectStickerDialog = new SelectStickerDialog();
        this.selectStickerDialog.show(getSupportFragmentManager(), "SelectStickerDialog");
    }

    @Override
    public void applyInformation(String selectedStickerName, int selectedStickerId) {
        this.selectStickerDialog.dismiss();
        this.onSendSticker(mDatabase.getReference(), selectedStickerName);
    }

    private void onSendSticker(DatabaseReference postRef, String stickerName) {
        postRef
                .child("Users")
                .child(this.username)
                .child("messages").child(this.chosenUser).push().setValue(new Message(StickItToEm.this.username, stickerName, getTime()));
        postRef
                .child("Users")
                .child(this.chosenUser)
                .child("messages").child(this.username).push().setValue(new Message(StickItToEm.this.username, stickerName, getTime()));
        postRef
                .child("Users")
                .child(this.username)
                .child("stickerCount")
                .child(stickerName).setValue(ServerValue.increment(1));
    }

    private String getTime() {
        Calendar calendar = Calendar.getInstance();
        int hour12hrs = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);
        String time = String.format("%02d", Integer.valueOf(hour12hrs))
                .concat(":")
                .concat(String.format("%02d", Integer.valueOf(minutes)));
        if (calendar.get(Calendar.AM_PM) == Calendar.AM)
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
        linearLayout.setPadding(10, 10, 10, 10);
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(this.getDrawable(message.getStickerName()));
        LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(
                300, 300
        );
        imageView.setPadding(20, 20, 20, 20);
        imageView.setLayoutParams(imageViewParams);
        linearLayout.addView(imageView);
        TextView textView = new TextView(this);
        textView.setPadding(20, 0, 20, 0);
        textView.setText(getTime());
        textView.setTextColor(Color.GRAY);
        textView.setTextSize(10);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                300, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(textViewParams);
        linearLayout.addView(textView);
        if (message.getUserId().equalsIgnoreCase(username)) {
            linearLayout.setGravity(Gravity.END);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        } else {
            linearLayout.setGravity(Gravity.START);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }
        linearLayout.setLayoutParams(linearLayoutParams);
        this.linearChatLayout.addView(linearLayout);
        this.scrollableChatContainer.post(() -> scrollableChatContainer.fullScroll(ScrollView.FOCUS_DOWN));
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

    private void showMessage(DataSnapshot dataSnapshot) {
        Message message = dataSnapshot.getValue(Message.class);

        if (dataSnapshot.getKey() != null) {
            this.addToChatWindow(message);
            System.out.println("Hooray");
        }
    }
}