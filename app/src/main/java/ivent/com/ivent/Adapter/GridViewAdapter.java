package ivent.com.ivent.Adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ivent.com.ivent.R;
import ivent.com.ivent.model.Picture;
import ivent.com.ivent.rest.AuthHeaders;

/**
 * Created by galmalachi on 06/04/2018.
 */

public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private List<Picture> imageList = new ArrayList<>();

    public GridViewAdapter(Context context, int layoutResourceId, List<Picture> imageList) {
        super(context, layoutResourceId, imageList);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = row.findViewById(R.id.event_image_grid_text);
            holder.image = row.findViewById(R.id.event_image_grid_image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Picture image = imageList.get(position);
        holder.imageTitle.setText(image.getDescription());

        // loading image using Glide library
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").encodedAuthority("10.0.2.2:8080")
                .appendPath("picture")
                .appendPath(String.valueOf(image.getId()));

        Glide.with(context).load(AuthHeaders.getGlideUrlWithHeaders(builder.build().toString())).into(holder.image);
        return row;
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }
}