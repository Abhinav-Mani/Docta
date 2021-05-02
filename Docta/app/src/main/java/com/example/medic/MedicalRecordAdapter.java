package com.example.medic;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class MedicalRecordAdapter extends RecyclerView.Adapter<MedicalRecordAdapter.MyHolder> {
    ArrayList<String> urls;
    Context context;
    MedicalRecordAdapter(ArrayList<String> urls,Context context){
        this.urls=urls;
        this.context=context;
    }
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singleitem,parent,false);
        MyHolder myViewHolder=new MyHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Glide.with(context).asBitmap().
                load(urls.get(position)).
                fitCenter().
                diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        Log.d("ak47", "getItemCount: "+urls.size());
        return urls.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}
