package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class EachProductAdapter extends RecyclerView.Adapter<EachProductAdapter.MyViewHolder> {

    private List<String> images;

    public EachProductAdapter(List<String> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public EachProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new MyViewHolder(layoutInflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull EachProductAdapter.MyViewHolder holder, int position) {
        String image = images.get(position);

        Context context = holder.itemView.getContext();
        Glide.with(context).load(image).into(holder.imageProduct);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageProduct;

        public MyViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.pager_layout, parent, false));
            imageProduct = itemView.findViewById(R.id.imageCloth);
        }
    }
}
