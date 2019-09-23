package com.example.familymessenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familymessenger.model.Family;
import com.example.familymessenger.model.Member;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class FamilyMemberUpdateView extends AppCompatActivity {

    Button button_pick_image;
    ImageView imageView_profile_photo;
    Button button_upload_image;

    EditText edit_text_old_password;
    EditText edit_text_new_password;
    Button button_reset_password;

    Button button_delete_member;

    TextView text_view_name;
    TextView text_view_nick_name;
    TextView text_view_phone_number;

    Uri mImageURI;
    String mImageType ;

    Family mFamily;
    Member mMember;

    private StorageReference mStorageRef;
    private FirebaseFirestore db ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member_update_view);


        Bundle data = getIntent().getExtras();
        mMember = (Member) data.getParcelable(Keys.EXTRA_MEMBER_OBJECT);
        mFamily = (Family) data.getParcelable(Keys.EXTRA_FAMILY_OBJECT);

        db = FirebaseFirestore.getInstance();


        button_pick_image = findViewById(R.id.button_FamilyMemberUpdate_PickImage);
        imageView_profile_photo = findViewById(R.id.image_view_FamilyMemberUpdate_profile_photo);
        button_upload_image = findViewById(R.id.button_FamilyMemberUpdate_UploadImage);

        edit_text_new_password = findViewById(R.id.edit_text_FamilyMemberUpdate_newPassword);
        edit_text_old_password = findViewById(R.id.edit_text_FamilyMemberUpdate_oldPassword);
        button_reset_password = findViewById(R.id.button_FamilyMemberUpdate_update_password);


        text_view_name = findViewById(R.id.text_view_FamilyMemberUpdate_member_name);
        text_view_nick_name = findViewById(R.id.text_view_FamilyMemberUpdate_nick_name);
        text_view_phone_number = findViewById(R.id.text_view_FamilyMemberUpdate_member_phone);

        text_view_name.setText(mMember.getName());
        text_view_nick_name.setText(mMember.getNickName());
        text_view_phone_number.setText(mMember.getPhone());


        button_delete_member = findViewById(R.id.button_FamilyMemberUpdate_delete_member);


        button_delete_member.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DocumentReference docRef = db.collection("Families").document(mFamily.getFamilyName()).collection("Members").document(mMember.getName());
                        docRef.delete().addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(FamilyMemberUpdateView.this, "Member Deleted Successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                        );
                    }
                }
        );


        button_reset_password.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String newPassword = edit_text_new_password.getText().toString().trim();
                        String oldPassword = mMember.getPassword();

                        if( !oldPassword.contentEquals(mFamily.getFamilyPassword()) )
                        {
                            Toast.makeText(FamilyMemberUpdateView.this, "Old Password Does Not Match", Toast.LENGTH_SHORT).show();
                        }
                        else {


                            DocumentReference docRef = db.collection("Families").document(mFamily.getFamilyName());
                            docRef.set(new Family(mFamily.getFamilyName(), newPassword)).addOnSuccessListener(
                                    new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(FamilyMemberUpdateView.this, "Password Updated Successfully!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                            );

                        }

                    }
                }
        );





        FirebaseFirestore db = FirebaseFirestore.getInstance();

        button_pick_image.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(
                                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        startActivityForResult(i, Keys.RESULT_LOAD_IMAGE);
                    }
                }
        );

        button_upload_image.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Toast.makeText(FamilyMemberUpdateView.this, createTransactionID() + "."+  mImageType, Toast.LENGTH_SHORT).show();

                            uploadImage(createTransactionID() + "." +  mImageType);



                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );




        Toast.makeText(this, "" + mMember.getName(), Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Keys.RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);

                mImageURI = selectedImage;
                mImageType = getMimeType(getApplicationContext(),selectedImage);
                imageView_profile_photo.setImageBitmap(bitmap);
            }catch (IOException e ){
                e.printStackTrace();
            }

        }


        // String picturePath contains the path of selected Image
    }
    public String createTransactionID() throws Exception{
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    public String getMimeType(Context context, Uri uri) {
        String mimeType = null;

        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getExtensionFromMimeType(cR.getType(uri));

        return type;
    }

    private void uploadImage(final String refs) {

        if(mImageURI != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");

            progressDialog.show();

            mStorageRef = FirebaseStorage.getInstance().getReference();
            StorageReference ref = mStorageRef.child(refs);

            ref.putFile(mImageURI)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference documentReference = db.collection("Families").document(mFamily.getFamilyName()).collection("Members").document(mMember.getName());
                            documentReference.update(
                                    "profileImage" , refs
                            ).addOnSuccessListener(
                                    new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(FamilyMemberUpdateView.this, "Profile Image Added Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );



                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");

                        }
                    });
        }
    }

}
