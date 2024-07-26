package com.example.healthmate;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import androidx.recyclerview.widget.RecyclerView;
public class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.OverviewViewHolder> {
    private Context context;
    private ArrayList<OverviewItem> overviewItems;

    public OverviewAdapter(Context context, ArrayList<OverviewItem> overviewItems) {
        this.context = context;
        this.overviewItems = overviewItems;
    }

    @Override
    public OverviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.overviewlist_item, parent, false);
        return new OverviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OverviewViewHolder holder, int position) {
        OverviewItem currentItem = overviewItems.get(position);

        holder.itemImage.setImageResource(currentItem.getListImage());
        if (currentItem.getTitle().equals("Heart Rate")){
            holder.itemImage.setColorFilter(ContextCompat.getColor(context, R.color.heartrate));
        }
        else if (currentItem.getTitle().equals("Blood Sugar")){
            holder.itemImage.setBackgroundColor(ContextCompat.getColor(context, R.color.bloodpressure));
        }

        holder.itemTitle.setText(currentItem.getTitle());
        holder.itemSubtitle.setText(currentItem.getLastUpdated());
    }

    @Override
    public int getItemCount() {
        return overviewItems.size();
    }

    public class OverviewViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemTitle;
        TextView itemSubtitle;

        public OverviewViewHolder(View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemSubtitle = itemView.findViewById(R.id.item_subtitle);
        }
    }
}