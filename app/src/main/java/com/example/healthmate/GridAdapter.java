package com.example.healthmate;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import android.content.Intent;
import android.content.Context;
import java.util.Map;
public class GridAdapter extends BaseAdapter{
    private Context context;
    private String[] data;
    private int[] imageIds;
    private Map<String, String> detailedTerms;

    public GridAdapter(Context context, String[] data, int[] imageIds, Map<String, String> detailedTerms) {
        this.context = context;
        this.data = data;
        this.imageIds = imageIds;
        this.detailedTerms = detailedTerms;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.gridItemText);
        ImageView imageView = convertView.findViewById(R.id.gridItemImage);
        textView.setText(data[position]);
        imageView.setImageResource(imageIds[position]);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("title", data[position]);
                intent.putExtra("imageId", imageIds[position]);
                intent.putExtra("article", detailedTerms.get(data[position]));
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
