package com.example.adelaramadhinaassignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import android.content.ContentValues;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.ObjectDetectorOptionsBase;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;
import com.ibm.watson.developer_cloud.android.library.camera.CameraHelper;
import com.ibm.watson.developer_cloud.android.library.camera.GalleryHelper;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class ObjectLearnActivity extends AppCompatActivity {

    //IBM
    private ImageView imageView;
    private TextView textView;
    private VisualRecognition visualRecognition;
    private CameraHelper cameraHelper;
    private GalleryHelper galleryHelper;
    private File photoFile;
    private Bitmap photoBitmap;
    private Uri contentUri;
    private final String api_key = "add_your_key_here";//IBM Watson API key

    //Firebase
    private static final int PICK_IMAGE_REQUEST = 1001;
    private static final int REQUEST_IMAGE_CAPTURE = 1000;
    private static final int REQUEST_PERMISSION = 3000;
    private Activity activity;
    private Uri outputFileUri;
    private final String TAG = "";
    private String currentPhotoPath;
    private TextView titleAbove;
    private Button btncapt;
    private Button btnload;
    Button btnAdd;
    String imageFilename;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_learn);
        Bundle extras = getIntent().getExtras();
        String title = extras.getString("title");
        int imgRes = extras.getInt("imgResource");
        ObjectLearnActivity.this.setTitle(title);
        ImageView image = (ImageView) findViewById(R.id.imageView);
        image.setImageResource(imgRes);
        titleAbove = findViewById(R.id.textTitle);
        titleAbove.setVisibility(View.INVISIBLE);
        btnAdd = findViewById(R.id.buttonAdd);
        btnAdd.setVisibility(View.INVISIBLE);



        int btnValue = extras.getInt("btnVal", 0);
        switch(btnValue) {
            case 0: return;
            case R.id.imageButtonIBM:
                btncapt = findViewById(R.id.buttonCapture);
                btnload = findViewById(R.id.buttonLoad);
                btncapt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cameraHelper.dispatchTakePictureIntent();
                    }

                });

                btnload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        galleryHelper.dispatchGalleryIntent();
                    }
                });
                imageView = (ImageView) findViewById(R.id.imageView);
                textView = (TextView) findViewById(R.id.textView);
                cameraHelper = new CameraHelper(this);
                galleryHelper = new GalleryHelper(this);
                IamOptions options = new IamOptions.Builder()
                        .apiKey(api_key)
                        .build();
                visualRecognition = new VisualRecognition("2020-04-28", options);

                break;
            case R.id.imageButtonFirebase:
                btncapt = findViewById(R.id.buttonCapture);
                btnload = findViewById(R.id.buttonLoad);
                btncapt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkPermissions() == false)
                            return;
                        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        outputFileUri = getContentResolver()
                                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                        startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
                    }
                });

                btnload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
                    }
                });
                imageView = findViewById(R.id.imageView);
                textView = (TextView) findViewById(R.id.textView);
                activity = this;
                break;
        }

    }

    private boolean checkPermissions() {
        String permissions[] = {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        boolean grantCamera =
                ContextCompat.checkSelfPermission(activity, permissions[0]) == PackageManager.PERMISSION_GRANTED;
        boolean grantExternal =
                ContextCompat.checkSelfPermission(activity, permissions[1]) == PackageManager.PERMISSION_GRANTED;

        if (!grantCamera && !grantExternal) {
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSION);
        } else if (!grantCamera) {
            ActivityCompat.requestPermissions(activity, new String[]{permissions[0]}, REQUEST_PERMISSION);
        } else if (!grantExternal) {
            ActivityCompat.requestPermissions(activity, new String[]{permissions[1]}, REQUEST_PERMISSION);
        }

        return grantCamera && grantExternal;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Bundle extras = getIntent().getExtras();
        int btnValue = extras.getInt("btnVal", 0);
        super.onActivityResult(requestCode, resultCode, data);
        if (btnValue == R.id.imageButtonIBM) {
            if (requestCode == CameraHelper.REQUEST_IMAGE_CAPTURE) {
                photoBitmap = cameraHelper.getBitmap(resultCode);
                photoFile = cameraHelper.getFile(resultCode);
                imageView.setImageBitmap(photoBitmap);
            }

            if (requestCode == GalleryHelper.PICK_IMAGE_REQUEST) {
                photoBitmap = galleryHelper.getBitmap(resultCode, data);
                photoFile = galleryHelper.getFile(resultCode, data);
                imageView.setImageBitmap(photoBitmap);
            }
            imageView.setImageBitmap(photoBitmap);
            contentUri = Uri.fromFile(photoFile);

            runBackgroundThread();
        } else if (btnValue == R.id.imageButtonFirebase) {

            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                photoBitmap = getCapturedImage();
                imageView.setImageBitmap(photoBitmap);
            }
            if (requestCode == PICK_IMAGE_REQUEST) {
                photoBitmap = getBitmap(resultCode, data);
                imageView.setImageBitmap(photoBitmap);
                outputFileUri = data.getData();
            }
            String [] subStrings = outputFileUri.toString().split("/");
            imageFilename = subStrings[subStrings.length-1];

            InputImage image = InputImage.fromBitmap(photoBitmap, 0);

            ObjectDetectorOptions options =
                    new ObjectDetectorOptions.Builder()
                            .setDetectorMode(ObjectDetectorOptionsBase.SINGLE_IMAGE_MODE)
                            .enableMultipleObjects()
                            .enableClassification()
                            .build();

            ObjectDetector objectDetector = ObjectDetection.getClient(options);

            objectDetector.process(image)
                    .addOnSuccessListener(
                            new OnSuccessListener<List<DetectedObject>>() {
                                @Override
                                public void onSuccess(List<DetectedObject> firebaseVisionObjects) {
                                    titleAbove.setVisibility(View.VISIBLE);
                                    btnAdd.setVisibility(View.VISIBLE);
                                    btncapt.setVisibility(View.INVISIBLE);
                                    btnload.setVisibility(View.INVISIBLE);

                                    if (firebaseVisionObjects.size() == 0) {
                                        titleAbove.setText("Unknown");
                                        textView.setText("Firebase ML: Detected objects: Unknown");
                                        btnAdd.setOnClickListener(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View arg0) {
                                                Intent intent = new Intent(ObjectLearnActivity.this,EditActivity.class);
                                                intent.putExtra("title", "Unknown");
                                                intent.putExtra("description", "Firebase ML: Detected objects: Unknown" );
                                                intent.putExtra("imgResc", outputFileUri.toString() );
                                                intent.putExtra("fileName",  imageFilename);
                                                intent.putExtra("keys", "");
                                                intent.putExtra("btnVal", btnAdd.getId());
                                                startActivity(intent);
                                            }
                                        });


                                        return;
                                    }
                                    textView.setText("Firebase ML: Detected objects:");
                                    for (DetectedObject object : firebaseVisionObjects) {
                                        if (object.getLabels().size() == 0) {
                                            titleAbove.setText("Unknown");
                                            textView.setText("Firebase ML: Detected objects: Unknown");
                                            btnAdd.setOnClickListener(new View.OnClickListener() {

                                                @Override
                                                public void onClick(View arg0) {
                                                    Intent intent = new Intent(ObjectLearnActivity.this,EditActivity.class);
                                                    intent.putExtra("title", "Unknown");
                                                    intent.putExtra("description", "Firebase ML: Detected objects: Unknown" );
                                                    intent.putExtra("imgResc", outputFileUri.toString() );
                                                    intent.putExtra("fileName",  imageFilename);
                                                    intent.putExtra("keys", "");
                                                    intent.putExtra("btnVal", btnAdd.getId());
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                        else {
                                            for (DetectedObject.Label label : object.getLabels()) {
                                                CharSequence currText = textView.getText();
                                                if (label.getText().length() > 2) {
                                                    titleAbove.setText(label.getText());
                                                    textView.setText("Firebase ML: Detected objects: " + label.getText() + ", Detected Score: " + label.getConfidence());
                                                    btnAdd.setOnClickListener(new View.OnClickListener() {

                                                        @Override
                                                        public void onClick(View arg0) {
                                                            Intent intent = new Intent(ObjectLearnActivity.this,EditActivity.class);
                                                            intent.putExtra("title", label.getText());
                                                            intent.putExtra("description", "Firebase ML: Detected objects: " + label.getText() + " Detected Score: " + label.getConfidence());
                                                            intent.putExtra("imgResc", outputFileUri.toString() );
                                                            intent.putExtra("fileName",  imageFilename);
                                                            intent.putExtra("keys", "");
                                                            intent.putExtra("btnVal", btnAdd.getId());
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }
                                                else {
                                                    titleAbove.setText(currText);
                                                    textView.setText("Firebase ML: Detected objects: "  + currText + ", Detected Score: " + label.getConfidence());
                                                    btnAdd.setOnClickListener(new View.OnClickListener() {

                                                        @Override
                                                        public void onClick(View arg0) {
                                                            Intent intent = new Intent(ObjectLearnActivity.this,EditActivity.class);
                                                            intent.putExtra("title", currText);
                                                            intent.putExtra("description", "Firebase ML: Detected objects: "  + currText + " Detected Score: " + label.getConfidence());
                                                            intent.putExtra("imgResc", outputFileUri.toString() );
                                                            intent.putExtra("fileName",  imageFilename);
                                                            intent.putExtra("keys", "");
                                                            intent.putExtra("btnVal", btnAdd.getId());
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }
                                            }
                                        }

                                    }

                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            textView.setText("Failed");
                        }
                    });
        }
    }


// Firebase Function.
    private Bitmap getCapturedImage() {
        Bitmap srcImage = null;
        try {
            srcImage = InputImage
                    .fromFilePath(getBaseContext(), outputFileUri)
                    .getBitmapInternal();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return srcImage;
    }
    public Bitmap getBitmap(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            try {
                return BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(targetUri));
            } catch (FileNotFoundException e) {
                Log.e(TAG, "File Not Found", e);
                return null;
            }
        }
        Log.e(TAG, "Result Code was not OK");
        return null;
    }


    //IBM Function
    private void runBackgroundThread(){


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                InputStream imagesStream = null;
                try {
                    imagesStream = new FileInputStream(photoFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                ClassifyOptions classifyOptions = new ClassifyOptions.Builder()
                        .imagesFile(imagesStream)
                        .imagesFilename(photoFile.getName())
                        .threshold((float) 0.6)
                        .classifierIds(Arrays.asList("default"))
                        .build();
                ClassifiedImages result = visualRecognition.classify(classifyOptions).execute();

                Gson gson = new Gson();
                String json = gson.toJson(result);
                Log.d("json", json);
                String classLabel = null;
                String scoreLabel = null;
                try {
                    classLabel = new JSONObject(json)
                            .getJSONArray("images")
                            .getJSONObject(0)
                            .getJSONArray("classifiers")
                            .getJSONObject(0)
                            .getJSONArray("classes")
                            .getJSONObject(0)
                            .getString("class");
                   scoreLabel = new JSONObject(json)
                            .getJSONArray("images")
                            .getJSONObject(0)
                            .getJSONArray("classifiers")
                            .getJSONObject(0)
                            .getJSONArray("classes")
                            .getJSONObject(0)
                            .getString("score");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String label = classLabel;
                final String score = scoreLabel;
                final InputStream image = imagesStream;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btncapt = findViewById(R.id.buttonCapture);
                        btncapt.setVisibility(View.INVISIBLE);
                        btnload = findViewById(R.id.buttonLoad);
                        btnload.setVisibility(View.INVISIBLE);
                        titleAbove.setVisibility(View.VISIBLE);
                        titleAbove.setText(label);
                        textView.setText("IBM Watson: Detected object: " + label + ", Detected Score: " + score);
                        btnAdd.setVisibility(View.VISIBLE);

                       String currentDatetime = LocalDateTime.now().toString();
                       imageFilename = currentDatetime.replaceAll("\\D+", "");
                        btnAdd.setOnClickListener(new View.OnClickListener() {


                            @Override
                            public void onClick(View arg0) {

                                Intent intent = new Intent(ObjectLearnActivity.this,EditActivity.class);
                                intent.putExtra("title", label);
                                intent.putExtra("description", "IBM Watson: Detected object: " + label + " Detected Score: " + score);
                                intent.putExtra("imgResc", contentUri.toString() );
                                intent.putExtra("fileName",  imageFilename);
                                intent.putExtra("keys", "");
                                intent.putExtra("btnVal", btnAdd.getId());
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        });
    }


}