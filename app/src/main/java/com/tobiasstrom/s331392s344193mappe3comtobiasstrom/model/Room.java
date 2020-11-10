package com.tobiasstrom.s331392s344193mappe3comtobiasstrom.model;

public class Room {
    private String id;
    private String idHouse;
    private String floorNr;
    private String roomNr;
    private String capacity;
    private String description;

    public Room() {
    }

    public Room(String id,String idHouse ,  String floorNr, String roomNr, String capacity, String description) {
        this.id = id;
        this.idHouse = id;
        this.floorNr = floorNr;
        this.roomNr = roomNr;
        this.capacity = capacity;
        this.description = description;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getIdHouse() {
        return idHouse;
    }
    public void setIdHouse(String idHouse) {
        this.idHouse = idHouse;
    }

    public String getFloorNr() {
        return floorNr;
    }
    public void setFloorNr(String floorNr) {
        this.floorNr = floorNr;
    }

    public String getRoomNr() {
        return roomNr;
    }
    public void setRoomNr(String roomNr) {
        this.roomNr = roomNr;
    }

    public String getCapacity() {
        return capacity;
    }
    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", idHouse='" + idHouse + '\'' +
                ", floorNr=" + floorNr +
                ", roomNr=" + roomNr +
                ", capacity=" + capacity +
                ", description='" + description + '\'' +
                '}';
    }
}
