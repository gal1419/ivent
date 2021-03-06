package ivent.com.ivent.Adapter;

/**
 * Created by galmalachi on 10/02/2018.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ivent.com.ivent.R;
import ivent.com.ivent.activity.EventDetailsActivity;
import ivent.com.ivent.activity.GalleryActivity;
import ivent.com.ivent.model.Event;
import ivent.com.ivent.service.Utils;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    private Context context;
    private List<Event> eventList;

    public EventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventId = event.getId();
        holder.title.setText(event.getTitle());
        holder.count.setText(String.format("%d participants", event.getParticipants().size()));

        List<String> paths = new ArrayList<>();
        paths.add("event");
        paths.add("thumbnail");
        paths.add(String.valueOf(event.getId()));

        Utils.downloadWithGlide(paths, context, holder.thumbnail);

        holder.overflow.setOnClickListener(view -> showPopupMenu(holder.overflow, position));
    }


    private void showPopupMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.setOnMenuItemClickListener(menuItem -> {
            Intent intent;

            switch (menuItem.getItemId()) {
                case R.id.action_quick_upload:
                    intent = new Intent(context, GalleryActivity.class);
                    intent.putExtra("shouldOpenPicker", true);
                    intent.putExtra("eventId", eventList.get(position).getId());
                    intent.putExtra("eventTitle", eventList.get(position).getTitle());
                    context.startActivity(intent);
                    return true;
                case R.id.action_view_event_details:
                    intent = new Intent(context, EventDetailsActivity.class);
                    intent.putExtra("event", eventList.get(position));
                    intent.putExtra("enableCheckIn", false);
                    ((Activity) context).startActivityForResult(intent, 2);
                    return true;
                default:
            }
            return false;

        });

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.manu_event, popup.getMenu());
        popup.show();
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private Long eventId;
        private TextView title;
        private TextView count;
        private ImageView thumbnail;
        private ImageView overflow;


        public MyViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.title);
            count = view.findViewById(R.id.count);
            thumbnail = view.findViewById(R.id.thumbnail);
            overflow = view.findViewById(R.id.overflow);

            view.setOnClickListener(v -> {
                Intent intent = new Intent(context, GalleryActivity.class);
                intent.putExtra("eventId", eventId);
                intent.putExtra("eventTitle", title.getText());
                intent.putExtra("shouldOpenPicker", false);
                context.startActivity(intent);
            });
        }
    }
}