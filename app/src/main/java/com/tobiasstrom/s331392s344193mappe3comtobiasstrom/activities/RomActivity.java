package com.tobiasstrom.s331392s344193mappe3comtobiasstrom.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.Toast;

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

    //Setter de variablen vi trenger.
    private static final String TAG = "RomActivity";
    private String idBuilding;
    private int floorsBuilding;
    private Room room;
    private Toolbar myToolbar;
    private String buildingName;
    private List<Room> roomList = new ArrayList<>();
    private RoomRecyclerViewAdapter roomRecyclerViewAdapter;
    private Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rom);
        //Henter ut infoen soom ble sent over fra den andre aktiviteten.
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            idBuilding = bundle.getString("id");
            buildingName = bundle.getString("buildingName");
        }
        //Henter ut rom i et spesefikt hus.
        String url = "http://student.cs.hioa.no/~s344193/AppApi/getRom.php?idHus=" + idBuilding;
        Log.e(TAG, "onCreate: " + url );
        getRoom task= new getRoom();
        task.execute(new String[]{url});

        //Oppretter toolbar med tittel.
        myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle(buildingName);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        myToolbar.inflateMenu(R.menu.toolbar_menu);
        setSupportActionBar(myToolbar);

        //Henter ut hus får å finne etasjer.
        String urlHus = "http://student.cs.hioa.no/~s344193/AppApi/getHus.php?idHus="+ idBuilding;
        Log.e(TAG, "onCreate: " + urlHus);
        getBuilding taskBuilding = new getBuilding();
        taskBuilding.execute(new String[]{urlHus});

    }
    //Metoder for toolbaren.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }
    //Hva som skjer når man trykkel på knappen i toolbaren
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        showPopup(null);
        return super.onOptionsItemSelected(item);
    }

    //Henter ut alle rommene i bygget.
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
                    while ((s = br.readLine()) != null) {
                        output = output + s;
                    }
                    conn.disconnect();
                    try {
                        JSONArray roomArray = new JSONArray(output);
                        for (int i = 0; i < roomArray.length(); i++) {
                            //Henter ut room og legger dem inn i ett array.
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
            //Fyller opp RV hvis det er ellementer i listen
            Log.e(TAG, "onPostExecute: Har hentet ut room ");
            RecyclerView recyclerView = findViewById(R.id.rvRoom);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(RomActivity.this));
            roomRecyclerViewAdapter = new RoomRecyclerViewAdapter(RomActivity.this, roomList);
            if(roomList.size() > 0){
                recyclerView.setAdapter(roomRecyclerViewAdapter);
            }
            else {
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(RomActivity.this, "Oppret rom får å kunne booke møte", duration);
                toast.show();
            }

        }
    }
    //Viser poup
    public void showPopup(Room room){
        //Oppretter dialog
        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.add_room);

        //henter ut de elementene vi trenger
        EditText txtRoomNr = myDialog.findViewById(R.id.txtRoomRomNr);
        EditText txtRoomCapasity = myDialog.findViewById(R.id.txtRoomRoomCapasity);
        EditText txtRomFloors = myDialog.findViewById(R.id.txtRoomRomFloors);
        EditText txtRoomDescription = myDialog.findViewById(R.id.txtRoomRoomDescription);
        Button btnAddRoom = myDialog.findViewById(R.id.btnAddRoom);
        //Hvis room er null. Noe den alltid er. Gjør det mulig for update i fremtiden.
        if(room == null){
            btnAddRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Henter ut info og skjekker om inforen er riktig.
                    String text = "Her er det noe feil i informasjonen";
                    String roomNr = txtRoomNr.getText().toString();
                    String roomCapasity = txtRoomCapasity.getText().toString();
                    String roomFloors = txtRomFloors.getText().toString();
                    String description = txtRoomDescription.getText().toString();
                    String roomNumberEncode = "";
                    String roomDescriptionEncode = "";
                    boolean right = true;
                    if(txtRomFloors.getText().toString().isEmpty() || txtRoomNr.getText().toString().isEmpty() || txtRoomDescription.getText().toString().isEmpty() || txtRoomCapasity.getText().toString().isEmpty()){
                        right = false;
                        text += "\nDu må fylle alle feltene";
                    }else {
                        try {
                            int txtFloors = Integer.parseInt(roomFloors);
                            if(txtFloors>floorsBuilding){
                                right = false;
                                text += "\nFor mange etasjer maks er " + floorsBuilding;
                            }
                        }catch (NumberFormatException e){
                            right = false;
                        }
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
                        for(Room room1 : roomList){
                            if (room1.getRoomNr().equals(roomNr)){
                                right = false;
                                text += "\nRomnummeret finnes fra før";
                                break;
                            }
                        }
                        if(!description.matches("[a-zøæåA-ZÆØÅ_0-9 \\.\\,]+")){
                            text += "\nBeskrivelse kan bare indeholde tall og bokstaver";
                        }
                    }
                    //Legger til møte hvis infoen stemmer.
                    if(right){
                        String url = "http://student.cs.hioa.no/~s344193/AppApi/addRom.php?idHus="+idBuilding+"&beskrivelse="+roomDescriptionEncode+"&nummer="+roomNumberEncode+"&kapasitet="+roomCapasity+"&etasje="+roomFloors;
                        Log.e(TAG, "onClick: " + url );
                        url.replace(" ", "20%");
                        addRoom task = new addRoom();
                        task.execute(new String[]{url});
                        myDialog.cancel();

                    }else {
                        //Hviser dialog hvis infoen ikke stemmer
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(RomActivity.this, text, duration);
                        toast.show();
                    }
                }
            });
            myDialog.show();
        }
    }
    //Legger til room.
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
            String url = "http://student.cs.hioa.no/~s344193/AppApi/getRom.php?idHus=" + idBuilding;
            Log.e(TAG, "onCreate: " + url );
            roomList.clear();
            getRoom task= new getRoom();
            task.execute(new String[]{url});

        }

    }
    //Henter ut etasjer til bygningen.
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
                        floorsBuilding = jsonobject.getInt("antallEtasjer");
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

        }

    }


}