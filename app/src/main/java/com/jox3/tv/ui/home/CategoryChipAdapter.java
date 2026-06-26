package com.jox3.tv.ui.home;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
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
    private String selectedCategory;

    public CategoryChipAdapter(List<String> categories, OnCategorySelected listener) {
        this.categories = categories;
        this.listener = listener;
        this.selectedCategory = categories.isEmpty() ? null : categories.get(0);
    }

    @NonNull
    @Override
    public ChipHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_chip, parent, false);
        view.setForeground(buildChipForeground());
        return new ChipHolder((TextView) view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChipHolder holder, int position) {
        String category = categories.get(position);
        holder.text.setText(category);

        boolean isSelected = category.equals(selectedCategory);
        holder.text.setBackgroundResource(
                isSelected ? R.drawable.bg_chip_active : R.drawable.bg_chip_inactive);
        holder.text.setTextColor(holder.text.getContext().getColor(
                isSelected ? R.color.text_primary : R.color.text_secondary));

        holder.text.setOnFocusChangeListener((v, hasFocus) -> {
            v.refreshDrawableState();
            if (hasFocus) {
                v.bringToFront();
                v.animate().scaleX(1.08f).scaleY(1.08f).setDuration(120).start();
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120).start();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            String previous = selectedCategory;
            selectedCategory = category;

            int previousIndex = categories.indexOf(previous);
            if (previousIndex >= 0) notifyItemChanged(previousIndex);
            notifyItemChanged(position);

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

        if (selectedCategory == null || !categories.contains(selectedCategory)) {
            selectedCategory = categories.isEmpty() ? null : categories.get(0);
        }
        notifyDataSetChanged();
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    private static Drawable buildChipForeground() {
        StateListDrawable selector = new StateListDrawable();
        selector.addState(
                new int[]{android.R.attr.state_focused},
                buildFocusRing());
        GradientDrawable transparent = new GradientDrawable();
        transparent.setColor(0x00000000);
        transparent.setCornerRadius(dp(10));
        selector.addState(new int[]{}, transparent);
        return selector;
    }

    private static Drawable buildFocusRing() {
        GradientDrawable dimBg = new GradientDrawable();
        dimBg.setShape(GradientDrawable.RECTANGLE);
        dimBg.setColor(0x3000FF88);
        dimBg.setCornerRadius(dp(10));

        GradientDrawable glow = new GradientDrawable();
        glow.setShape(GradientDrawable.RECTANGLE);
        glow.setStroke(dp(7), 0x4400FF88);
        glow.setCornerRadius(dp(10));
        glow.setColor(0x00000000);

        GradientDrawable ring = new GradientDrawable();
        ring.setShape(GradientDrawable.RECTANGLE);
        ring.setStroke(dp(3), 0xFF00FF88);
        ring.setCornerRadius(dp(10));
        ring.setColor(0x00000000);

        return new LayerDrawable(new Drawable[]{dimBg, glow, ring});
    }

    private static int dp(int value) {
        return (int) (value * android.content.res.Resources.getSystem().getDisplayMetrics().density);
    }

    static class ChipHolder extends RecyclerView.ViewHolder {
        TextView text;
        ChipHolder(@NonNull TextView itemView) {
            super(itemView);
            text = itemView;
        }
    }
}
