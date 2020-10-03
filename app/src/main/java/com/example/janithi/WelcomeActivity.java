package com.example.janithi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void openProducts(View view){
        Intent intent = new Intent(this, ProductActivity.class);
        startActivity(intent);
    }

    public void goToCart(View view){
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    public void doSearch(View view){
        Intent intent = new Intent(this, SearchActivity.class);
        EditText editText = (EditText)findViewById(R.id.searchTxt);
        intent.putExtra("SEARCH_DATA", editText.getText().toString());
        startActivity(intent);
    }
}