package com.example.janithi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CartActivity extends AppCompatActivity implements ProductAdapter.ProductOnClickListener {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseRecyclerOptions<Product> firebaseRecyclerOptions;
    private FirebaseRecyclerAdapter<Product, ProductAdapter.ProductViewHolder> firebaseRecyclerAdapter;
    private DatabaseReference databaseReference;
    private ProductAdapter.ProductOnClickListener onClickListener;

    private TextView subtotalT;
    private TextView deliveryT;
    private TextView taxT;
    private TextView totalT;

    private  double subtotal = 0;
    private  double delivery = 0;
    private  double tax = 0;
    private  double total = 0;
    private  int count = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        subtotalT = findViewById(R.id.subtotal);

        onClickListener = this;
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        firebaseDatabase = FirebaseDatabase.getInstance();
        //TODO Add child("963164963V") to following line after getting the user NIC!
        databaseReference = firebaseDatabase.getReference().child("User").child("Cart");

        /*
        ArrayList<Product> products = new ArrayList<Product>();
        products.add(new Product(R.drawable.photo_2020_08_15_18_53_50,"Lipstick","L'Oréal","France",70));
        products.add(new Product(R.drawable.photo_2020_08_15_18_53_50,"Shampoo","L'Oréal","France",120));
        products.add(new Product(R.drawable.photo_2020_08_15_18_53_50,"Foundation","L'Oréal","France",85));
        products.add(new Product(R.drawable.photo_2020_08_15_18_53_50,"Perfume","L'Oréal","France",62));
        products.add(new Product(R.drawable.photo_2020_08_15_18_53_50,"Conditioner","L'Oréal","France",100));
        products.add(new Product(R.drawable.photo_2020_08_15_18_53_50,"Lip Gloss","L'Oréal","France",25));
        products.add(new Product(R.drawable.photo_2020_08_15_18_53_50,"Hair oil","L'Oréal","France",250));
        products.add(new Product(R.drawable.photo_2020_08_15_18_53_50,"Night Cream","L'Oréal","France",45));
        products.add(new Product(R.drawable.photo_2020_08_15_18_53_50,"Cleansing Milk","L'Oréal","France",50));
        products.add(new Product(R.drawable.photo_2020_08_15_18_53_50,"Cleansing Cream","L'Oréal","France",65));


        for (Product product:
                products) {
            String key = databaseReference.push().getKey();
            databaseReference.child(key).setValue(product);
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Product>().setQuery(databaseReference, Product.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Product, ProductAdapter.ProductViewHolder>(firebaseRecyclerOptions) {
            @NonNull
            @Override
            public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout,parent,false);
                return new ProductAdapter.ProductViewHolder(view, onClickListener);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder productViewHolder, int i, @NonNull Product product) {
                productViewHolder.product.setText(product.getName());
                productViewHolder.productImage.setImageResource(product.getImage());
                productViewHolder.manufacturer.setText(product.getManufacturer());
                productViewHolder.origin.setText(product.getOrigin());
                productViewHolder.price.setText(String.format("$ %s", product.getPrice()));
            }
        };

        firebaseRecyclerAdapter.startListening();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                subtotal = 0;
                total = 0;
                for(int x = 0; x < firebaseRecyclerAdapter.getItemCount(); x++){
                    if(count < firebaseRecyclerAdapter.getItemCount()){
                        updateTotals(firebaseRecyclerAdapter.getItem(x).getPrice(), false);
                    }else{
                        updateTotals(firebaseRecyclerAdapter.getItem(x).getPrice(), true);
                    }
                }
                count = firebaseRecyclerAdapter.getItemCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void onProductClick(int position) {
        Intent intent = new Intent(this, ProductActivity.class);
        intent.putExtra("PRODUCT", firebaseRecyclerAdapter.getItem(position));
        startActivity(intent);
    }

    @Override
    public void onProductRemoveClick(final int adPosition, int lyPosition) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are sure want to delete this item?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final double temp = firebaseRecyclerAdapter.getItem(adPosition).getPrice();
                final Query query = databaseReference.orderByChild("name").equalTo(firebaseRecyclerAdapter.getItem(adPosition).getName());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            snapshot.getRef().removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    updateTotals(temp, false);
                                    Toast.makeText(getBaseContext(), "Successfully removed from cart!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();

    }

    public void updateTotals(double price, boolean isAdd){
        if(isAdd){
            this.subtotal += price;
        }else{
            this.subtotal -= price;
        }
       // this.total = subtotal + tax + delivery;

        subtotalT.setText(String.format("$ %s", subtotal));
      //  deliveryT.setText(String.format("$ %s", delivery));
     //   taxT.setText(String.format("$ %s", tax));
    //    totalT.setText(String.format("$ %s", total));
    }

    public void goToPayment(View v){
        //TODO uncomment when integrating!
        /*if(subtotal > 0){
            if(firebaseRecyclerAdapter.getItemCount() > 0){
                firebaseDatabase.getReference().child("User").child("TotalPayable").setValue(String.valueOf(total), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Toast.makeText(getBaseContext(), subtotal+"", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this, PaymentActivity.class);
                        startActivity(intent);
                    }
                });
            }
        }*/
    }
}