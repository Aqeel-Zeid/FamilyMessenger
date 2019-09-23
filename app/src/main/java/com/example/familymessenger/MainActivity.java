package com.example.familymessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.familymessenger.model.Family;
import com.example.familymessenger.model.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private EditText edit_text_family_name ;
    private  EditText edit_text_member_name;
    private EditText edit_text_password;
    private Button button_login;
    private Button button_family_signUp;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_text_family_name = findViewById(R.id.edit_text_MainActivity_family_name);
        edit_text_member_name = findViewById(R.id.edit_text_MainActivity_member_name);
        edit_text_password = findViewById(R.id.edit_text_MainActivity_password);


        SharedPreferences prefs = getSharedPreferences(Keys.SHARED_PREF_LOGGED_IN_MEMBER, MODE_PRIVATE);

        if(prefs.contains(Keys.SHARED_PREF_MEMBER_NAME))
        {
            String familyname = prefs.getString(Keys.SHARED_PREF_FAMILY_NAME, "No name defined");//"No name defined" is the default value.
            final String memberName = prefs.getString(Keys.SHARED_PREF_MEMBER_NAME, "No name defined");//"No name defined" is the default value.

            Intent intent = new Intent(getApplicationContext(), MainMenu.class);
            startActivity(intent);


        }


        button_login = findViewById(R.id.button_MainActivity_login);

        button_login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                        public void onClick(View v) {

                            final String mMembername = edit_text_member_name.getText().toString().trim();
                            final String mFamilyName = edit_text_family_name.getText().toString().trim();
                            final String mPassword = edit_text_password.getText().toString().trim();


                            db = FirebaseFirestore.getInstance();

                            DocumentReference dref =  db.collection("Families").document(mFamilyName).collection("Members").document(mMembername);
                            dref.get().addOnCompleteListener(
                                    new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                if (documentSnapshot.exists()) {

                                                    Member mMember = documentSnapshot.toObject(Member.class);

                                                    if(mPassword.contentEquals(mMember.getPassword()))
                                                    {
                                                        SharedPreferences.Editor editor = getSharedPreferences(Keys.SHARED_PREF_LOGGED_IN_MEMBER, MODE_PRIVATE).edit();
                                                        editor.putString( Keys.SHARED_PREF_FAMILY_NAME , mFamilyName);
                                                        editor.putString( Keys.SHARED_PREF_MEMBER_NAME , mMembername);
                                                        editor.apply();

                                                        Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                                                        startActivity(intent);

                                                    }
                                                    else {
                                                        Toast.makeText(MainActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                                                    }


                                                } else {

                                                    Toast.makeText(MainActivity.this, "Invaid UserName Or Family name", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        }
                                    }
                            );





                        }
                }
        );

        button_family_signUp = (Button) findViewById(R.id.button_MainActivity_family_sign_up);
        button_family_signUp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent  = new Intent(getApplicationContext(), FamilyManager.class);
                        startActivity(intent);
                    }
                }
        );

    }



}
