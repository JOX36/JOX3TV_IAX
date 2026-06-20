package com.jox3.tv.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jox3.tv.R;

import java.util.List;

public class CategoryChipAdapter extends RecyclerView.Adapter<CategoryChipAdapter.ChipHolder> {

    public interface OnCategorySelected {
        void onSelected(String category);
    }

    private final List<String> categories;
    private final OnCategorySelected listener;
    private int selectedIndex = 0;

    public CategoryChipAdapter(List<String> categories, OnCategorySelected listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChipHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_chip, parent, false);
        return new ChipHolder((TextView) view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChipHolder holder, int position) {
        String category = categories.get(position);
        holder.text.setText(category);

        boolean isSelected = position == selectedIndex;
        holder.text.setBackgroundResource(
                isSelected ? R.drawable.bg_chip_active : R.drawable.bg_chip_inactive);
        holder.text.setTextColor(holder.text.getContext().getColor(
                isSelected ? R.color.text_primary : R.color.text_secondary));

        holder.itemView.setOnClickListener(v -> {
            int previous = selectedIndex;
            selectedIndex = position;
            notifyItemChanged(previous);
            notifyItemChanged(selectedIndex);
            if (listener != null) listener.onSelected(category);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void updateCategories(List<String> newCategories) {
        categories.clear();
        categories.addAll(newCategories);
        selectedIndex = 0;
        notifyDataSetChanged();
    }

    static class ChipHolder extends RecyclerView.ViewHolder {
        TextView text;
        ChipHolder(@NonNull TextView itemView) {
            super(itemView);
            text = itemView;
        }
    }
}
