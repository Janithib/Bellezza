package com.example.janithi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private ArrayList<Product> dataSet;
    private boolean removeVisibility;
    private ProductOnClickListener productOnClickListener;

    public static class ProductViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener{
        ImageView productImage;
        TextView product;
        TextView manufacturer;
        TextView origin;
        TextView price;
        ImageButton removeImage;
        ProductOnClickListener productOnClickListener;

        public ProductViewHolder(@NonNull final View itemView, ProductOnClickListener productOnClickListener) {
            super(itemView);
            this.productImage = itemView.findViewById(R.id.productImage);
            this.product = itemView.findViewById(R.id.productName);
            this.manufacturer = itemView.findViewById(R.id.manufacturer);
            this.origin = itemView.findViewById(R.id.origin);
            this.price = itemView.findViewById(R.id.price);
            this.removeImage = itemView.findViewById(R.id.removeBtn);

            this.productOnClickListener = productOnClickListener;
            this.removeImage.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.equals(removeImage)){
                productOnClickListener.onProductRemoveClick(getAdapterPosition(), getLayoutPosition());
            }else{
                productOnClickListener.onProductClick(getAdapterPosition());
            }
        }
    }

    public ProductAdapter(ArrayList<Product> dataSet, boolean removeVisibility, ProductOnClickListener productOnClickListener) {
        this.dataSet = dataSet;
        this.removeVisibility = removeVisibility;
        this.productOnClickListener = productOnClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout,parent,false);
        return new ProductViewHolder(view, productOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ImageView productImage = holder.productImage;
        TextView product = holder.product;
        TextView manufacturer = holder.manufacturer;
        TextView origin = holder.origin;
        TextView price = holder.price;
        ImageButton remove = holder.removeImage;
        if(removeVisibility)
            remove.setVisibility(View.GONE);
        productImage.setImageResource(dataSet.get(position).image);
        product.setText(dataSet.get(position).getName());
        manufacturer.setText(dataSet.get(position).getManufacturer());
        origin.setText(dataSet.get(position).getOrigin());
        price.setText(String.format("$ %s", dataSet.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface ProductOnClickListener {
        void onProductClick(int position);
        void onProductRemoveClick(int adPosition, int lyPosition);
    }
}
