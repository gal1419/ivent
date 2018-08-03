package ivent.com.ivent.Adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import ivent.com.ivent.R;
import ivent.com.ivent.model.Picture;
import ivent.com.ivent.rest.AuthHeaders;

/**
 * Created by galmalachi on 06/04/2018.
 */

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Picture> imageList = new ArrayList<>();

    public GalleryAdapter(Context context, List<Picture> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.gallery_item, parent, false);
        viewHolder = new MyItemHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Picture image = imageList.get(position);
        //holder.imageTitle.setText(image.getDescription());

        // loading image using Glide library
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").encodedAuthority("10.0.2.2:8080")
                .appendPath("picture")
                .appendPath(String.valueOf(image.getId()));

        Glide.with(context)
                .load(AuthHeaders.getGlideUrlWithHeaders(builder.build().toString()))
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(((MyItemHolder) holder).mImg);

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView mImg;

        public MyItemHolder(View itemView) {
            super(itemView);

            mImg = (ImageView) itemView.findViewById(R.id.event_image_grid_image);
        }

    }
}