package edu.northeastern.numadsp23_team20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class AddMembersList extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private AddMemberAdapter memberAdapter;
    private ArrayList<User> memberList;
    private String currentuser;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private final String TAG="AddMembersList";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members_list);

        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //filterList(newText);
                return true;
            }
        });
        recyclerView = findViewById(R.id.members_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        this.memberList = new ArrayList<>();
        User user1 = new User("Rutu");
        User user2 = new User("Rahul");
        memberList.add(user1);
        memberList.add(user2);
        memberAdapter = new AddMemberAdapter(this.memberList, getApplicationContext());
        recyclerView.setAdapter(memberAdapter);
    }

//    public void filterList(String text){
//        ArrayList<User> filteredList = new ArrayList<>();
//        for(User user: memberList){
//            if(user.getUsername().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))){
//                filteredList.add(user);
//            }
//        }
//
//        if (filteredList.isEmpty()){
//            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
//        }else{
//            memberAdapter.setFilteredList(filteredList);
//        }
//    }

}