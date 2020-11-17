package com.tobiasstrom.s331392s344193mappe3comtobiasstrom.util;

import com.google.android.gms.maps.model.LatLng;
import com.tobiasstrom.s331392s344193mappe3comtobiasstrom.model.Meeting;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    //Kodrinaten som den zoomer inn til.
    public static final LatLng osloMet = new LatLng(59.921647, 10.733563);

    //Metode som blir feller for flere klasser.
    public static List<Meeting> selectedMeetings = new ArrayList<>();

}
