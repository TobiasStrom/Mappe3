package com.tobiasstrom.s331392s344193mappe3comtobiasstrom.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.R;
import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "MapsActivity";

    private LocationManager locationManager;
    private LocationListener locationListener;
    private BottomSheetDialogFragment bottomSheetDialog;

    private GoogleMap mMap;
    private Marker mOslomet;
    private Marker mP46;
    private Marker mP35;
    private Marker mP32;
    private Marker mP52;
    private Dialog myDialog;
    private Marker pressedMarker;
    private Marker mMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        List<Marker> markerList = new ArrayList<>();

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mOslomet = mMap.addMarker(new MarkerOptions().position(Constants.osloMet).title("Oslomet"));
        mOslomet.setTag(0);
        markerList.add(mOslomet);

        mP32 = mMap.addMarker(new MarkerOptions().position(Constants.p32).title("P32"));
        mP32.setTag(0);
        markerList.add(mP32);

        mP46 = mMap.addMarker(new MarkerOptions().position(Constants.p46).title("P46"));
        mP46.setTag(0);
        markerList.add(mP46);

        mP35 = mMap.addMarker(new MarkerOptions().position(Constants.p35).title("P35"));
        mP35.setTag(0);
        markerList.add(mP35);

        mP52 = mMap.addMarker(new MarkerOptions().position(Constants.p52).title("P52"));
        mP52.setTag(0);
        markerList.add(mP52);


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.osloMet,17));

        mMap.setOnMarkerClickListener(this);


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng point) {
                MarkerOptions options = new MarkerOptions()
                        .position(point);
                mMarker = mMap.addMarker(options);
                Log.e(TAG, "onMapLongClick: " + point );
                showPopup(-1);
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.e(TAG, "onMarkerClick: test");
        Integer clickCount = (Integer) marker.getTag();
        if (clickCount!= null){
            clickCount = clickCount + 1;

            marker.setTag(clickCount);

            //Toast.makeText(this, marker.getTitle() + " has been clicked " + clickCount + " times", Toast.LENGTH_SHORT).show();
            pressedMarker = marker;
            Log.e(TAG, "onMarkerClick: inne" );
            //Intent intent = new Intent(MapsActivity.this, BuildingActivity.class);
            //startActivity(intent);

        }
        showPopup(1);
        return false;
    }

    public void showPopup(int i){
        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.map_information);
        TextView mapAdress = myDialog.findViewById(R.id.mapAdress);
        Button openBuilding = myDialog.findViewById(R.id.openBuilding);
        if (i == -1){
            mapAdress.setText("Dette er en ny adresse");
            openBuilding.setText("Opprett Bygning");
        }else{
            mapAdress.setText(pressedMarker.getTitle());
            openBuilding.setText("Vis detaljer");
        }
        openBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, BuildingActivity.class);
                startActivity(intent);
            }
        });


        myDialog.show();
        Log.e(TAG, "showPopup: my dialog" );

    }
}