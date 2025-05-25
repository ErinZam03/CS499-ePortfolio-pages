package com.example.inventoryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private List<InventoryItem> inventoryList;

    // Constructor
    public InventoryAdapter(List<InventoryItem> inventoryList) {
        this.inventoryList = inventoryList;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory_row, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem item = inventoryList.get(position);
        holder.itemName.setText(item.getName());
        holder.itemCategory.setText(item.getCategory());
        holder.itemStockLevel.setText(String.valueOf(item.getStockLevel()));
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    // ViewHolder class
    public static class InventoryViewHolder extends RecyclerView.ViewHolder {
        // Declare the TextView variables
        TextView itemName, itemCategory, itemStockLevel;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize the TextView variables
            itemName = itemView.findViewById(R.id.itemName);
            itemCategory = itemView.findViewById(R.id.itemCategory);
            itemStockLevel = itemView.findViewById(R.id.itemStockLevel);
        }
    }
}