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

    private static final String TAG = "MapsActivity";

    private LocationManager locationManager;
    private LocationListener locationListener;

    private GoogleMap mMap;
    private Marker mOslomet;
    private Marker mP46;
    private Marker mP35;
    private Marker mP32;
    private Marker mP52;
    private Dialog myDialog;
    private Marker pressedMarker;
    private Marker mMarker;
    public Building newBuilding;
    public Building selectedBuilding = new Building();
    private List<Building> buildings = new ArrayList<>();
    private Map<String, Building> mapBulding = new HashMap<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getSavedLocation task= new getSavedLocation();
        task.execute(new String[]{"http://student.cs.hioa.no/~s344193/AppApi/getHus.php"});
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        List<Marker> markerList = new ArrayList<>();

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //mOslomet = mMap.addMarker(new MarkerOptions().position(Constants.osloMet).title("Oslomet"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.osloMet,17));

        mMap.setOnMarkerClickListener(this);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {

                //showPopup(-1);
                Geocoder gcd = new Geocoder(MapsActivity.this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(point.latitude, point.longitude, 1);
                    if (addresses.size() > 0)
                    {
                        Log.d(TAG, "onMapLongClick: " + addresses.toString());
                        if(addresses.get(0).getThoroughfare() == null || addresses.get(0).getThoroughfare().equals("Unnamed Road")){
                            Context context = getApplicationContext();
                            CharSequence text = "Det er ingen addresse på dette stedet!";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }else {
                            newBuilding = new Building();
                            newBuilding.setAddress(addresses.get(0).getThoroughfare());
                            newBuilding.setAddressNr(addresses.get(0).getFeatureName());
                            newBuilding.setPlace(addresses.get(0).getAdminArea());
                            newBuilding.setPostalNr(addresses.get(0).getPostalCode());
                            newBuilding.setLng(point.longitude);
                            newBuilding.setLat(point.latitude);

                            MarkerOptions options = new MarkerOptions()
                                    .position(point);
                            //mMarker = mMap.addMarker(options);
                            showPopup(null);
                        }
                    }
                    else
                    {
                        // do your staff
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Integer clickCount = (Integer) marker.getTag();
        //LatLng lng = marker.getPosition().longitude + 0.03;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),17));

        String url = "http://student.cs.hioa.no/~s344193/AppApi/getHus.php?gpsLat="+ marker.getPosition().latitude +"&gpsLong="+marker.getPosition().longitude;
        Log.e(TAG, "onMarkerClick: " + url );
        getBuilding task= new getBuilding();
        task.execute(new String[]{url});

        return false;
    }

    public void showPopup(Building building){

        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.map_information);

        Window window = myDialog.getWindow();
        WindowManager.LayoutParams wpl = window.getAttributes();

        wpl.gravity = Gravity.BOTTOM;
        wpl.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wpl);
        EditText txtTilte = myDialog.findViewById(R.id.txtTilte);
        TextView txtMapAdress = myDialog.findViewById(R.id.txtMapAdress);
        Button openBuilding = myDialog.findViewById(R.id.openBuilding);
        EditText txtFloors = myDialog.findViewById(R.id.txtFloors);
        EditText txtOpening = myDialog.findViewById(R.id.txtOpening);
        EditText txtClosing = myDialog.findViewById(R.id.txtClosing);
        EditText txtDescription = myDialog.findViewById(R.id.txtDescription);
        Button btnRoom = myDialog.findViewById(R.id.btnRoom);
        btnRoom.setEnabled(false);
        btnRoom.setText("Rom");



        if (building == null){
            openBuilding.setText("Opprett");
            txtMapAdress.setText(newBuilding.getAddress() + " " + newBuilding.getAddressNr()
                    + ", " + newBuilding.getPostalNr() + " " + newBuilding.getPlace());
            openBuilding.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String opening = txtOpening.getText().toString();
                    String closing = txtClosing.getText().toString();
                    String title = txtTilte.getText().toString();
                    String description = txtDescription.getText().toString();
                    String floors = txtFloors.getText().toString();
                    String titleEncode = "";
                    String descriptionEncode = "";
                    openBuilding.setText("Oppdater");
                    btnRoom.setEnabled(true);
                    boolean right = true;
                    mMap.addMarker(new MarkerOptions().position(newBuilding.getLatLng()).title(newBuilding.getTitle())).getId();
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
                    if(right){
                        String url = "http://student.cs.hioa.no/~s344193/AppApi/addHus.php?tittel="+ titleEncode +"&beskrivelse="+ descriptionEncode +"&gate="+newBuilding.getAddress() +"&gateNr="+ newBuilding.getAddressNr() +"&postNr="+newBuilding.getPostalNr() +"&postSted="+newBuilding.getPlace() +"&gpsLat="+ newBuilding.getLat()+"&gpsLong="+newBuilding.getLng()+"&antallEtasjer="+floors+"&aapenTid="+opening+":00:00&stengtTid="+closing+":00:00";
                        Log.e(TAG, "onClick: " + url );
                        url.replace(" ", "20%");
                        addBuilding task= new addBuilding();
                        task.execute(new String[]{url});
                    }
                }
            });
        }else{
            txtMapAdress.setText(building.getAddress() + " " + building.getAddressNr()
                    + ", " + building.getPostalNr() + " " + building.getPlace());
            txtTilte.setText(building.getTitle());
            txtFloors.setText(building.getFloors()+" ");
            txtOpening.setText(building.getOpening().getHours() +"");
            txtClosing.setText(building.getClosing().getHours()+ "");
            txtDescription.setText(building.getDescription());
            openBuilding.setText("Oppdater");
            btnRoom.setEnabled(true);

        }

        btnRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, RomActivity.class);
                if(building == null){
                    intent.putExtra("id", newBuilding.getId());
                }else {
                    intent.putExtra("id", building.getId());
                }
                MapsActivity.this.startActivity(intent);
            }
        });
        myDialog.show();

    }
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
                    System.out.println("Output from Server .... \n");
                    while ((s = br.readLine()) != null) {
                        output = output + s;
                    }
                    conn.disconnect();
                    try {
                        JSONArray building = new JSONArray(output);
                        for (int i = 0; i < building.length(); i++) {
                            JSONObject jsonobject = building.getJSONObject(i);
                            Building newBuilding = new Building();
                            newBuilding.setLat(jsonobject.getDouble("gpsLat"));
                            newBuilding.setLng(jsonobject.getDouble("gpsLong"));
                            buildings.add(newBuilding);
                        }
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
            for(Building building: buildings){
                mMap.addMarker(new MarkerOptions().position(building.getLatLng()).title(building.getAddress())).getId();
            }
        }


    }

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
                    System.out.println("Output from Server .... \n");
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
                        newBuilding = selectedBuilding;

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
            Log.e(TAG, "onPostExecute: " + selectedBuilding.toString() );
            showPopup(selectedBuilding);
        }

    }
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
            Log.e(TAG, "onPostExecute: Du har opprettet en bygning");

        }

    }
}