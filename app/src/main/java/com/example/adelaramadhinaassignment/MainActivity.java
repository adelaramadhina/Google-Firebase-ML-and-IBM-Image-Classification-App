package com.example.adelaramadhinaassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    Button btnStart;
    ImageView imgView;
    ImageButton ibmBtn;
    ImageButton firebaseBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       MainActivity.this.setTitle("IBM and Google Image Classification");
        textView = findViewById(R.id.textViewMain);
        btnStart =findViewById(R.id.buttonStart);
        btnStart.setVisibility(View.VISIBLE);
        imgView = findViewById(R.id.imageView);
        btnStart.setVisibility(View.VISIBLE);
        ibmBtn = findViewById(R.id.imageButtonIBM);
        ibmBtn.setVisibility(View.INVISIBLE);
        firebaseBtn = findViewById(R.id.imageButtonFirebase);
        firebaseBtn.setVisibility(View.INVISIBLE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, ObjectLearnActivity.class);
        int id = item.getItemId();
        switch (id) {
            case R.id.action_IBM:
                intent.putExtra("btnVal", ibmBtn.getId());
                intent.putExtra("title", "IBM Watson ML Cloud Services");
                intent.putExtra("imgResource", R.drawable.ibm);
                startActivity(intent);
                break;
            case R.id.action_firebase:
                intent.putExtra("btnVal", firebaseBtn.getId());
                intent.putExtra("title", "Google Firebase ML Cloud Services");
                intent.putExtra("imgResource", R.drawable.google);
                startActivity(intent);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void Start (View view) {
        textView.setText("This app uses cognitive services from IBM Watson and Google Machine Learning kit to classify images");
        btnStart.setVisibility(View.INVISIBLE);
        imgView.setVisibility(View.INVISIBLE);
        ibmBtn.setVisibility(View.VISIBLE);
        firebaseBtn.setVisibility(View.VISIBLE);

        ibmBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this, ObjectLearnActivity.class);
                intent.putExtra("btnVal", ibmBtn.getId());
                intent.putExtra("title", "IBM Watson ML Cloud Services");
                intent.putExtra("imgResource", R.drawable.ibm);
                startActivity(intent);
            }
        });
        firebaseBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this, ObjectLearnActivity.class);
                intent.putExtra("btnVal", firebaseBtn.getId());
                intent.putExtra("title", "Google Firebase ML Cloud Services");
                intent.putExtra("imgResource", R.drawable.google);
                startActivity(intent);
            }
        });
    }

}