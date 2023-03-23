package edu.northeastern.numadsp23_team20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ChooseChat extends AppCompatActivity {
    MyAdapter myAdapter;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    String currUsername;
    Intent intent;
    private String[] titles= new String[]{"Chats","Stickers used"};
    private FirebaseDatabase mDatabase;
    ValueEventListener valueEventListener;
    private HashMap<String, HashMap<String, String>> prevMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_chat);
        createNotificationChannel();
        tabLayout = findViewById(R.id.tabs);
        viewPager2 = findViewById(R.id.view_pager);
        myAdapter = new MyAdapter(this);
        viewPager2.setAdapter(myAdapter);
        new TabLayoutMediator(tabLayout,viewPager2,((tab, position) -> tab.setText(titles[position]))).attach();
        intent = getIntent();
        currUsername = intent.getStringExtra("username");
        mDatabase = FirebaseDatabase.getInstance();

        this.valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, HashMap<String, HashMap<String, String>>> childSnapshot;
                if (snapshot.getValue() != null) {
                    childSnapshot = (HashMap<String, HashMap<String, HashMap<String, String>>>) snapshot.getValue();
                    HashMap<String, HashMap<String, String>> messages = new HashMap<>();
                    if (childSnapshot.size() > 0) {
                        for (Map.Entry<String, HashMap<String, HashMap<String, String>>> childSnapshotItem : childSnapshot.entrySet()) {
                            messages.putAll(childSnapshotItem.getValue());
                        }
                        if (prevMessages == null) {
                            prevMessages = messages;
                        } else {
                            Set<String> prevMessagesSet;
                            Set<String> newMessagesSet;
                            prevMessagesSet = prevMessages.keySet();
                            newMessagesSet = messages.keySet();
                            newMessagesSet.removeAll(prevMessagesSet);
                            prevMessages = messages;
                            String key;
                            String[] newMessagesArray = newMessagesSet.toArray(new String[newMessagesSet.size()]);
                            key = newMessagesArray[0];
                            String stickerName = messages.get(key).get("stickerName");
                            String timeStamp = messages.get(key).get("timestamp");
                            String sentBy = messages.get(key).get("userId");
                            if (!sentBy.equals(currUsername)) {
                                sendNotification(sentBy, timeStamp, stickerName);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDatabase.getReference().child("Users/" + currUsername + "/messages").addValueEventListener(this.valueEventListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.getReference().child("Users/" + currUsername + "/messages").removeEventListener(this.valueEventListener);
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public Bundle getMyData() {
        Bundle bundle = new Bundle();
        bundle.putString("current_user", currUsername);
        return bundle;
    }

    public String getRecentNode(HashMap<String, HashMap<String, String>> hash_map) throws ParseException {
        List<String> keys = new ArrayList<>(hash_map.keySet());
        String currentKey = null;
        long lowestDifferece = Long.MAX_VALUE;

        DateFormat formatter = new SimpleDateFormat("hh:mm a");
        String timeNow = formatter.format(new Date());

        for (int i = 0; i < keys.size(); i++) {
            String time = hash_map.get(keys.get(i)).get("timestamp");
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(timeNow);

            long difference = date2.getTime() - date1.getTime();
            if (difference < lowestDifferece) {
                lowestDifferece = difference;
                currentKey = keys.get(i);
            }
        }
        return currentKey;
    }

    public void sendNotification(String sentBy, String timeStamp, String stickerName) {

        int id = getResources().getIdentifier("edu.northeastern.numadsp23_team20:drawable/" + stickerName, null, null);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), id);

        String channelId = getString(R.string.channel_id);
        NotificationCompat.Builder notifyBuild = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("New sticker from " + sentBy + " at: " + timeStamp)
                .setContentText("You received a new " + stickerName + " sticker!")
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(icon))
                .setSmallIcon(R.drawable.placeholder_image)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int idUnique = createID();
        notificationManager.notify(idUnique, notifyBuild.build());
    }

    public int createID() {
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.US).format(now));
        return id;
    }
}