package com.example.medic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MedicalHistory extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference database;
    private FirebaseUser mAuth;
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    Button addButton;
    ArrayList<String> list=new ArrayList<String>();
    RecyclerView recyclerView;
    MedicalRecordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);

        init();

        setListeners();
    }

    private void setListeners() {
        addButton.setOnClickListener(this);
    }

    private void init() {
        Log.d("ak47", "init: ");
        mAuth =  FirebaseAuth.getInstance().getCurrentUser();
        database=FirebaseDatabase.getInstance().getReference(mAuth.getUid());
        addButton=findViewById(R.id.add);

        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager linearLinearLayoutManager=new LinearLayoutManager(this);
        adapter=new MedicalRecordAdapter(list,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLinearLayoutManager);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    String url=(String) snapshot1.getValue();
                    list.add(url);
                    Log.d("ak47", "onDataChange: "+url);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(intent.resolveActivity(getPackageManager())!=null)
                {
                    startActivityForResult(intent,0);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String currentTime= System.currentTimeMillis()+"";
        if(data!=null&& requestCode==0) {
            Bitmap b = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataArray = baos.toByteArray();
            UploadTask mainImageUpload=mStorageRef.child(currentTime).putBytes(dataArray);
            mainImageUpload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("ak47", "onSuccess: 2");

                    mStorageRef.child(currentTime).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url=String.valueOf(uri);
                            Log.d("ak47",url);
                            database.child(currentTime).setValue(url).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MedicalHistory.this,"Report Added",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }
            });
        }
    }

}