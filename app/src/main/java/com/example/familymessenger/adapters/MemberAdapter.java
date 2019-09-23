package com.example.familymessenger.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.familymessenger.R;
import com.example.familymessenger.model.Member;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class MemberAdapter extends FirestoreRecyclerAdapter<Member,MemberAdapter.MemberHolder> {


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MemberAdapter(@NonNull FirestoreRecyclerOptions<Member> options) {
        super(options);
    }

    private StorageReference mStorageRef;

    private OnItemClickListner onItemClickListner;
    private Context context;

    @Override
    protected void onBindViewHolder(@NonNull final MemberHolder holder, int position, @NonNull Member model) {
        holder.text_view_name.setText(model.getName());
        holder.text_view_phone.setText(model.getPhone());
        holder.text_view_nickName.setText(model.getNickName());

        mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference pathReference = mStorageRef.child( model.getProfileImage());
        //Log.d("FBASEIMG" ,"images/" + model.getProfileImage() );

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(context).load(uri).into(holder.image_view_profile_pic);

            }
        });





    }

    @NonNull
    @Override
    public MemberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_item_family_member_view, parent , false);
        return new MemberHolder(v);
    }

    public class MemberHolder extends RecyclerView.ViewHolder
    {

        TextView text_view_name;
        TextView text_view_nickName;
        TextView text_view_phone;
        ImageView image_view_profile_pic;

        public MemberHolder(@NonNull View itemView) {
            super(itemView);
            text_view_name = itemView.findViewById(R.id.text_view_recycler_view_item_family_member_name);
            text_view_nickName = itemView.findViewById(R.id.text_view_recycler_view_item_family_member_nick_name);
            text_view_phone = itemView.findViewById(R.id.text_view_recycler_view_item_family_member_phone);
            image_view_profile_pic = itemView.findViewById(R.id.image_view_recycler_view_item_family_member_profile_pic);

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

    public void setOnItemClickListner(OnItemClickListner listner)
    {
        this.onItemClickListner = listner;
    }
}
