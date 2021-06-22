package com.example.claimit.ui.home;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.claimit.ClaimImageViewer;
import com.example.claimit.DashboardActivity;
import com.example.claimit.ImageUploadActivity;
import com.example.claimit.R;
import com.example.claimit.SignInActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClaimAdapter extends RecyclerView.Adapter<ClaimAdapter.Viewholder> {

    private HomeFragment context;
    private ClaimModel[] claimModelArrayList;
    private Context myContext;

    // Constructor
    public ClaimAdapter(HomeFragment context, ClaimModel[] claimModelArrayList) {
        this.context = context;
        this.claimModelArrayList = claimModelArrayList;
    }

    @NonNull
    @Override
    public ClaimAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.claim_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClaimAdapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        ClaimModel model = claimModelArrayList[position];
        holder.claimId.setText(model.getClaimId());
        holder.make.setText(model.getMake());
        holder.model.setText(model.getModel());
        holder.year.setText(model.getVehicle_year());
        String created_date = model.getCreated_date();
        SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date dtIn = new Date();
        try {
            dtIn = inFormat.parse(created_date);
        } catch (ParseException e) {
            Log.e("PARSE EXCEPTION", "Unable to parse date");
            e.printStackTrace();
        }
        holder.createdAt.setText(dateFormat.format(dtIn));
        myContext = holder.itemView.getContext();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("POS - ", String.valueOf(position));
                Log.d("ITEMS - ", model.toString());
                Intent imageView = new Intent(myContext, ClaimImageViewer.class);
                imageView.putExtra("MODEL", model.getImageUrl());
                imageView.putExtra("SEVERITY", model.getSeverity());
                myContext.startActivity(imageView);
            }
        });
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return claimModelArrayList.length;
    }

    // View holder class for initializing of
    // your views such as TextView and Imageview.
    public class Viewholder extends RecyclerView.ViewHolder {
        private TextView claimId, make, model, year, createdAt;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            claimId = itemView.findViewById(R.id.idClaimId);
            make = itemView.findViewById(R.id.idMake);
            model = itemView.findViewById(R.id.idModel);
            year = itemView.findViewById(R.id.idYear);
            createdAt = itemView.findViewById(R.id.idCreatedAt);
        }
    }
}