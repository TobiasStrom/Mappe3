package com.tobiasstrom.s331392s344193mappe3comtobiasstrom.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.R;
import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.model.Meeting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.tobiasstrom.s331392s344193mappe3comtobiasstrom.util.Constants.selectedMeetings;
//Trenger denne klassen får å kunne legge inn den informsjonen som vi trenger i recyclerview
//På den måten jeg ønsker
public class MeetingRecyclerViewAdapter extends RecyclerView.Adapter<MeetingRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "MeetingRecyclerViewAdap";
    //opprette de variablene vi trenger
    private Context context;
    private List<Meeting> meetingList;
    private Meeting meeting;
    private SimpleDateFormat dateFormatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //Konstruktør
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
    //Setter teksten ut i viewene
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Meeting meeting = meetingList.get(position);
        if (meeting.isSelected()) {
            holder.txtStartTime.setTextColor(context.getColor(R.color.red));
            holder.txtEndTime.setTextColor(context.getColor(R.color.red));
        } else {
            holder.txtStartTime.setTextColor(context.getColor(R.color.defaultSecondText));
            holder.txtEndTime.setTextColor(context.getColor(R.color.defaultSecondText));

        }

        Context context = holder.itemView.getContext();
        holder.txtStartTime.setText(context.getString(R.string.meeting_startOut, dateFormat.format(meeting.getStart())));
        holder.txtEndTime.setText(context.getString(R.string.meeting_endOut, dateFormat.format(meeting.getEnd())));
    }
    //trenger å vite størelse til listen
    @Override
    public int getItemCount() {
        return meetingList.size();
    }

    //Trenger denne får å hente ut alle ViewIndexene som vi trenger få å setet inn.
    public class ViewHolder extends RecyclerView.ViewHolder{
        final TextView txtStartTime;
        final TextView txtEndTime;


        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            this.txtStartTime = itemView.findViewById(R.id.txtStartTime);
            this.txtEndTime = itemView.findViewById(R.id.txtEndTime);
            //Hvis du trykker på et av elementene.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //henter posisjoene til møte
                    int position = getAdapterPosition();
                    meeting = meetingList.get(position);
                    //Hvis møte allerede er valgt
                    if (meeting.isSelected()){
                        CharSequence text = "Du kan ikke velge et møte som allerede er valgt";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    } else { //Booker et møte og setter teksten
                        //put on selected style; put text color til red
                        txtStartTime.setTextColor(view.getResources().getColor(R.color.red));
                        txtEndTime.setTextColor(view.getResources().getColor(R.color.red));

                        String startEncode = "";
                        String endEncode = "";
                        String start = dateFormatDate.format(meeting.getStart());
                        String end = dateFormatDate.format(meeting.getEnd());
                        Log.e(TAG, "onClick: " + start + " " + end );
                        try {
                            startEncode = URLEncoder.encode(start, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        try {
                            endEncode = URLEncoder.encode(end, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        //Legger til møter
                        String url = "http://student.cs.hioa.no/~s344193/AppApi/addReservasjon.php?idRom="+meeting.getIdRoom()+"&startDato="+startEncode+"&sluttDato="+endEncode;
                        url.replace(" ", "20%");
                        Log.e(TAG, "onClick: " + url );
                        addMeeting task = new addMeeting();
                        task.execute(new String[]{url});
                    }
                }
            });
        }
    }
    //Legger til møter
    public class addMeeting extends AsyncTask<String, Void,String> {
        @Override
        protected String doInBackground(String... urls) {
            String retur = "";
            String s = "";
            String output = "";
            for (String url : urls) {
                try {
                    URL urlen = new URL(urls[0]);
                    HttpURLConnection conn = (HttpURLConnection)
                            urlen.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Accept",
                            "application/json");
                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + conn.getResponseCode());
                    }
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));
                    System.out.println("Output from Server .... \n");
                    while ((s = br.readLine()) != null) {
                        output = output + s;
                    }
                    conn.disconnect();
                    return retur;
                } catch (Exception e) {
                    return "Noe gikk feil";
                }
            }
            return retur;
        }
        @Override
        protected void onPostExecute(String ss) {
            Log.e(TAG, "onPostExecute: Du har opprettet et møte");
            //Setter møte som selected og legger det til i listen
            meeting.setSelected(true);
            selectedMeetings.add(meeting);
        }
    }
}
