package com.example.familymessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.familymessenger.adapters.AlertAdapter;
import com.example.familymessenger.model.Alert;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AlertMain extends AppCompatActivity implements AlertAdapter.OnItemClickListner{

    Button button_add_alert;


    private AlertAdapter mAlertAdapter;
    private RecyclerView recycler_view_container;
    private CollectionReference alertRef;

    @Override
    protected void onStart() {
        setUpRecyclerView();
        mAlertAdapter.startListening();

        super.onStart();

    }

    private void setUpRecyclerView() {

        Query query = alertRef;
        FirestoreRecyclerOptions<Alert> options = new FirestoreRecyclerOptions.Builder<Alert>().setQuery(
                query , Alert.class
        ).build();

        mAlertAdapter = new AlertAdapter(options);
        recycler_view_container= findViewById(R.id.recycler_view_AlertMain_container);

        recycler_view_container.setHasFixedSize(true);
        recycler_view_container.setLayoutManager(new LinearLayoutManager(this));
        recycler_view_container.setAdapter(mAlertAdapter);

        mAlertAdapter.setOnItemClickListner(
                new AlertAdapter.OnItemClickListner() {
                    @Override
                    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                        Alert alert = documentSnapshot.toObject(Alert.class);
                        Toast.makeText(AlertMain.this, "" + alert.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                SharedPreferences prefs = getSharedPreferences(Keys.SHARED_PREF_LOGGED_IN_MEMBER, MODE_PRIVATE);
                String familyname = prefs.getString(Keys.SHARED_PREF_FAMILY_NAME, "No name defined");//"No name defined" is the default value.
                final String memberName = prefs.getString(Keys.SHARED_PREF_MEMBER_NAME, "No name defined");//"No name defined" is the default value.


                mAlertAdapter.deleteItem(viewHolder.getAdapterPosition(), getApplicationContext() ,memberName );
            }
        }).attachToRecyclerView(recycler_view_container);
        mAlertAdapter.setOnItemClickListner(new AlertAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Alert alert = documentSnapshot.toObject(Alert.class);
                String id = documentSnapshot.getId();
                Toast.makeText(getApplication(), "ID " + id , Toast.LENGTH_SHORT).show();
            }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                SharedPreferences prefs = getSharedPreferences(Keys.SHARED_PREF_LOGGED_IN_MEMBER, MODE_PRIVATE);
                String familyname = prefs.getString(Keys.SHARED_PREF_FAMILY_NAME, "No name defined");//"No name defined" is the default value.
                final String memberName = prefs.getString(Keys.SHARED_PREF_MEMBER_NAME, "No name defined");//"No name defined" is the default value.


                mAlertAdapter.updateItem(viewHolder.getAdapterPosition(), getApplicationContext() ,memberName );
            }
        }).attachToRecyclerView(recycler_view_container);



    }

    @Override
    protected void onResume() {
        super.onResume();
        mAlertAdapter.startListening();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mAlertAdapter.startListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mMemberAdapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_main);

        SharedPreferences prefs = getSharedPreferences(Keys.SHARED_PREF_LOGGED_IN_MEMBER, MODE_PRIVATE);
        String familyname = prefs.getString(Keys.SHARED_PREF_FAMILY_NAME, "No name defined");//"No name defined" is the default value.
        final String memberName = prefs.getString(Keys.SHARED_PREF_MEMBER_NAME, "No name defined");//"No name defined" is the default value.

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        alertRef = db.collection("Families")
                .document(familyname)
                .collection("Alerts");


        button_add_alert = findViewById(R.id.button_AlertMain_add_alert);

        button_add_alert.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), AddAlert.class);
                        startActivity(intent);
                    }
                }
        );

    }


    @Override
    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

    }
}
