package com.tobiasstrom.s331392s344193mappe3comtobiasstrom.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.R;
import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.model.Building;
import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.model.Room;
import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.ui.RoomRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class RomActivity extends AppCompatActivity {
    private static final String TAG = "RomActivity";
    private ActionBar toolbar;
    private TextView txtBuildingTitle;
    private String idBuilding;
    private Room room;
    private List<Room> roomList = new ArrayList<>();
    private RoomRecyclerViewAdapter roomRecyclerViewAdapter;
    private Dialog myDialog;
    private Button addRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rom);
        toolbar = getSupportActionBar();
        //toolbar.setTitle("Hei");



        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            idBuilding = bundle.getString("id");
            Log.e(TAG, "onCreate: " + idBuilding );
        }
        String url = "http://student.cs.hioa.no/~s344193/AppApi/getRom.php?idHus=" + idBuilding;
        Log.e(TAG, "onCreate: " + url );
        getRoom task= new getRoom();
        task.execute(new String[]{url});

        addRoom = findViewById(R.id.addRoom);
        addRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(null);
                Log.e(TAG, "onClick: hei " );
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.e(TAG, "onOptionsItemSelected: her" );
        return  super.onOptionsItemSelected(item);
    }

    public class getRoom extends AsyncTask<String, Void,String> {
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
                        JSONArray roomArray = new JSONArray(output);
                        for (int i = 0; i < roomArray.length(); i++) {
                            JSONObject roomObject = roomArray.getJSONObject(i);
                            room = new Room();
                            String id = roomObject.getString("idRom");
                            String description = roomObject.getString("beskrivelse");
                            String roomNr = roomObject.getString("nummer");
                            String idHouse = roomObject.getString("Hus_idHus");
                            String capasity = roomObject.getString("kapasitet");
                            String floor = roomObject.getString("etasje");
                            room.setId(id);
                            room.setIdHouse(idHouse);
                            room.setDescription(description);
                            room.setRoomNr(roomNr);
                            room.setCapacity(capasity);
                            room.setFloorNr(floor);
                            roomList.add(room);
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
            RecyclerView recyclerView = findViewById(R.id.rvRoom);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(RomActivity.this));
            roomRecyclerViewAdapter = new RoomRecyclerViewAdapter(RomActivity.this, roomList);
            if(roomList.size() > 0){
                recyclerView.setAdapter(roomRecyclerViewAdapter);
            }

        }
    }
    public void showPopup(Room room){
        myDialog = new Dialog(this);

        myDialog.setContentView(R.layout.add_room);
        EditText txtRoomNr = myDialog.findViewById(R.id.txtRoomRomNr);
        EditText txtRoomCapasity = myDialog.findViewById(R.id.txtRoomRoomCapasity);
        EditText txtRomFloors = myDialog.findViewById(R.id.txtRoomRomFloors);
        EditText txtRoomDescription = myDialog.findViewById(R.id.txtRoomRoomDescription);
        Button btnAddRoom = myDialog.findViewById(R.id.btnAddRoom);

        if(room == null){

            btnAddRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String roomNr = txtRoomNr.getText().toString();
                    String roomCapasity = txtRoomCapasity.getText().toString();
                    String roomFloors = txtRomFloors.getText().toString();
                    String description = txtRoomDescription.getText().toString();
                    String roomNumberEncode = "";
                    String roomDescriptionEncode = "";
                    boolean right = true;

                    try {
                        roomNumberEncode = URLEncoder.encode(roomNr, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        right = false;
                    }
                    try {
                        roomDescriptionEncode = URLEncoder.encode(description, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        right = false;
                    }
                    if(right){
                        String url = "http://student.cs.hioa.no/~s344193/AppApi/addRom.php?idHus="+idBuilding+"&beskrivelse="+roomDescriptionEncode+"&nummer="+roomNumberEncode+"&kapasitet="+roomCapasity+"&etasje="+roomFloors;
                        Log.e(TAG, "onClick: " + url );
                        url.replace(" ", "20%");
                        addRoom task = new addRoom();
                        task.execute(new String[]{url});
                        myDialog.cancel();

                    }
                }
            });
            myDialog.show();
        }
    }
    public class addRoom extends AsyncTask<String, Void,String> {
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
            Log.e(TAG, "onPostExecute: Du har opprettet et rom");

        }

    }


}