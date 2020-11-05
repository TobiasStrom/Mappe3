package com.tobiasstrom.s331392s344193mappe3comtobiasstrom.model;

public class Room {
    private int id;
    private int floorNr;
    private int roomNr;
    private int capacity;
    private String description;

    public Room() {
    }

    public Room(int id, int floorNr, int roomNr, int capacity, String description) {
        this.id = id;
        this.floorNr = floorNr;
        this.roomNr = roomNr;
        this.capacity = capacity;
        this.description = description;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getFloorNr() {
        return floorNr;
    }
    public void setFloorNr(int floorNr) {
        this.floorNr = floorNr;
    }

    public int getRoomNr() {
        return roomNr;
    }
    public void setRoomNr(int roomNr) {
        this.roomNr = roomNr;
    }

    public int getCapacity() {
        return capacity;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
