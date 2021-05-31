package com.example.adelaramadhinaassignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;

public class ListViewActivity extends AppCompatActivity {

    Button add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        //Set Title
        ListViewActivity.this.setTitle("IBM and Google Image Classification");

        ArrayList<ObjectEvent> events = new ArrayList<ObjectEvent>();

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference("AdelaRamadhina");
//        ArrayAdapter<ObjectEvent> adapter = new ArrayAdapter<ObjectEvent>(
//                this, android.R.layout.simple_list_item_1, events);

       ObjectEventAdapter adapter = new ObjectEventAdapter(
                this, R.layout.event_listview_item, events);

        // get canberra events from Firebase realtime database
        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ObjectEvent eventr = new ObjectEvent(
                        (String) dataSnapshot.child("aTitle").getValue(),
                        (String) dataSnapshot.child("bDescription").getValue(),
                        (String) dataSnapshot.child("cImageFile").getValue()
                );
                // Add a canberra event from Firebase to adapter
                adapter.add(eventr);
                eventr.getDatabaseKey(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Get list view from layout and set adapter to it
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        // To handle click event when user clicks on a list view item
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ObjectEvent event = events.get(position);
                        Intent intent = new Intent(view.getContext(), EventActivity.class);
                        intent.putExtra("title", event.getTitle());
                        intent.putExtra("description", event.getDescription());
                        intent.putExtra("image", event.getImageFilename());
                        intent.putExtra("keys", event.setKeyDatabase());
                        startActivity(intent);
                    }
                });
        add = findViewById(R.id.buttonAddMore);
        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(ListViewActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

}