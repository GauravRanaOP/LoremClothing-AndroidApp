package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.MyViewHolder> {

    Context context;
    List<Clothes> clothes;
    DatabaseReference database;

    public CartItemAdapter(Context context, List<Clothes> clothes, DatabaseReference database) {
        this.context = context;
        this.clothes = clothes;
        this.database = database;
    }

    public Clothes getClothes(int position) {
        return clothes.get(position);
    }

    @NonNull
    @Override
    public CartItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemAdapter.MyViewHolder holder, int position) {
        Clothes cloth = clothes.get(position);
        if (cloth.getQuantity() > 0) {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.name.setText(cloth.getName());
            holder.price.setText(String.format("Price: $%s", String.valueOf(cloth.getPrice())));
            Glide.with(context).load(cloth.getImage1()).into(holder.cartimage);
            double totalPrice = cloth.getPrice() * cloth.getQuantity();
            holder.itemValue.setText(String.valueOf(cloth.getQuantity()));

            double totalPriceAfterTax = totalPrice * 1.13;
            holder.totalAfterTax.setText(String.format("Price After Tax: $%s", String.format("%.2f", (totalPriceAfterTax + 5))));
        }
        else{
            holder.itemView.setVisibility(View.GONE);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            params.height = 0;
            params.width = 0;
            holder.itemView.setLayoutParams(params);
        }

        holder.addValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newQuantity = cloth.getQuantity() + 1;
                cloth.setQuantity(newQuantity);
                database.child(cloth.getName()).child("quantity").setValue(cloth.getQuantity());
                notifyDataSetChanged();
            }
        });

        holder.removeValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQuantity = cloth.getQuantity() - 1;
                if (newQuantity >= 0) {
                    cloth.setQuantity(newQuantity);
                    database.child(cloth.getName()).child("quantity").setValue(cloth.getQuantity());
                    notifyDataSetChanged();
                }
            }
        });

    }
    public double getTotalPrice() {
        double total = 0;
        for (Clothes clothe : clothes) {
            if (clothe.getQuantity() > 0) {
                double priceWithoutTax = clothe.getPrice() * clothe.getQuantity();
                double taxAmount = priceWithoutTax * 0.13;
                double totalPriceWithTax = priceWithoutTax + taxAmount;
                double totalafterDelivery = totalPriceWithTax + 5;
                total += totalafterDelivery;
            }
        }
        return total;
    }


    @Override
    public int getItemCount() {
        return clothes.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, delivery, tax, totalAfterTax, itemValue;
        ImageView cartimage, addValue, removeValue;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cartName);
            cartimage = itemView.findViewById(R.id.cartImage);
            price = itemView.findViewById(R.id.cartPrice);
            delivery = itemView.findViewById(R.id.cartDelivery);
            tax = itemView.findViewById(R.id.cartTax);
            totalAfterTax = itemView.findViewById(R.id.priceAfterTax);
            itemValue = itemView.findViewById(R.id.itemValue);
            addValue = itemView.findViewById(R.id.addValue);
            removeValue = itemView.findViewById(R.id.removeValue);

        }
    }
}
