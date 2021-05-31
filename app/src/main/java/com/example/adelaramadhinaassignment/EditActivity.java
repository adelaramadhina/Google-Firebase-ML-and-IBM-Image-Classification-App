package com.example.adelaramadhinaassignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {
    Button btnCancel;
    Button btnSave;
    EditText textTitle;
    EditText textDes;
    ImageView image;
    ImageView detectedImg;
    String FileName;
    Uri imgPath;
    String key = "";
    //Araylist




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //title
        EditActivity.this.setTitle("Editing Classification Results");
       //Bundle
        Bundle extras = getIntent().getExtras();

        int btnValue = extras.getInt("btnVal", 0);
        switch(btnValue) {
            case 0: return;
            case R.id.buttonAdd:
                FileName = extras.getString("fileName");
                String title = extras.getString("title");
                String desc = extras.getString("description");
                imgPath = Uri.parse(extras.getString("imgResc"));
                image = (ImageView) findViewById(R.id.imageViewEdit);
                image.setImageURI(imgPath);
                textTitle = findViewById(R.id.editTextName);
                textDes = findViewById(R.id.editTextDesc);
                textTitle.setText(title);
                textDes.setText(desc);
                btnCancel = findViewById(R.id.buttonCancel);
                btnSave = findViewById(R.id.buttonSave);
                btnCancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent(EditActivity.this, MainActivity.class);

                        startActivity(intent);

                    }

                });
                btnSave.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                            addNewDetectedObjectToRealtimeDB();
                        Intent intent = new Intent(EditActivity.this, ListViewActivity.class);
                        startActivity(intent);
                    }
                });

                break;
            case R.id.buttonEditt:
                String title2 = extras.getString("titleEdit");
                String desc2 = extras.getString("descriptionEdit");
                String keys = extras.getString("keys");
                String imageFileDownloaded = extras.getString("imgFile");

                textTitle = findViewById(R.id.editTextName);
                textDes = findViewById(R.id.editTextDesc);

                textTitle.setText(title2);
                textDes.setText(desc2);
                image = (ImageView) findViewById(R.id.imageViewEdit);
                image.setImageResource(R.drawable.google);
                btnCancel = findViewById(R.id.buttonCancel);
                btnSave = findViewById(R.id.buttonSave);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                final StorageReference imagesRef = storageRef.child("images");

                try {
                    final File localFile = File.createTempFile("temp", ".jpg");
                    String filename = "img_" + String.valueOf(imageFileDownloaded) + ".jpg";

                    downloadFile(imagesRef.child(filename), localFile);

                } catch (IOException ex) {
                    notifyUser(ex.getMessage());
                }

                btnCancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent(EditActivity.this, MainActivity.class);

                        startActivity(intent);

                    }

                });
                btnSave.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        textTitle = findViewById(R.id.editTextName);
                        textDes = findViewById(R.id.editTextDesc);

                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference dbRef = db.getReference("AdelaRamadhina");
                        dbRef.child(keys).child("aTitle").setValue( textTitle.getText().toString());
                        dbRef.child(keys).child("bDescription").setValue(textDes.getText().toString());
                        Intent intent = new Intent(EditActivity.this, ListViewActivity.class);

                        startActivity(intent);
                    }
                });


                break;

        }
//        String title = titleName.getText().toString();
//        String desc= titleDesc.getText().toString();
//        saveExistingResults2RealtimeDatabase(title, desc, FileName, key);
    }
    public void addNewDetectedObjectToRealtimeDB() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference("AdelaRamadhina");
//Images
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference imagesRef = storageRef.child("images");


        String key = dbRef.push().getKey();
        EditText titleName = (EditText)findViewById(R.id.editTextName);
        EditText titleDesc = (EditText)findViewById(R.id.editTextDesc);
        detectedImg = findViewById(R.id.imageViewEdit);
        String title = titleName.getText().toString();
        String desc = titleDesc.getText().toString();



        ObjectEvent objects = new ObjectEvent(title, desc, FileName);
        uploadJPGFile(imagesRef, imgPath, "img_" + FileName + ".jpg");

        dbRef.child(key).child("aTitle").setValue(objects.getTitle());
        dbRef.child(key).child("bDescription").setValue(objects. getDescription());
        dbRef.child(key).child("cImageFile").setValue(objects.getImageFilename());
    }


    public void uploadJPGFile( StorageReference imgsRef, Uri imgPath, String imageFilename) {
        if (imgPath != null) {
            Uri uri = imgPath;

            String filename = "img_" + FileName + ".jpg";
            UploadTask uploadTask = imgsRef.child(filename).putFile(uri);

            uploadTask.addOnFailureListener(
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),
                                    "Upload failed: " + e.getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                }
                            })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                }
                            });
        } else {
            Toast.makeText(getApplicationContext(), "Nothing to upload",
                    Toast.LENGTH_LONG).show();
        }
    }
    public void downloadFile(StorageReference fileRef, final File file) {
        if (file != null) {
            fileRef.getFile(file)
                    .addOnSuccessListener(
                            new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Uri uri = Uri.fromFile(file);
                                    ImageView imageView = (ImageView) findViewById(R.id.imageViewEdit);
                                    imageView.setImageURI(uri);
                                    //notifyUser("Download completed");
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                    notifyUser("Unable to download");
                                }
                            });
        }
    }

    private void notifyUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}