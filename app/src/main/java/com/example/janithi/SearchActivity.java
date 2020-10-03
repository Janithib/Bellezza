package com.example.janithi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements ProductAdapter.ProductOnClickListener {
    private EditText editText;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private ProductAdapter.ProductOnClickListener onClickListener;
    private FirebaseRecyclerOptions<Product> firebaseRecyclerOptions;
    private FirebaseRecyclerAdapter<Product, ProductAdapter.ProductViewHolder> productRecyclerAdapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        this.context = this;
        editText = findViewById(R.id.searchTxt_1);
        editText.setText(intent.getStringExtra("SEARCH_DATA"));

        recyclerView = findViewById(R.id.searchRecyclerView);

        onClickListener = this;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Products");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        doSearch();
    }

    public void doSearch(View view){
        doSearch();
    }

    public void doSearch(){
        recyclerView.removeAllViews();

        if(!editText.getText().toString().equals("")){
            String keyword = editText.getText().toString().substring(0,1).toUpperCase()+editText.getText().toString().substring(1);
            firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Product>().setQuery(databaseReference.orderByChild("name").startAt(keyword), Product.class).build();
        }else{
            firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Product>().setQuery(databaseReference, Product.class).build();
        }

        productRecyclerAdapter = new FirebaseRecyclerAdapter<Product, ProductAdapter.ProductViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder productViewHolder, int i, @NonNull Product product) {
                productViewHolder.product.setText(product.getName());
                productViewHolder.productImage.setImageResource(product.getImage());
                productViewHolder.manufacturer.setText(product.getManufacturer());
                productViewHolder.origin.setText(product.getOrigin());
                productViewHolder.price.setText(String.format("$ %s", product.getPrice()));
                productViewHolder.removeImage.setVisibility(View.GONE);
            }

            @NonNull
            @Override
            public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout,parent,false);
                return new ProductAdapter.ProductViewHolder(view, onClickListener);
            }
        };
        productRecyclerAdapter.startListening();

        productRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if(productRecyclerAdapter.getItemCount() == 0){
                    Toast toast = Toast.makeText(context, "Looks like there is no items!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL, 0 ,0);
                    toast.show();
                }
            }
        });

        recyclerView.setAdapter(productRecyclerAdapter);
    }

    @Override
    public void onProductClick(int position) {
        Intent intent = new Intent(this, ProductActivity.class);
        intent.putExtra("PRODUCT", productRecyclerAdapter.getItem(position));
        startActivity(intent);
    }

    @Override
    public void onProductRemoveClick(int adPosition, int lyPosition) {

    }
}