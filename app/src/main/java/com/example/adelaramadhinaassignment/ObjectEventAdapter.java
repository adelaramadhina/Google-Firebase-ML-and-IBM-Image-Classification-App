package com.example.adelaramadhinaassignment;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;

public class ObjectEventAdapter extends ArrayAdapter<ObjectEvent> {

    ArrayList<ObjectEvent> events;
    public ObjectEventAdapter(Context context, int resource, ArrayList<ObjectEvent> objects) {
        super(context, resource, objects);
        events = objects;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.event_listview_item, parent, false);
        }

        ObjectEvent event = events.get(position);
        final File file;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        ImageView icon = (ImageView) convertView.findViewById(R.id.imageViewIcon);

        try {
            final File localFile = File.createTempFile("temp", ".jpg");
            String filename = "images/"+ "img_" + String.valueOf(event.getImageFilename()) + ".jpg";

            downloadFile(storageRef.child(filename), localFile, convertView);

        } catch (IOException ex) {
        }

        TextView title = (TextView) convertView.findViewById(R.id.textViewTitle);

        title.setText(event.getTitle());
        return convertView;
    }
    public void downloadFile(StorageReference fileRef, final File file, final View view) {
        if (file != null) {
            fileRef.getFile(file)
                    .addOnSuccessListener(
                            new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Uri uri = Uri.fromFile(file);
                                    ImageView imageView = (ImageView) view.findViewById(R.id.imageViewIcon);
                                    imageView.setImageURI(uri);
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
        }
    }



}
