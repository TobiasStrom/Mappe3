package com.tobiasstrom.s331392s344193mappe3comtobiasstrom.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.R;
import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.activities.MeetingActivity;
import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.model.Room;

import java.util.List;

public class RoomRecyclerViewAdapter extends RecyclerView.Adapter<RoomRecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "RoomRecyclerViewAdapter";

    //Oppretter de varialblen vi trenger
    private Context context;
    private List<Room> roomList;
    private Room room;

    public RoomRecyclerViewAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room_list, parent, false);

        return new ViewHolder(view, context);
    }

    //Setter teksten ut i viewene
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.txtRoomNr.setText(room.getRoomNr());
        holder.txtCapasity.setText(room.getCapacity());
        holder.txtFloorNr.setText(room.getFloorNr());
        holder.txtRoomDescription.setText(room.getDescription());
    }
    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        final TextView txtRoomNr;
        final TextView txtCapasity;
        final TextView txtFloorNr;
        final TextView txtRoomDescription;

        public  int id;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            this.txtRoomNr = itemView.findViewById(R.id.txtRoomNr);
            this.txtCapasity = itemView.findViewById(R.id.txtCapasity);
            this.txtFloorNr = itemView.findViewById(R.id.txtFloorNr);
            this.txtRoomDescription = itemView.findViewById(R.id.txtRoomDescription);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    room = roomList.get(position);
                    Intent intent = new Intent(context, MeetingActivity.class);
                    intent.putExtra("id", room.getId());
                    context.startActivity(intent);
                }
            });
        }
    }

}
