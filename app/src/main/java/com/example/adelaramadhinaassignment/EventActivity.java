package com.example.adelaramadhinaassignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class EventActivity extends AppCompatActivity {

    Button Delete;
    Button Edit;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        //Set Title
        EventActivity.this.setTitle("IBM and Google Image Classification");
        Bundle extras = getIntent().getExtras();
        String title = extras.getString("title");
        String dates = extras.getString("description");
        String imageRes = extras.getString("image");
        key = extras.getString("keys");

        TextView tv = (TextView) findViewById(R.id.textViewLarge);
        tv.setText(title);
        tv = (TextView) findViewById(R.id.textViewSmall);
        tv.setText(dates);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference imagesRef = storageRef.child("images");

        Delete = findViewById(R.id.buttonDelete);
        Delete.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0) {
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = db.getReference("AdelaRamadhina");

                dbRef.child(key).removeValue();
                Intent intent = new Intent(EventActivity.this, ListViewActivity.class);
                startActivity(intent);
            }

        });
        Edit = findViewById(R.id.buttonEditt);
        Edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(EventActivity.this, EditActivity.class);
                intent.putExtra("titleEdit", title);
                intent.putExtra("descriptionEdit", dates);
                intent.putExtra("keys", key);
                intent.putExtra("imgFile", imageRes);
                intent.putExtra("btnVal", Edit.getId());
                startActivity(intent);
            }
        });






        try {
            final File localFile = File.createTempFile("temp", ".jpg");
            String filename = "img_" + String.valueOf(imageRes) + ".jpg";

            downloadFile(imagesRef.child(filename), localFile);

        } catch (IOException ex) {
            notifyUser(ex.getMessage());
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
                                    ImageView imageView = (ImageView) findViewById(R.id.imageViewYuh);
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