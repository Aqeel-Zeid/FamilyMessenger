package com.example.familymessenger.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familymessenger.R;
import com.example.familymessenger.model.Alert;
import com.example.familymessenger.model.Member;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

public class AlertAdapter extends FirestoreRecyclerAdapter<Alert, AlertAdapter.AlertHolder> {


    private Context context;
    private AlertAdapter.OnItemClickListner onItemClickListner;



    public AlertAdapter(@NonNull FirestoreRecyclerOptions<Alert> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AlertHolder holder, int position, @NonNull Alert model) {

        holder.text_view_priority.setText(String.valueOf(model.getPriority()));
        holder.text_view_description.setText(String.valueOf(model.getDescription()));
        holder.text_view_title.setText(String.valueOf(model.getTitle()));

        if(model.getPriority() <= 2 )
        {
            holder.card_view.setCardBackgroundColor(Color.parseColor("#ffcccb"));
        }
        else if(model.getPriority() > 2  &&  model.getPriority() <= 5)
        {
            holder.card_view.setCardBackgroundColor(Color.parseColor("#FFEB3B"));
        }
        else {
            holder.card_view.setCardBackgroundColor(Color.parseColor("#CDDC39"));
        }

    }

    @NonNull
    @Override
    public AlertHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_alert_item, parent , false);
        return new AlertAdapter.AlertHolder(v);
    }

    public void deleteItem(int adapterPosition , final Context context , final String owner) {

        final int adapterP = adapterPosition;

        String path = getSnapshots().getSnapshot(adapterPosition).getReference().getPath();

        FirebaseFirestore db =  FirebaseFirestore.getInstance();

        DocumentReference documentReference = db.document(path);

        documentReference.get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Alert alert = documentSnapshot.toObject(Alert.class);
                        if(alert.getOwner().contentEquals(owner))
                        {
                            getSnapshots().getSnapshot(adapterP).getReference().delete();
                        }
                        else {
                            Toast.makeText(context, "This Item is Only Deleted Temporarily , To delete Permenetly contact the owner", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );


    }

    public void updateItem(int adapterPosition, final Context applicationContext, String memberName) {

        final int adapterP = adapterPosition;

        String path = getSnapshots().getSnapshot(adapterPosition).getReference().getPath();

        FirebaseFirestore db =  FirebaseFirestore.getInstance();

        final DocumentReference documentReference = db.document(path);

        documentReference.get().addOnSuccessListener(
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Alert alert = documentSnapshot.toObject(Alert.class);
                        if(alert.getPriority() < 0)
                        {
                            Toast.makeText(applicationContext, "Priority Number Cannot be less Than 1", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            alert.setPriority(alert.getPriority() - 1);
                            documentReference.set(alert);

                        }
                    }
                }
        );

    }

    public class AlertHolder extends RecyclerView.ViewHolder
    {

        TextView text_view_title;
        TextView text_view_description;
        TextView text_view_priority;
        CardView card_view;

        public AlertHolder(@NonNull View itemView) {
            super(itemView);

            text_view_title = itemView.findViewById(R.id.text_view_AlertItem_title);
            text_view_description = itemView.findViewById(R.id.text_view_AlertItem_description);
            text_view_priority = itemView.findViewById(R.id.text_view_AlertItem_priority);
            card_view = itemView.findViewById(R.id.card_view_alert_item);

            itemView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int position = getAdapterPosition();
                            if(position != RecyclerView.NO_POSITION && onItemClickListner != null)
                            {
                                onItemClickListner.onItemClick(getSnapshots().getSnapshot(position) , position);
                            }
                        }
                    }
            );


        }
    }

    public interface OnItemClickListner{
        void onItemClick(DocumentSnapshot documentSnapshot , int position);
    }

    public void setOnItemClickListner(AlertAdapter.OnItemClickListner listner)
    {
        this.onItemClickListner = listner;
    }

}
