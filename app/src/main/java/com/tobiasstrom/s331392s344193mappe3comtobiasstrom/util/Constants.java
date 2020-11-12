package com.tobiasstrom.s331392s344193mappe3comtobiasstrom.util;

import com.google.android.gms.maps.model.LatLng;
import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.model.Meeting;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static final LatLng osloMet = new LatLng(59.921647, 10.733563);
    public static final LatLng p46 = new LatLng(59.921101, 10.733316);
    public static final LatLng p35 = new LatLng(59.919434, 10.735239);
    public static final LatLng p32 = new LatLng(59.920006, 10.735867);
    public static final LatLng p52 = new LatLng(59.922470, 10.732752);

    public static List<Meeting> selectedMeetings = new ArrayList<>();

}
