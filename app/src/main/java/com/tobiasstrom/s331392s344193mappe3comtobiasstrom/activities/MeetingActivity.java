package com.tobiasstrom.s331392s344193mappe3comtobiasstrom.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.R;
import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.model.Building;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.tobiasstrom.s331392s344193mappe3comtobiasstrom.util.Constants.selectedMeetings;

public class MeetingActivity extends AppCompatActivity {

    //Variablen vi trenger.
    private static final String TAG = "MeetingActivity";
    private String idRoom;
    private String idHouse;
    private Meeting meeting;
    private int houseOpening;
    private int houseClosing;
    private List<Meeting> meetingList = new ArrayList<>();
    private MeetingRecyclerViewAdapter recyclerViewAdapter;
    private ImageButton btnLast;
    private ImageButton btnNext;
    private TextView txtDate;
    public Calendar calendar;
    //får å formatere dato og tid.
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private SimpleDateFormat dateFormatUt = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dateFormatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String date;
    private String roomName;
    public Building selectedBuilding = new Building();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);
        txtDate = findViewById(R.id.txtDate);
        btnLast = findViewById(R.id.btnLast);
        btnNext = findViewById(R.id.btnNext);

        calendar = Calendar.getInstance();
        date = dateFormat.format(calendar.getTime());
        txtDate.setText(date);
        //Henter ut verdiene som ble sendt over.
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            idRoom = bundle.getString("id");
            idHouse = bundle.getString("idHouse");
            roomName = bundle.getString("roomName");
        }
        String ut = dateFormatUt.format(calendar.getTime());
        //Henter ut reservasjoner til dagen som er valgt.
        String url = "http://student.cs.hioa.no/~s344193/AppApi/getReservasjon.php?idRom=" + idRoom+"&day=" + ut;
        Log.e(TAG, "onCreate: "+ url);
        getMeeting task = new getMeeting();
        task.execute(new String[]{url});
        //Henter ut hus sånn at det kan hentes ut åpeningstid til bygningen
        String urlHus = "http://student.cs.hioa.no/~s344193/AppApi/getHus.php?idHus="+ idHouse;
        Log.e(TAG, "onCreate: " + urlHus);
        getBuilding taskBuilding = new getBuilding();
        taskBuilding.execute(new String[]{urlHus});

        //Når du bytter til neste dag
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextDay();
            }
        });
        //Når du bytter til forrige dag
        btnLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastDay();
            }
        });

        //Setter toolbar og setter tittel
        Toolbar myToolbar = findViewById(R.id.toolbar2);
        myToolbar.setTitle("RomNr: " + roomName);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(myToolbar);

    }

    //Henter ut alle møtene og legger det i array
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
                            meeting.setSelected(true);
                            selectedMeetings.add(meeting);
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
            //Bygger liste med elementene som ble hentet ut.
            buildList();
        }
    }

    //Metoden neste dag som endrer valgt dag å henter ut informasjonen.
    public void nextDay(){
        selectedMeetings.clear();
        calendar.add(Calendar.DATE, 1);
        date = dateFormat.format(calendar.getTime());
        txtDate.setText(date);
        String ut = dateFormatUt.format(calendar.getTime());
        String url = "http://student.cs.hioa.no/~s344193/AppApi/getReservasjon.php?idRom=" + idRoom+"&day=" + ut;
        Log.e(TAG, "onCreate: "+ url);
        getMeeting task = new getMeeting();
        task.execute(new String[]{url});

    }
    //Metode lastDay som endrer valgt dag og henter informasjon.
    public void lastDay(){
        selectedMeetings.clear();
        calendar.add(Calendar.DATE, -1);
        date = dateFormat.format(calendar.getTime());
        txtDate.setText(date);
        String ut = dateFormatUt.format(calendar.getTime());
        String url = "http://student.cs.hioa.no/~s344193/AppApi/getReservasjon.php?idRom=" +idRoom+"&day=" + ut;
        Log.e(TAG, "onCreate: "+ url);
        getMeeting task = new getMeeting();
        task.execute(new String[]{url});

    }
    //Fyller opp listview med de elementene som er valgt.
    public void populateRV(List<Meeting> listMeeting){
        RecyclerView recyclerView = findViewById(R.id.rvMeeting);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MeetingActivity.this));
        recyclerViewAdapter = new MeetingRecyclerViewAdapter(MeetingActivity.this, listMeeting);
        if (listMeeting.size() > 0) {
            recyclerView.setAdapter(recyclerViewAdapter);
        }
        recyclerViewAdapter.notifyDataSetChanged();
    }
    //Henter ut en byggning.
    public class getBuilding extends AsyncTask<String, Void,String> {
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
                    while ((s = br.readLine()) != null) {
                        output = output + s;
                    }
                    conn.disconnect();
                    try {
                        JSONArray building = new JSONArray(output);
                        JSONObject jsonobject = building.getJSONObject(0);
                        selectedBuilding = new Building();
                        String id = jsonobject.getString("idHus");
                        String tittel = jsonobject.getString("tittel");
                        String gate = jsonobject.getString("gate");
                        String beskrivelse = jsonobject.getString("beskrivelse");
                        String gateNr = jsonobject.getString("gateNr");
                        String postNr = jsonobject.getString("postNummer");
                        String poststed = jsonobject.getString("postSted");
                        Double lat = jsonobject.getDouble("gpsLat");
                        Double lng = jsonobject.getDouble("gpsLong");
                        int etasjer = jsonobject.getInt("antallEtasjer");
                        String tidStart = jsonobject.getString("aapenTid");
                        String tidStenge = jsonobject.getString("stengtTid");
                        selectedBuilding.setId(id);
                        selectedBuilding.setTitle(tittel);
                        selectedBuilding.setAddress(gate);
                        selectedBuilding.setAddressNr(gateNr);
                        selectedBuilding.setPostalNr(postNr);
                        selectedBuilding.setPlace(poststed);
                        selectedBuilding.setLat(lat);
                        selectedBuilding.setLng(lng);
                        selectedBuilding.setFloors(etasjer);
                        selectedBuilding.setDescription(beskrivelse);
                        selectedBuilding.setOpening(tidStart);
                        selectedBuilding.setClosing(tidStenge);

                        return retur;
                    } catch (JSONException e) {
                        e.printStackTrace();
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
            //Setter oppningstid
            houseOpening = selectedBuilding.getOpening().getHours();
            houseClosing = selectedBuilding.getClosing().getHours();
            buildList();
        }
    }
    //Bygger en liste med ellementene vi trenger.
    public void buildList() {
        //Fjerner ellementene som der der inne
        meetingList.clear();
        //Går igjennom like mange ganger som opningtiden eksisterer.
        for(int i = houseOpening; i < houseClosing; i++){
            //Oppretter et nytt møte på en time.
            Meeting newMeeting = new Meeting();
            newMeeting.setIdRoom(idRoom);
            Calendar newCaledar = calendar;
            newCaledar.set(Calendar.HOUR_OF_DAY,i);
            newCaledar.set(Calendar.MINUTE, 0);
            newCaledar.set(Calendar.SECOND,0);
            String start = dateFormatDate.format(newCaledar.getTime());
            newMeeting.setStart(start);
            int to = i + 1;
            newCaledar.set(Calendar.HOUR_OF_DAY,to);
            String end = dateFormatDate.format(newCaledar.getTime());
            newMeeting.setEnd(end);
            //Hvis det er noe elemeter i selectedMeeting i som er møter den dagen.
            if(selectedMeetings.size()>0){
                //Går igjennom vært element i listen
                for (Meeting meeting : selectedMeetings){
                    //hvis tiden er det samme så setter den inn møte.
                    if (newMeeting.getStart().compareTo(meeting.getStart()) == 0){
                        newMeeting = meeting;
                        break;
                    }
                }
            }
            //Legger inn elementene
            meetingList.add(newMeeting);
        }
        //Bygger RV
        populateRV(meetingList);
    }
}