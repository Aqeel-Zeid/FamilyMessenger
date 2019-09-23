package com.example.familymessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.familymessenger.model.Family;
import com.example.familymessenger.model.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainMenu extends AppCompatActivity {

    TextView text_view_member_name;
    TextView text_view_family_name;
    ImageView image_view_profile_pic;
    Button button_conversations;
    Button button_alerts;
    Button button_log_out;

    private StorageReference mStorageRef;

    Member mMember;
    Family mFamily;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        button_alerts = findViewById(R.id.button_MainMenu_main_alerts);
        button_log_out = findViewById(R.id.button_MainMenu_main_log_out);

        button_alerts.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), AlertMain.class);
                        startActivity(intent);

                    }
                }
        );

        button_log_out.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences prefs = getSharedPreferences(Keys.SHARED_PREF_LOGGED_IN_MEMBER, MODE_PRIVATE);
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.clear();
                        edit.commit();

                        finish();
                    }
                }
        );

        text_view_member_name = findViewById(R.id.text_view_MainMenu_member_name);
        text_view_family_name = findViewById(R.id.text_view_MainMenu_family_name);
        image_view_profile_pic = findViewById(R.id.image_view_MainMenu_profile_pic);

        SharedPreferences prefs = getSharedPreferences(Keys.SHARED_PREF_LOGGED_IN_MEMBER, MODE_PRIVATE);
        String familyname = prefs.getString(Keys.SHARED_PREF_FAMILY_NAME, "No name defined");//"No name defined" is the default value.
        final String memberName = prefs.getString(Keys.SHARED_PREF_MEMBER_NAME, "No name defined");//"No name defined" is the default value.

        Toast.makeText(this, familyname + " " + memberName, Toast.LENGTH_SHORT).show();

        db = FirebaseFirestore.getInstance();

        DocumentReference dref = db.collection("Families").document(familyname.trim()).collection("Members").document(memberName.trim());

        dref.get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        //mMember = documentSnapshot.toObject(Member.class);
                        //Toast.makeText(MainMenu.this, "Failed loading Data", Toast.LENGTH_SHORT).show();
                    }
                }
        ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainMenu.this, "Failed loading Data", Toast.LENGTH_SHORT).show();
                    }
                }
        ).addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();

                            if (documentSnapshot.exists()) {
                                mMember = documentSnapshot.toObject(Member.class);
                                //Toast.makeText(MainMenu.this, "loading Data Success " + mMember.getName() , Toast.LENGTH_SHORT).show();
                                if (mMember.getProfileImage() != null) {
                                    StorageReference pathReference = mStorageRef.child(  mMember.getProfileImage());
                                    //Log.d("FBASEIMG" ,"images/" + model.getProfileImage() );

                                    pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Toast.makeText(MainMenu.this, "Loaded Image", Toast.LENGTH_SHORT).show();
                                            Glide.with(getApplicationContext()).load(uri).into(image_view_profile_pic);

                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(MainMenu.this, "loading Data Failed " + documentSnapshot.get("name"), Toast.LENGTH_SHORT).show();
                            }


                        }
                    }
                }
        );


        mStorageRef = FirebaseStorage.getInstance().getReference();


        text_view_family_name.setText(familyname);
        text_view_member_name.setText(memberName);


        //Button button_conversations.;


    }
}
