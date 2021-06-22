package com.example.claimit;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    Context context;
    List<ImageItem> postList;

    public PostAdapter(Context context , List<ImageItem> postList){
        this.context = context;
        this.postList = postList;
    }
    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(context).inflate(R.layout.each_image_view , parent , false);
        return new PostHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        ImageItem item = postList.get(position);
        holder.setImageView(item.getImageUrl());
        holder.setmSeverity(item.getSeverity());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView mSeverity;
        View view;
        public PostHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

        }

        public void setmSeverity(String severity) {
            mSeverity = view.findViewById(R.id.severity);
            String sev = "Severity: " + severity;
            mSeverity.setText(sev);
        }

        public void setImageView(String url){
            imageView = view.findViewById(R.id.imageView);
            Glide.with(context).load(url).into(imageView);
        }
    }
}