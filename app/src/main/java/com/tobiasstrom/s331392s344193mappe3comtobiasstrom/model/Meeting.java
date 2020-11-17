package com.tobiasstrom.s331392s344193mappe3comtobiasstrom.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Meeting {
    private String id;
    private String idRoom;
    private Date start;
    private Date end;
    private boolean selected;

    public Meeting() {
    }

    public Meeting(String id, String idRoom, Date start, Date end) {
        this.id = id;
        this.idRoom = idRoom;
        this.start = start;
        this.end = end;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getIdRoom() {
        return idRoom;
    }
    public void setIdRoom(String idRoom) {
        this.idRoom = idRoom;
    }

    public Date getStart() {
        return start;
    }
    //Konverterer string til rikig Dateform
    public void setStart(String start) {
        Date starts = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            starts = sdf.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.start = starts;
    }
    public void setStart(Date start){
        this.start =start;
    }

    public Date getEnd() {
        return end;
    }
    //Konverterer string til rikig Dateform
    public void setEnd(String end) {
        Date ends = null;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ends = sdf.parse(end);
        }catch (ParseException e){
            e.printStackTrace();
        }
        this.end = ends;
    }
    public void setEnd(Date end){
        this.end = end;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "id='" + id + '\'' +
                ", idRoom='" + idRoom + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
