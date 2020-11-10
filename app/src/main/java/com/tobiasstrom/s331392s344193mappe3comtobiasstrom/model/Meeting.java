package com.tobiasstrom.s331392s344193mappe3comtobiasstrom.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Meeting {
    private String id;
    private String idRoom;
    private Date start;
    private Date end;

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

    public Date getEnd() {
        return end;
    }
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
