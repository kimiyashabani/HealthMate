package com.example.healthmate;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryViewHolder extends RecyclerView.ViewHolder{
    private TextView categoryName;
    private ImageView categoryImage;
    private Context context;

    public CategoryViewHolder(@NonNull View itemView, Context context, final OnItemClickListener listener) {
        super(itemView);
        this.context = context;
        categoryImage = itemView.findViewById(R.id.cat_img);
        categoryName = itemView.findViewById(R.id.cat_title);

        itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            }
        });
    }
    public void bind(CategoryItem category){
        categoryImage.setImageResource(category.getPicPath());
        //categoryImage.setColorFilter(category.getBackgroundColor());
        categoryName.setText(category.getCategory());
        if (category.getCategory().equals("Heart rate")){
            itemView.setBackgroundResource(R.drawable.heart_rate_bg);
            categoryImage.setColorFilter(context.getResources().getColor(R.color.heartrate));
        } else if (category.getCategory().equals("Sleep")) {
            itemView.setBackgroundResource(R.drawable.sleep_bg);
            categoryImage.setColorFilter(context.getResources().getColor(R.color.sleep));
        }else if (category.getCategory().equals("Weight")) {
            itemView.setBackgroundResource(R.drawable.weight_bg);
            categoryImage.setColorFilter(context.getResources().getColor(R.color.weight));
        }else if (category.getCategory().equals("Blood Pressure")) {
            itemView.setBackgroundResource(R.drawable.blood_pressure_bg);
            categoryImage.setColorFilter(context.getResources().getColor(R.color.bloodpressure));
        }else if (category.getCategory().equals("Temperature")) {
            itemView.setBackgroundResource(R.drawable.temperature);
            categoryImage.setColorFilter(context.getResources().getColor(R.color.temperature));
        }else if (category.getCategory().equals("Medication")) {
            itemView.setBackgroundResource(R.drawable.medication_bg);
            categoryImage.setColorFilter(context.getResources().getColor(R.color.temperature));
        }
    }
}
