package com.tobiasstrom.s331392s344193mappe3comtobiasstrom.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.R;
import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.model.Meeting;
import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.model.Room;
import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.ui.MeetingRecyclerViewAdapter;
import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.ui.RoomRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MeetingActivity extends AppCompatActivity {
    private static final String TAG = "MeetingActivity";
    private String idRoom;
    private Meeting meeting;
    private List<Meeting> meetingList = new ArrayList<>();
    private MeetingRecyclerViewAdapter recyclerViewAdapter;
    private Button addMeetingRoom;
    private Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            idRoom = bundle.getString("id");
        }
        Log.e(TAG, "onCreate: "+ idRoom );

        String url = "http://student.cs.hioa.no/~s344193/AppApi/getReservasjon.php?idRom=" + idRoom;
        Log.e(TAG, "onCreate: "+ url);
        getMeeting task = new getMeeting();
        task.execute(new String[]{url});
        addMeetingRoom = findViewById(R.id.addMeetingRoom);

        addMeetingRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(null);
                Log.e(TAG, "onClick: har jeg trykket " );
            }
        });


    }
    public class getMeeting extends AsyncTask<String, Void,String> {
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
                    try {
                        JSONArray meetingArray = new JSONArray(output);
                        for (int i = 0; i < meetingArray.length(); i++) {
                            JSONObject meetingObject = meetingArray.getJSONObject(i);
                            meeting = new Meeting();
                            String id = meetingObject.getString("idReservasjon");
                            String idRoom = meetingObject.getString("Rom_idRom");
                            String startTime = meetingObject.getString("startDato");
                            String endTime = meetingObject.getString("sluttDato");
                            meeting.setId(id);
                            meeting.setIdRoom(idRoom);
                            meeting.setStart(startTime);
                            meeting.setEnd(endTime);
                            meetingList.add(meeting);
                        }
                        return retur;
                    } catch (JSONException e) {
                        Log.e(TAG, "doInBackground: Det er ingen elementer i listen");
                        //e.printStackTrace();
                    }
                    return retur;
                } catch (Exception e) {
                    return "Noe gikk feil";
                }
            }
            return retur;
        }

        @Override
        protected void onPostExecute(String ss) {
            Log.e(TAG, "onPostExecute: Har hentet ut room ");
            RecyclerView recyclerView = findViewById(R.id.rvMeeting);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(MeetingActivity.this));
            recyclerViewAdapter = new MeetingRecyclerViewAdapter(MeetingActivity.this, meetingList);
            if (meetingList.size() > 0) {
                recyclerView.setAdapter(recyclerViewAdapter);
            }

        }
    }
    public void showPopup(Meeting meeting){
        myDialog = new Dialog(this);

        myDialog.setContentView(R.layout.add_meeting);
        EditText txtMeetingStartTime = myDialog.findViewById(R.id.txtMeetingStart);
        EditText txtMeetingEndTime = myDialog.findViewById((R.id.txtMeetingMeetingEndTime));
        Button addMeeting = myDialog.findViewById(R.id.addMeeting);
        if(meeting == null){
            addMeeting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String startTime = txtMeetingStartTime.getText().toString();
                    String endtime = txtMeetingEndTime.getText().toString();
                    String url = "http://student.cs.hioa.no/~s344193/AppApi/addReservasjon.php?idRom=0&startDato=YYYY-MM-DD20%HH:mm:SS&sluttDato=YYYY-MM-DD HH:mm:SS";
                    Log.e(TAG, "onClick: " + url );
                }

            });

        }
        myDialog.show();

    }
}