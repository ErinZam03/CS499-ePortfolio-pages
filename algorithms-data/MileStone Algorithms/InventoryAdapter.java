package com.example.inventoryapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// Adapter for displaying and managing inventory items in a RecyclerView
public class InventoryAdapter extends ListAdapter<InventoryItem, InventoryAdapter.InventoryViewHolder> {

    // Holds the full list of inventory items for filtering/sorting operations
    private List<InventoryItem> currentFullList;

    // Constructor that connects the adapter with DiffUtil for efficient updates
    public InventoryAdapter() {
        super(DIFF_CALLBACK);
    }

    // Updates the full item list and submits it to the adapter for display
    public void updateList(List<InventoryItem> fullList) {
        this.currentFullList = fullList;
        submitList(fullList);
    }

    // Filters and sorts the item list based on search text, category, and sorting option
    public void sortAndFilter(String query, String categoryFilter, String sortOption) {
        List<InventoryItem> filtered = currentFullList.stream()
                .filter(item -> item.getName().toLowerCase().contains(query.toLowerCase()))
                .filter(item -> categoryFilter.equalsIgnoreCase("All") || item.getCategory().equalsIgnoreCase(categoryFilter))
                .collect(Collectors.toList());

        // Apply sorting based on the selected sort option
        if (sortOption.equals("Name A→Z")) {
            Collections.sort(filtered, Comparator.comparing(InventoryItem::getName));
        } else if (sortOption.equals("Name Z→A")) {
            Collections.sort(filtered, (a, b) -> b.getName().compareTo(a.getName()));
        } else if (sortOption.equals("Stock Low→High")) {
            Collections.sort(filtered, Comparator.comparingInt(InventoryItem::getStockLevel));
        } else if (sortOption.equals("Stock High→Low")) {
            Collections.sort(filtered, (a, b) -> Integer.compare(b.getStockLevel(), a.getStockLevel()));
        }

        // Submit the filtered and sorted list to update the UI
        submitList(filtered);
    }

    // Inflates the item view layout and returns a ViewHolder
    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory_row, parent, false);
        return new InventoryViewHolder(view);
    }

    // Binds data to the views in the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem item = getItem(position);
        holder.itemName.setText(item.getName());
        holder.itemCategory.setText(item.getCategory());
        holder.itemStockLevel.setText(String.valueOf(item.getStockLevel()));

        // Display colored indicator based on stock level
        int stock = item.getStockLevel();
        if (stock <= 5) {
            holder.stockIndicator.setBackgroundResource(R.drawable.circle_indicator_red);
        } else if (stock <= 10) {
            holder.stockIndicator.setBackgroundResource(R.drawable.circle_indicator_yellow);
        } else {
            holder.stockIndicator.setBackgroundResource(R.drawable.circle_indicator_green);
        }

        // Set up edit button to open EditItemActivity with item ID
        holder.editButton.setOnClickListener(v -> {
            Intent editIntent = new Intent(v.getContext(), EditItemActivity.class);
            editIntent.putExtra("ITEM_ID", item.getId());
            v.getContext().startActivity(editIntent);
        });
    }

    // ViewHolder class for caching item views
    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemCategory, itemStockLevel;
        View stockIndicator;
        Button editButton;

        InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemCategory = itemView.findViewById(R.id.itemCategory);
            itemStockLevel = itemView.findViewById(R.id.itemStockLevel);
            stockIndicator = itemView.findViewById(R.id.stockIndicator);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }

    // DiffUtil logic for detecting item changes efficiently
    public static final DiffUtil.ItemCallback<InventoryItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<InventoryItem>() {
        // Check if two items represent the same entry based on ID
        @Override
        public boolean areItemsTheSame(@NonNull InventoryItem oldItem, @NonNull InventoryItem newItem) {
            return oldItem.getId() == newItem.getId();
        }

        // Check if the content of two items is the same
        @Override
        public boolean areContentsTheSame(@NonNull InventoryItem oldItem, @NonNull InventoryItem newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getCategory().equals(newItem.getCategory()) &&
                    oldItem.getStockLevel() == newItem.getStockLevel();
        }
    };
}
