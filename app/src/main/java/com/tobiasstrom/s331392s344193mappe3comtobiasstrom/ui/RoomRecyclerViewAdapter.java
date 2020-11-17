package com.tobiasstrom.s331392s344193mappe3comtobiasstrom.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.R;
import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.activities.MeetingActivity;
import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.model.Room;

import java.util.List;
//Trenger denne klassen får å kunne legge inn den informsjonen som vi trenger i recyclerview
//På den måten jeg ønsker
public class RoomRecyclerViewAdapter extends RecyclerView.Adapter<RoomRecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "RoomRecyclerViewAdapter";

    //Oppretter de varialblen vi trenger
    private Context context;
    private List<Room> roomList;
    private Room room;
    //Konstruktør
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
        Context context = holder.itemView.getContext();
        holder.txtRoomNr.setText(context.getString(R.string.roomNrOut, room.getRoomNr()));
        holder.txtCapasity.setText(context.getString(R.string.capasatyOut, room.getCapacity()));
        holder.txtFloorNr.setText(context.getString(R.string.floorOut, room.getFloorNr()));
        holder.txtRoomDescription.setText(context.getString(R.string.descriptionOut, room.getDescription()));
    }
    //trenger å vite størelse til listen
    @Override
    public int getItemCount() {
        return roomList.size();
    }

    //Trenger denne får å hente ut alle ViewIndexene som vi trenger få å setet inn.
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
            //Hvis du trykker på ett rom
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    room = roomList.get(position);
                    //Sender over informasjoen.
                    Intent intent = new Intent(context, MeetingActivity.class);
                    intent.putExtra("id", room.getId());
                    intent.putExtra("idHouse", room.getIdHouse());
                    intent.putExtra("roomName", room.getRoomNr());
                    context.startActivity(intent);
                    //Sender melding som sier at reservasjoer en binnende.
                    CharSequence text = "Reservasjoner er binnende";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //Sider at der ikke er mulig å avbestille.
                    CharSequence text = "For å slette må du kontakte admin";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    return false;
                }
            });
        }
    }

}
