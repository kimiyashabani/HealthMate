package com.example.healthmate;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryViewHolder extends RecyclerView.ViewHolder{
    private TextView categoryName;
    private ImageView categoryImage;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        categoryImage = itemView.findViewById(R.id.cat_img);
        categoryName = itemView.findViewById(R.id.cat_title);
    }
    public void bind(CategoryDomain category){
        categoryImage.setImageResource(category.getPicPath());
        categoryImage.setColorFilter(category.getBackgroundColor());
        categoryName.setText(category.getCategory());
    }
}
