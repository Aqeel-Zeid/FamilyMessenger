package com.example.familymessenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.familymessenger.model.Alert;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddAlert extends AppCompatActivity {


    EditText edit_text_title ;
    EditText edit_text_description ;
    NumberPicker number_picker_priority;
    Button button_add;

    FirebaseFirestore db;
    Alert mAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alert);

        db = FirebaseFirestore.getInstance();

        edit_text_title = findViewById(R.id.edit_text_AddAlert_add_note_title);
        edit_text_description = findViewById(R.id.edit_text_AddAlert_add_note_description);
        number_picker_priority = findViewById(R.id.number_picker_AddAlert_priority_number);
        button_add = findViewById(R.id.button_AlertMain_add_alert);

        number_picker_priority.setMinValue(1);
        number_picker_priority.setMaxValue(10);


        button_add.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = edit_text_title.getText().toString();
                        String description = edit_text_description.getText().toString();
                        Integer priority = number_picker_priority.getValue();

                        if(!title.isEmpty() & !description.isEmpty())
                        {
                            SharedPreferences prefs = getSharedPreferences(Keys.SHARED_PREF_LOGGED_IN_MEMBER, MODE_PRIVATE);
                            String familyname = prefs.getString(Keys.SHARED_PREF_FAMILY_NAME, "No name defined");//"No name defined" is the default value.
                            final String memberName = prefs.getString(Keys.SHARED_PREF_MEMBER_NAME, "No name defined");//"No name defined" is the default value.

                            mAlert = new Alert(title,description,familyname,memberName,priority);


                            CollectionReference dref = db.collection("Families").document(familyname).collection("Alerts");
                            dref.add(mAlert).addOnSuccessListener(
                                    new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(AddAlert.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(),AlertMain.class);
                                            startActivity(intent);
                                        }
                                    }
                            );
                        }
                        else {
                            Toast.makeText(AddAlert.this, "Please Fill In The Required Fields", Toast.LENGTH_SHORT).show();
                        }





                        //Toast.makeText(AddAlert.this, "" + title + " " + description + " " + priority, Toast.LENGTH_SHORT).show();

                    }
                }
        );




    }
}
