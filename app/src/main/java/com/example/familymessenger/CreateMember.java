package com.example.familymessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.familymessenger.model.Family;
import com.example.familymessenger.model.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.model.Document;
import com.google.gson.Gson;

import static android.Manifest.permission.SEND_SMS;

public class CreateMember extends AppCompatActivity {

    Family mFamily;
    Member member;
    private EditText edit_text_name;
    private EditText edit_text_nick_name;
    private EditText edit_text_password;
    private EditText edit_text_phone_number;
    private Button button_add_member;
    private Button button_send_sms;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_member);

        Bundle data = getIntent().getExtras();
        mFamily = (Family) data.getParcelable(FamilyManager.INTENT_EXTRA_LOGIN_FAMILY_OBJECT);


        button_send_sms = findViewById(R.id.button_CreateMember_send_sms);
        button_add_member = findViewById(R.id.button_CreateMember_add_member);
        button_send_sms.setEnabled(false);

        edit_text_name = findViewById(R.id.edit_text_CreateMember_name);
        edit_text_nick_name = findViewById(R.id.edit_text_CreateMember_nick_name);

        edit_text_password = findViewById(R.id.edit_text_CreateMember_password);
        edit_text_phone_number = findViewById(R.id.edit_text_CreateMember_phone_number);


        button_add_member.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final String name = edit_text_name.getText().toString().trim();
                        final String nickName = edit_text_nick_name.getText().toString().trim();
                        final String password = edit_text_password.getText().toString().trim();
                        final String phoneNumber = edit_text_phone_number.getText().toString().trim();


                        final DocumentReference documentReference = db.collection("Families").document(mFamily.getFamilyName()).collection("Members").document(name);
                        documentReference.get().addOnCompleteListener(
                                new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            if (documentSnapshot.exists()) {
                                                Toast.makeText(CreateMember.this, "Try Another Member Name", Toast.LENGTH_SHORT).show();
                                            } else {

                                                final Member mMember = new Member(name, nickName, password, phoneNumber);
                                                documentReference.set(mMember).addOnSuccessListener(
                                                        new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(CreateMember.this, "Member Added Successfully", Toast.LENGTH_SHORT).show();
                                                                member = mMember;
                                                                button_send_sms.setEnabled(true);
                                                            }
                                                        }
                                                );

                                            }
                                        }
                                    }
                                }
                        );
                    }
                }
        );

        button_send_sms.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String msgBody = "You Have Been Invited To Family Chat from A member. Here Are The Login Credentials , " +
                                "family Name : " + mFamily.getFamilyName() + "member name : " + member.getName() +
                                "temporory password : " + member.getPassword();

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("smsto:" + member.getPhone()));
                        intent.putExtra("sms_body" , msgBody);

                        startActivity(intent);
                        finish();
                        
                    }
                }
        );

    }





}




