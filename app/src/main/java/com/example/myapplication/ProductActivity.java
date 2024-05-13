package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

public class ProductActivity extends AppCompatActivity {
    private ImageView logoutview;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private RecyclerView recyclerView;
    DatabaseReference database;
    StorageReference reference;
    List<Clothes> clothes;
    private ProductAdapter productAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        recyclerView = findViewById(R.id.productRecycler);
        database = FirebaseDatabase.getInstance().getReference("products");
        reference = FirebaseStorage.getInstance().getReference();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        clothes = new ArrayList<>();
        productAdapter = new ProductAdapter(this, clothes, database);
        recyclerView.setAdapter(productAdapter);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clothes.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Clothes cloth = dataSnapshot.getValue(Clothes.class);

                    clothes.add(cloth);
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        logoutview = findViewById(R.id.logout);

        if(user == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        logoutview.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });
    }
}