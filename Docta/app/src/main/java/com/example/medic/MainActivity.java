package com.example.medic;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.medic.ml.Skindeasease;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    Button uploadImage,medicalHistory;
    ImageView imageView;
    Skindeasease model;
    TextView textView;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser==null){
            Intent startIntent=new Intent(MainActivity.this,SignIn.class);
            startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        listeners();
    }

    private void listeners() {
        uploadImage.setOnClickListener(this);
        medicalHistory.setOnClickListener(this);
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        uploadImage = findViewById(R.id.uploadImage);
        medicalHistory = findViewById(R.id.medical_history);
        imageView =findViewById(R.id.image);
        textView =findViewById(R.id.diseaseName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.uploadImage:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getPackageManager())!=null)
                {
                    startActivityForResult(intent,0);
                }
                break;
            case R.id.medical_history:
                Intent intent2 = new Intent(MainActivity.this,MedicalHistory.class);
                startActivity(intent2);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null&& requestCode==0) {
            Bitmap b = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(b);
            try {
                model=Skindeasease.newInstance(getBaseContext());

                ImageProcessor imageProcessor =
                        new ImageProcessor.Builder()
                                .add(new ResizeOp(48*2, (48*2)/ 3, ResizeOp.ResizeMethod.BILINEAR))
                                .build();

                TensorImage tfImage = new TensorImage(DataType.UINT8);

                tfImage.load(b);
                tfImage = imageProcessor.process(tfImage);

                TensorImage tfimage=tfImage;

                Log.d("ak47","tf"+tfimage);
                Skindeasease.Outputs outputs=model.process(tfimage.getTensorBuffer());
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                Log.d("ak47", "onActivityResult: "+outputFeature0.getIntArray()[0]);
                Log.d("ak47", "onActivityResult: "+outputFeature0.getIntArray()[1]);
                textView.setText("Acne and Rosacea");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}