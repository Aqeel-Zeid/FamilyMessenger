package com.example.familymessenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familymessenger.adapters.MemberAdapter;
import com.example.familymessenger.model.Family;
import com.example.familymessenger.model.Member;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.PriorityQueue;

public class FamilyMainMenu extends AppCompatActivity implements  MemberAdapter.OnItemClickListner {

    private Family mFamily;
    private EditText edit_text_old_password;
    private EditText edit_text_new_password;
    private TextView textView_family_name;
    private Button button_reset_password;
    private Button button_add_member;

    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private MemberAdapter mMemberAdapter;
    private RecyclerView recycler_view_container;
    private CollectionReference membersRef ;


    @Override
    protected void onStart() {
        setUpRecyclerView();
        super.onStart();
        mMemberAdapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMemberAdapter.startListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mMemberAdapter.stopListening();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_main_menu);

        Bundle data = getIntent().getExtras();
        mFamily = (Family) data.getParcelable(FamilyManager.INTENT_EXTRA_LOGIN_FAMILY_OBJECT);
        //Toast.makeText(this, "" + mFamily.getFamilyName(), Toast.LENGTH_SHORT).show();

        membersRef = db.collection("Families").document(mFamily.getFamilyName()).collection("Members");

        edit_text_new_password  = findViewById(R.id.edit_text_FamilyMainMenu_new_password);
        edit_text_old_password  = findViewById(R.id.edit_text_FamilyMainMenu_old_password);
        button_reset_password = findViewById(R.id.button_FamilyMainMenu_reset_password);
        button_add_member = findViewById(R.id.button_FamilyMainMenu_add_member);
        textView_family_name = findViewById(R.id.text_view_FamilyMainMenu_family_name);

        textView_family_name.setText(mFamily.getFamilyName());

        button_add_member.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), CreateMember.class);
                        intent.putExtra(FamilyManager.INTENT_EXTRA_LOGIN_FAMILY_OBJECT, mFamily);
                        startActivity(intent);
                    }
                }
        );

        button_reset_password.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String newPassword = edit_text_new_password.getText().toString().trim();
                        String oldPassword = edit_text_old_password.getText().toString().trim();

                        if( !oldPassword.contentEquals(mFamily.getFamilyPassword()) )
                        {
                            Toast.makeText(FamilyMainMenu.this, "Old Password Does Not Match", Toast.LENGTH_SHORT).show();
                        }
                        else {


                            DocumentReference docRef = db.collection("Families").document(mFamily.getFamilyName());
                            docRef.set(new Family(mFamily.getFamilyName(), newPassword)).addOnSuccessListener(
                                    new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(FamilyMainMenu.this, "Password Updated Successfully!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                            );

                        }

                    }
                }
        );









    }
    //Setting Up Recycler View
    private void setUpRecyclerView() {
        Query query = membersRef;
        FirestoreRecyclerOptions<Member> options = new FirestoreRecyclerOptions.Builder<Member>().setQuery(query , Member.class).build();

        mMemberAdapter = new MemberAdapter(options);
        recycler_view_container = findViewById(R.id.recycler_view_FamilyMainMenu_members_container);
        recycler_view_container.setHasFixedSize(true);
        recycler_view_container.setLayoutManager(new LinearLayoutManager(this));
        recycler_view_container.setAdapter(mMemberAdapter);

        mMemberAdapter.setOnItemClickListner(
                new MemberAdapter.OnItemClickListner() {
                    @Override
                    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                        Member myMember = documentSnapshot.toObject(Member.class);
                        //Toast.makeText(FamilyMainMenu.this, "" + myMember.getName(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), FamilyMemberUpdateView.class);
                        intent.putExtra(Keys.EXTRA_MEMBER_OBJECT, myMember);
                        intent.putExtra(Keys.EXTRA_FAMILY_OBJECT, mFamily);



                        startActivity(intent);
                    }
                }
        );


    }

    @Override
    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

    }
}
