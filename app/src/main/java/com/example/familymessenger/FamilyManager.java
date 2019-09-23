package com.example.familymessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.familymessenger.model.Family;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

public class FamilyManager extends AppCompatActivity {

    public static final String INTENT_EXTRA_LOGIN_FAMILY_OBJECT = "INTENT_EXTRA_LOGIN_FAMILY_OBJECT";

    private Button button_create_family ;
    private EditText edit_text_family_name;
    private EditText edit_text_password;
    private Button edit_text_Login;
    private Button button_login_Button;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_manager);

        button_create_family = (Button) findViewById(R.id.button_FamilyManager_family_sign_up);
        button_login_Button = findViewById(R.id.button_FamilyManager_login);
        edit_text_family_name = findViewById(R.id.edit_text_FamilyManager_family_name);
        edit_text_password = findViewById(R.id.edit_text_FamilyManager_password);

        button_login_Button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String familyName = edit_text_family_name.getText().toString().trim();
                        final String password = edit_text_password.getText().toString().trim();

                        DocumentReference docRef = db.collection("Families").document(familyName);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {

                                        Family mFamily = document.toObject(Family.class);

                                        if(mFamily.getFamilyPassword().contentEquals(password))
                                        {
                                            Toast.makeText(FamilyManager.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), FamilyMainMenu.class);
                                            intent.putExtra(INTENT_EXTRA_LOGIN_FAMILY_OBJECT, mFamily);

                                            SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
                                            SharedPreferences.Editor prefsEditor = mPrefs.edit();

                                            Gson gson = new Gson();
                                            String json = gson.toJson(mFamily);
                                            prefsEditor.putString(Keys.SHARED_PREF_FAMILY_OBJECT, json);
                                            prefsEditor.commit();

                                            startActivity(intent);


                                        }else{
                                            Toast.makeText(FamilyManager.this, "Incorrect Password ", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                        Toast.makeText(FamilyManager.this, "Incorrect Password Or Family Name ", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(FamilyManager.this, "Incorrect Password Or Family Name ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
        );

        button_create_family.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(getApplicationContext(), CreateFamily.class);
                startActivity(intent);
            }
        });

    }

}
