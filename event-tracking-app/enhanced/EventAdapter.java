package com.example.cs360projectonelynneamarihugh;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    public interface OnEventActionListener {
        void onEdit(Event event);
        void onDelete(Event event);
    }

    private final List<Event> events;
    private final OnEventActionListener listener;

    public EventAdapter(List<Event> events, OnEventActionListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.title.setText(event.getTitle());
        holder.date.setText(event.getDate());
        holder.time.setText(event.getTime());

        holder.editButton.setOnClickListener(v -> listener.onEdit(event));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(event));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void setEvents(List<Event> newEvents) {
        events.clear();
        events.addAll(newEvents);
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, time;
        Button editButton, deleteButton;

        EventViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.eventItemTitle);
            date = itemView.findViewById(R.id.eventItemDate);
            time = itemView.findViewById(R.id.eventItemTime);
            editButton = itemView.findViewById(R.id.eventItemEditButton);
            deleteButton = itemView.findViewById(R.id.eventItemDeleteButton);
        }
    }
}