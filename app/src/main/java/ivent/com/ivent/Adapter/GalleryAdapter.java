package ivent.com.ivent.Adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import ivent.com.ivent.R;
import ivent.com.ivent.model.Picture;
import ivent.com.ivent.service.Utils;

/**
 * Created by galmalachi on 06/04/2018.
 */

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Picture> imageList = new ArrayList<>();
    private List<Long> selectedIds = new ArrayList<>();


    public GalleryAdapter(Context context, List<Picture> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_item, parent, false);
        return new MyItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Picture image = imageList.get(position);

        if (selectedIds.contains(image.getId())) {
            //if item is selected then,set foreground color of FrameLayout.
            ((MyItemHolder) holder).mImg.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.selectedImageColor)));
        } else {
            //else remove selected item color.
            ((MyItemHolder) holder).mImg.setForeground(new ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)));
        }

        List<String> paths = new ArrayList<>();
        paths.add("picture");
        paths.add(String.valueOf(image.getId()));
        Utils.downloadWithGlide(paths, context, ((MyItemHolder) holder).mImg);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public Picture getItem(int position) {
        return imageList.get(position);
    }

    public void setSelectedIds(List<Long> selectedIds) {
        this.selectedIds = selectedIds;
        notifyDataSetChanged();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView mImg;

        public MyItemHolder(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.event_image_grid_image);
        }

    }
}