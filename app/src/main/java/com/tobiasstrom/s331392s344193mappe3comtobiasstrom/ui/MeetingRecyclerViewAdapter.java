package com.tobiasstrom.s331392s344193mappe3comtobiasstrom.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.R;
import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.model.Meeting;

import java.util.List;

public class MeetingRecyclerViewAdapter extends RecyclerView.Adapter<MeetingRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "MeetingRecyclerViewAdap";

    private Context context;
    private List<Meeting> meetingList;
    private Meeting meeting;

    public MeetingRecyclerViewAdapter(Context context, List<Meeting> meetingList) {
        this.context = context;
        this.meetingList = meetingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meeting_list, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Meeting meeting = meetingList.get(position);
        holder.txtStartTime.setText(meeting.getStart().toString());
        holder.txtEndTime.setText(meeting.getEnd().toString());

    }

    @Override
    public int getItemCount() {
        return meetingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        final TextView txtStartTime;
        final TextView txtEndTime;


        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            this.txtStartTime = itemView.findViewById(R.id.txtStartTime);
            this.txtEndTime = itemView.findViewById(R.id.txtEndTime);

        }
    }
}
