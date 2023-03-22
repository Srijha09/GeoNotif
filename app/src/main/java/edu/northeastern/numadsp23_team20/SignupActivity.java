package edu.northeastern.numadsp23_team20;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Techpass Master on 01-Jul-20.
 * www.techpassmaster.com
 */
public class SignupActivity extends AppCompatActivity {
    private EditText edit_txt_Fullname, edit_txt_Username, edit_txt_Email, edit_txt_Pass;
    private Button button_register;
    private TextView text_view_login;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    String fullname, username, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        edit_txt_Fullname = findViewById(R.id.edit_txt_Fullname);
        edit_txt_Username = findViewById(R.id.edit_txt_Username);
        edit_txt_Email = findViewById(R.id.edit_txt_Email);
        edit_txt_Pass = findViewById(R.id.edit_txt_Pass);
        text_view_login = findViewById(R.id.text_view_login);
        button_register = findViewById(R.id.button_register);
        //        Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("GeoNotif");
        text_view_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        //        handle user SignUp button
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateFullname() | !validateUsername() | !validateEmail() | !validatePassword()) {
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener
                        (new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    User data = new User(fullname, username, email);
                                    FirebaseDatabase.getInstance().getReference("GeoNotif")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(data).
                                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(SignupActivity.this, "Successful Registered", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                } else {
                                    //    progressbar GONE
                                    System.out.println(task.getException().getLocalizedMessage());
                                    Toast.makeText(SignupActivity.this, "Check Email id or Password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private boolean validateFullname() {
        fullname = edit_txt_Fullname.getText().toString().trim();
        if (TextUtils.isEmpty(fullname)) {
            Toast.makeText(SignupActivity.this, "Enter Your Full Name", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateUsername() {
        username = edit_txt_Username.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(SignupActivity.this, "Enter Your User Name", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateEmail() {
        email = edit_txt_Email.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(SignupActivity.this, "Enter Your Email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(SignupActivity.this, "Please enter valid Email", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePassword() {
        password = edit_txt_Pass.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(SignupActivity.this, "Enter Your Password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() <= 6) {
            Toast.makeText(SignupActivity.this, "Password is Very Short", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    //    if the user already logged in then it will automatically send on Dashboard/MainActivity activity.
    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}