package com.hcdc.e_announce;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Context context;
    private JSONArray events;
    private HomeScreenActivity homeScreenActivity = new HomeScreenActivity();

    public EventAdapter(Context context, JSONArray events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.event_holder, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        try {
            JSONObject event = events.getJSONObject(position);
            holder.event = event;
            holder.eventName.setText(event.getString("event_name"));
            holder.eventDate.setText(changeDateFormat(event.getString("date")));
            holder.eventTime.setText(event.getString("time"));
        } catch (JSONException e) {
            Log.e("onBindViewHolder", e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return events.length();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        JSONObject event;
        TextView eventName , eventDate, eventTime, readMore;
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventTime = itemView.findViewById(R.id.eventTime);
            readMore = itemView.findViewById(R.id.readMore);
            readMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Iterator<String> keys = event.keys();
                    Intent intent = new Intent(context, MoreInfoActivity.class);
                    while (keys.hasNext()) {
                        String key = keys.next();
                        try {
                            intent.putExtra(key, event.getString(key));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                    context.startActivity(intent);
                }
            });
        }
    }

    public String changeDateFormat(String date) {
        char month[] = {date.charAt(5), date.charAt(6)};
        char day[] = {date.charAt(8), date.charAt(9)};
        char year[] = {date.charAt(0), date.charAt(1), date.charAt(2), date.charAt(3)};
        date = homeScreenActivity.getMonthFormat(
                Integer.parseInt(String.copyValueOf(month))) + " " +
                String.copyValueOf(day) + ", " +
                String.copyValueOf(year);
        return date;
    }
}
