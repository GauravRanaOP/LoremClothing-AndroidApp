package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CartItemAdapter adapter;
    List<Clothes> clothes;
    DatabaseReference database;
    StorageReference reference;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.cartRecyclerView);
        database = FirebaseDatabase.getInstance().getReference("products");
        reference = FirebaseStorage.getInstance().getReference();
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        clothes = new ArrayList<>();
        adapter = new CartItemAdapter(this, clothes, database);
        recyclerView.setAdapter(adapter);

        double total = adapter.getTotalPrice();
        Button checkout = findViewById(R.id.checkoutCart);
        final String[] totalafter = new String[1];
        checkout.setText("Checkout ($" + String.format("%.2f", total) + ")");


        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                double totalPriceAfterTax = adapter.getTotalPrice();
                Button checkoutButton = findViewById(R.id.checkoutCart);
                checkoutButton.setText("Checkout ($" + String.format("%.2f", totalPriceAfterTax) + ")");

            }
        });

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clothes.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Clothes cloth = dataSnapshot.getValue(Clothes.class);
                    if(cloth.getQuantity()>0) {
                        clothes.add(cloth);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
//                Clothes cloth = adapter.getClothes(position);
//                cloth.setQuantity(0);
//                database.child(cloth.getName()).child("quantity").setValue(cloth.getQuantity());
//                Toast.makeText(CartActivity.this, "Item has been removed!", Toast.LENGTH_SHORT).show();
                showAlertDialog(position);

            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        ImageView cartlogout = findViewById(R.id.logout);

        if(user == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        cartlogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });

        checkout.setOnClickListener(view -> {
            Intent intent = new Intent(this, CheckoutActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void showAlertDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");
        builder.setMessage("Item will be removed");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Clothes cloth = adapter.getClothes(position);
            cloth.setQuantity(0);
            database.child(cloth.getName()).child("quantity").setValue(cloth.getQuantity());
            Toast.makeText(CartActivity.this, "Item has been removed!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            adapter.notifyItemChanged(position);
        });

        builder.show();
    }
}