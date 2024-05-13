package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class EachProductActivity extends AppCompatActivity {
    FirebaseAuth auth;
    ViewPager2 vPager;
    DatabaseReference database;
    FirebaseUser user;
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference("products");

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_each_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Clothes cloth = new Clothes();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("cloth")) {
            cloth = (Clothes) intent.getSerializableExtra("cloth");
         }

        List<String> images = new ArrayList<>();
        images.add(cloth.getImage1());
        images.add(cloth.getImage2());
        images.add(cloth.getImage3());

        vPager = findViewById(R.id.vPager);
        adapter = new EachProductAdapter(images);
        vPager.setAdapter(adapter);

        TextView name = findViewById(R.id.productEachName);
        name.setText(cloth.getName());

        TextView price = findViewById(R.id.productEachPrice);
        price.setText(String.format("$%s", String.valueOf(cloth.getPrice())));

        TextView description = findViewById(R.id.productEachDescription);
        description.setText(cloth.getDescription());

        Button addToCart = findViewById(R.id.productEachAdd);
        TextView itemValue = findViewById(R.id.itemValue);
        ImageView removeValue = findViewById(R.id.removeValue);
        ImageView addValue = findViewById(R.id.addValue);

        Clothes finalCloth = cloth;
        if(finalCloth.getQuantity()> 0){
            addToCart.setVisibility(View.INVISIBLE);
            itemValue.setVisibility(View.VISIBLE);
            itemValue.setText(String.valueOf(finalCloth.getQuantity()));
            addValue.setVisibility(View.VISIBLE);
            removeValue.setVisibility(View.VISIBLE);
        }
        else if(finalCloth.getQuantity()<=0){
            itemValue.setVisibility(View.INVISIBLE);
            addValue.setVisibility(View.INVISIBLE);
            removeValue.setVisibility(View.INVISIBLE);
            addToCart.setVisibility(View.VISIBLE);
        }

        addValue.setOnClickListener(v -> {
            finalCloth.setQuantity(finalCloth.getQuantity() + 1);
            itemValue.setText(String.valueOf(finalCloth.getQuantity()));
            if (finalCloth.getQuantity() > 0) {
                addToCart.setVisibility(View.INVISIBLE);
                itemValue.setVisibility(View.VISIBLE);
                addValue.setVisibility(View.VISIBLE);
                removeValue.setVisibility(View.VISIBLE);
            }

            database.child(finalCloth.getName()).child("quantity").setValue(finalCloth.getQuantity());
        });

        removeValue.setOnClickListener(v -> {
            if (finalCloth.getQuantity() > 0) {
                finalCloth.setQuantity(finalCloth.getQuantity() - 1);
                itemValue.setText(String.valueOf(finalCloth.getQuantity()));

                if (finalCloth.getQuantity() <= 0) {
                    addToCart.setVisibility(View.VISIBLE);
                    itemValue.setVisibility(View.INVISIBLE);
                    addValue.setVisibility(View.INVISIBLE);
                    removeValue.setVisibility(View.INVISIBLE);
                }

                database.child(finalCloth.getName()).child("quantity").setValue(finalCloth.getQuantity());
            }
        });

        addToCart.setOnClickListener(v -> {
            finalCloth.setQuantity(finalCloth.getQuantity() + 1);
            itemValue.setText(String.valueOf(finalCloth.getQuantity()));

            if (finalCloth.getQuantity() > 0) {
                addToCart.setVisibility(View.INVISIBLE);
                itemValue.setVisibility(View.VISIBLE);
                addValue.setVisibility(View.VISIBLE);
                removeValue.setVisibility(View.VISIBLE);
            }

            database.child(finalCloth.getName()).child("quantity").setValue(finalCloth.getQuantity());
        });


        if(user == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        ImageView logoutButton = findViewById(R.id.logout);

        logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });


        FloatingActionButton cartButton = findViewById(R.id.goToCartFloating);
        cartButton.setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
            finish();
        });
    }


}