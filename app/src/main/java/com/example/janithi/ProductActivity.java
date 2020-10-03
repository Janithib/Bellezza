package com.example.janithi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProductActivity extends AppCompatActivity {
    private ImageView productImage;
    private TextView productName;
    private TextView manufacturer;
    private TextView origin;
    private TextView price;
    private Product product;
    private Button cartButton;
    private Context context;

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        this.productImage = findViewById(R.id.productImage);
        this.productName = findViewById(R.id.productName);
        this.manufacturer = findViewById(R.id.manufacturer);
        this.origin = findViewById(R.id.origin);
        this.price = findViewById(R.id.price);
        this.cartButton = findViewById(R.id.addToCartBtn);

        this.context = this;
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.databaseReference = firebaseDatabase.getReference().child("User").child("Cart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getIntent() != null){
            Intent intent = getIntent();
            product = (Product) intent.getSerializableExtra("PRODUCT");
            if(product != null){
                if(product.getImage() == 0)
                    productImage.setImageResource(R.drawable.photo_2020_08_15_18_53_50);
                else
                    productImage.setImageResource(product.getImage());
                productName.setText(product.getName());
                manufacturer.setText(product.getManufacturer());
                origin.setText(product.getOrigin());
                price.setText(String.format("$ %s", product.getPrice()));
            }
        }
    }

    public void goToCart(View view){
        String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(product, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Intent intent = new Intent(context, CartActivity.class);
                startActivity(intent);
            }
        });
    }
}