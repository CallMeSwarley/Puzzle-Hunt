package com.socialgaming.androidtutorial.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.socialgaming.androidtutorial.R;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder> {

    Context context;
    String[] data;
    int[] images;

    public RVAdapter(Context context, String[] s, int[] images){
        this.context = context;
        this.data = s;
        this.images = images;
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.set_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull RVAdapter.MyViewHolder holder, int position) {
        holder.text.setText(data[position]);
        holder.image.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView text;
        ImageView image;

        public MyViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.amt_pieces_textView);
            image = itemView.findViewById(R.id.image_preview_imageView);
        }
    }
}
