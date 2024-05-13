package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    Context context;
    List<Clothes> clothes;
    DatabaseReference database;

    public ProductAdapter(Context context, List<Clothes> clothes, DatabaseReference database) {
        this.context = context;
        this.clothes = clothes;
        this.database = database;
    }


    @NonNull
    @Override
    public ProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new MyViewHolder(v);    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.MyViewHolder holder, int position) {
        Clothes cloth = clothes.get(position);
        holder.name.setText(cloth.getName());
//        holder.image.setText(product.getImage());
        Glide.with(context).load(cloth.getImage1()).into(holder.image);
        holder.price.setText(String.format("$%s", cloth.getPrice()));
        holder.description.setText(cloth.getDescription());


        if(cloth.getQuantity()> 0){
            holder.addToCart.setVisibility(View.INVISIBLE);
            holder.itemValue.setVisibility(View.VISIBLE);
            holder.itemValue.setText(String.valueOf(cloth.getQuantity()));
            holder.addValue.setVisibility(View.VISIBLE);
            holder.removeValue.setVisibility(View.VISIBLE);

            holder.addValue.setOnClickListener(v -> {
                cloth.setQuantity(cloth.getQuantity()+1);
                database.child(cloth.getName()).child("quantity").setValue(cloth.getQuantity());
                notifyDataSetChanged();
            });

            holder.removeValue.setOnClickListener(v -> {
                if(cloth.getQuantity() > 0) {
                    cloth.setQuantity(cloth.getQuantity() - 1);
                    database.child(cloth.getName()).child("quantity").setValue(cloth.getQuantity());
                    notifyDataSetChanged();
                }
            });
        }
//
        if(cloth.getQuantity()<=0){
            holder.itemValue.setVisibility(View.INVISIBLE);
            holder.addValue.setVisibility(View.INVISIBLE);
            holder.removeValue.setVisibility(View.INVISIBLE);
            holder.addToCart.setVisibility(View.VISIBLE);
        }

        holder.addToCart.setOnClickListener(v -> {
            cloth.setQuantity(cloth.getQuantity()+1);
            database.child(cloth.getName()).child("quantity").setValue(cloth.getQuantity());
            notifyDataSetChanged();
        });

        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EachProductActivity.class);
            intent.putExtra("cloth", cloth);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return clothes.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, description, itemValue;
        ImageView image, addValue, removeValue;
        Button addToCart;
        MaterialCardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cartName);
            image = itemView.findViewById(R.id.cartImage);
            price = itemView.findViewById(R.id.cartPrice);
            description = itemView.findViewById(R.id.productDescription);
            addToCart = itemView.findViewById(R.id.addToCartButton);
            itemValue = itemView.findViewById(R.id.itemValue);
            addValue = itemView.findViewById(R.id.addValue);
            removeValue = itemView.findViewById(R.id.removeValue);
            cardView = itemView.findViewById(R.id.productCard);


        }
    }
}
