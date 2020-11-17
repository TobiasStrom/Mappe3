package com.tobiasstrom.s331392s344193mappe3comtobiasstrom.activities;

import androidx.fragment.app.FragmentActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.R;
import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.model.Building;
import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    //Oppretter vaiablene vi trenger
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private Dialog myDialog;
    public Building newBuilding;
    public Building selectedBuilding = new Building();
    private List<Building> buildings = new ArrayList<>();

    //Metoden som vil gjøre med en gang
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //Henter mapFragmet
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Kjører metoden getSavedLocation
        getSavedLocation task= new getSavedLocation();
        task.execute(new String[]{"http://student.cs.hioa.no/~s344193/AppApi/getHus.php"});
    }
    //Nor karte er klart
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Bestemmer hvilke type kart
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //zommer inn til oslomet
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.osloMet,17));
        //Gjør sånn at popopen funker
        mMap.setOnMarkerClickListener(this);

        //Hvis du holder lenge på kartet
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                //Henter inn geocoder får å kovertere lokasjon til adresse
                Geocoder gcd = new Geocoder(MapsActivity.this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    //Hentere inn adresser for lokasjoenen
                    addresses = gcd.getFromLocation(point.latitude, point.longitude, 1);
                    if (addresses.size() > 0) {
                        //Skjekker om adressen finnes og skriver ut en toast hvis det ikke finnes
                        if(addresses.get(0).getThoroughfare() == null || addresses.get(0).getThoroughfare().equals("Unnamed Road")){
                            Context context = getApplicationContext();
                            CharSequence text = getString(R.string.no_adress);
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }else {
                            //Hvis den finnes oppretter den en newBuilding og legger inn verdien vi trenger.
                            newBuilding = new Building();
                            newBuilding.setAddress(addresses.get(0).getThoroughfare());
                            newBuilding.setAddressNr(addresses.get(0).getFeatureName());
                            newBuilding.setPlace(addresses.get(0).getAdminArea());
                            newBuilding.setPostalNr(addresses.get(0).getPostalCode());
                            newBuilding.setLng(point.longitude);
                            newBuilding.setLat(point.latitude);
                            //Oppreter en popup
                            showPopup(null);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Når du trykker på en marker så kjører den metoden getBuilding
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),17));
        String url = "http://student.cs.hioa.no/~s344193/AppApi/getHus.php?gpsLat="+ marker.getPosition().latitude +"&gpsLong="+marker.getPosition().longitude;
        Log.e(TAG, "onMarkerClick: " + url );
        getBuilding task= new getBuilding();
        task.execute(new String[]{url});
        return false;
    }
    //metode som lager popup med den informasjoen vi ønsker og tar inn bygning som verdi
    public void showPopup(Building building){
        //Oppretter dialogen
        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.map_information);

        //Gjør sånn at det ikke er grå bakgrunn
        Window window = myDialog.getWindow();
        WindowManager.LayoutParams wpl = window.getAttributes();
        wpl.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wpl);

        //Henter inn elementene vi trenger.
        EditText txtTilte = myDialog.findViewById(R.id.txtTilte);
        TextView txtMapAdress = myDialog.findViewById(R.id.txtMapAdress);
        Button openBuilding = myDialog.findViewById(R.id.openBuilding);
        EditText txtFloors = myDialog.findViewById(R.id.txtFloors);
        EditText txtOpening = myDialog.findViewById(R.id.txtOpening);
        EditText txtClosing = myDialog.findViewById(R.id.txtClosing);
        EditText txtDescription = myDialog.findViewById(R.id.txtDescription);
        Button btnRoom = myDialog.findViewById(R.id.btnRoom);
        //Setter tittel og deakriverer knapp
        btnRoom.setEnabled(false);
        btnRoom.setText(R.string.room);

        //Hvis metoden ikke når inn noen bygning.
        if (building == null){
            //Setter tittel på knapp og setter adressen.
            openBuilding.setText(R.string.create);
            txtMapAdress.setText(newBuilding.getAddress() + " " + newBuilding.getAddressNr()
                    + ", " + newBuilding.getPostalNr() + " " + newBuilding.getPlace());
            //hva som skjer når du trykker på kanppen opprett.
            openBuilding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Henter verdien var feltene
                    String text = getString(R.string.somthing_wrong_input);
                    String opening = txtOpening.getText().toString();
                    String closing = txtClosing.getText().toString();
                    String title = txtTilte.getText().toString();
                    String description = txtDescription.getText().toString();
                    String floors = txtFloors.getText().toString();
                    String titleEncode = "";
                    String descriptionEncode = "";

                    //innput validering
                    boolean right = true;
                    if(closing.isEmpty() || opening.isEmpty() || title.isEmpty() || description.isEmpty() || floors.isEmpty()){
                        right = false;
                        text += "\n" + getString(R.string.fill_all_field);
                    }
                    else {
                        if(!title.matches("[a-zøæåA-ZÆØÅ0-9_ \\.\\,]+")){
                            right = false;
                            text += "\n" + getString(R.string.only_number_letter);
                        }
                        try {
                            titleEncode = URLEncoder.encode(title, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            right = false;
                        }
                        try {
                            descriptionEncode = URLEncoder.encode(description, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            right = false;
                        }

                        int openingInt = Integer.parseInt(opening);
                        int closingInt = Integer.parseInt(closing);
                        if(openingInt >= closingInt){
                            right = false;
                            text += "\n" +
                                    getString(R.string.opening_before);
                        }
                        if(openingInt < 0){
                            right = false;
                            text += "\n" +
                                    getString(R.string.open_aftere_00);
                        }
                        if (openingInt > 23){
                            right = false;
                            text += "\n" +
                                    getString(R.string.not_open_after_23);
                        }
                        if (closingInt<0){
                            right = false;
                            text += "\n" +
                                    getString(R.string.close_after_00);
                        }
                        if (closingInt > 23) {
                            right = false;
                            text += "\n" +
                                    getString(R.string.close_before_23);
                        }
                        if(!description.matches("[a-zøæåA-ZÆØÅ_0-9 \\.\\,]+")){
                            right = false;
                            text += "\n" +
                                    getString(R.string.description_only_letter_and_number);
                        }

                    }
                    //Hvis inputvailderingen går igjennom.
                    if(right){
                        String url = "http://student.cs.hioa.no/~s344193/AppApi/addHus.php?tittel="+ titleEncode +"&beskrivelse="+ descriptionEncode +"&gate="+newBuilding.getAddress() +"&gateNr="+ newBuilding.getAddressNr() +"&postNr="+newBuilding.getPostalNr() +"&postSted="+newBuilding.getPlace() +"&gpsLat="+ newBuilding.getLat()+"&gpsLong="+newBuilding.getLng()+"&antallEtasjer="+floors+"&aapenTid="+opening+":00:00&stengtTid="+closing+":00:00";
                        String url2 = "http://student.cs.hioa.no/~s344193/AppApi/getHus.php?gpsLat="+ newBuilding.getLat() +"&gpsLong="+newBuilding.getLng();
                        Log.e(TAG, "onClick: " + url );
                        url.replace(" ", "20%");
                        //Legger til byggning og og henter ut bygingen den er på få å bruke den senere
                        addBuilding task= new addBuilding();
                        task.execute(new String[]{url});
                        getBuilding task2 = new getBuilding();
                        task2.execute(new String[]{url2});
                        //Legger til marker.
                        mMap.addMarker(new MarkerOptions().position(newBuilding.getLatLng()).title(newBuilding.getTitle()));
                        //Gjør så man kan trykke på rom knappen
                        openBuilding.setEnabled(false);
                        btnRoom.setEnabled(true);

                    }else { //Hvis det ikke er godskjent input
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(MapsActivity.this, text, duration);
                        toast.show();
                    }
                }
            });
        }else{
            //Hvis bygningen finnes fra før. Så fyller den feltene. og setter knappen som enabled.
            txtMapAdress.setText(building.getAddress() + " " + building.getAddressNr()
                    + ", " + building.getPostalNr() + " " + building.getPlace());
            txtTilte.setText(building.getTitle());
            txtFloors.setText(building.getFloors()+" ");
            txtOpening.setText(building.getOpening().getHours() +"");
            txtClosing.setText(building.getClosing().getHours()+ "");
            txtDescription.setText(building.getDescription());
            openBuilding.setEnabled(false);
            btnRoom.setEnabled(true);
        }
        //Når man trykker på rom knappen så sender dne over litt informajon til den nye aktiviteten
        btnRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, RomActivity.class);
                if(building == null){
                    intent.putExtra("id", newBuilding.getId());
                    intent.putExtra("floorsBuilding", newBuilding.getFloors());
                }else {
                    intent.putExtra("id", building.getId());
                    intent.putExtra("buildingName", building.getTitle());
                    intent.putExtra("floorsBuilding", building.getFloors());
                }
                MapsActivity.this.startActivity(intent);
            }
        });
        //Hviser dialogen
        myDialog.show();

    }
    //Henter alle de lagrede lokasjoenen.
    private class getSavedLocation extends AsyncTask<String, Void,String> {
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
                    conn.setRequestMethod("GET");
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
                        for (int i = 0; i < building.length(); i++) {
                            //Legger alle elementen inn i et array sånn de kan skrives ut.
                            JSONObject jsonobject = building.getJSONObject(i);
                            Building newBuilding = new Building();
                            newBuilding.setLat(jsonobject.getDouble("gpsLat"));
                            newBuilding.setLng(jsonobject.getDouble("gpsLong"));
                            newBuilding.setTitle(jsonobject.getString("tittel"));
                            buildings.add(newBuilding);
                        }
                        return retur;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return retur;
                } catch (Exception e) {
                    return getString(R.string.went_wrong);
                }
            }
            return retur;
        }
        @Override
        protected void onPostExecute(String ss) {
            for(Building building: buildings){
                //Legger ellementene inn i kartet
                mMap.addMarker(new MarkerOptions().position(building.getLatLng()).title(building.getTitle())).getId();
            }
        }
    }

    //Henter ut en spesefik bygning
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
                        //Oppretter en byggning og fyller den opp.
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
                        newBuilding = selectedBuilding;

                        return retur;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return retur;
                } catch (Exception e) {
                    return getString(R.string.went_wrong);
                }
            }
            return retur;
        }
        @Override
        protected void onPostExecute(String ss) {
            //Viser popup med bygnigen.
            showPopup(selectedBuilding);
        }

    }
    //Legger til bygningen
    public class addBuilding extends AsyncTask<String, Void,String> {
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
                    return getString(R.string.went_wrong);
                }
            }
            return retur;
        }
        @Override
        protected void onPostExecute(String ss) {
            //Skriver ut en melding i loggen som bekrefter at det har skjedd noe.
            Log.e(TAG, "onPostExecute: " +
                    getString(R.string.greated_a_building));
        }

    }
}