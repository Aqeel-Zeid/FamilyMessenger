package com.example.familymessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.familymessenger.model.Family;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateFamily extends AppCompatActivity {

    private EditText edit_text_family_name ;
    private EditText edit_text_password ;
    private EditText edit_text_password2;
    private Button button_create_family;
    CoordinatorLayout coordinatorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_family);

        edit_text_family_name = (EditText) findViewById(R.id.edit_text_CreateFamily_familyName);
        edit_text_password = (EditText) findViewById(R.id.edit_text_CreateFamily_password);
        edit_text_password2 = (EditText) findViewById(R.id.edit_text_CreateFamily_reEnterPassword);
        button_create_family = findViewById(R.id.button_CreateFamily_CreateFamily);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();


        button_create_family.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edit_text_family_name.getText().toString().isEmpty() || edit_text_password.getText().toString().isEmpty() || !edit_text_password.getText().toString().equals(edit_text_password2.getText().toString()))
                {
                    Toast.makeText(CreateFamily.this, "Password Missmatch Or Empty Fields Present", Toast.LENGTH_SHORT).show();
                }else {
                    DocumentReference docRef = db.collection("Families").document(edit_text_family_name.getText().toString().trim());
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Toast.makeText(CreateFamily.this, "Family Name Is Already Taken Please Try Adding A Number In Front ", Toast.LENGTH_SHORT).show();
                                } else {



                                    DocumentReference docRef = db.collection("Families").document(edit_text_family_name.getText().toString().trim());
                                    docRef.set(new Family(edit_text_family_name.getText().toString().trim(),edit_text_password.getText().toString().trim() )).addOnSuccessListener(
                                            new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(CreateFamily.this, "Family Creation Successful ", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), FamilyManager.class);
                                                }
                                            }
                                    );

                                }
                            } else {

                            }
                        }
                    });
                }
            }
        });



    }

}
