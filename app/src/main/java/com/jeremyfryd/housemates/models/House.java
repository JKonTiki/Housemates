package com.jeremyfryd.housemates.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremy on 12/20/16.
 */

public class House {
    private String name;
    private String latitude;
    private String longitude;
    private List<String> roommates = new ArrayList<String>();
    private String houseCode;

    public House(String name, String latitude, String longitude, String houseCode){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.houseCode = houseCode;
    }

    public House(){}

    public String getName(){
        return name;
    }

    public String getLatitude(){
        return latitude;
    }

    public String getLongitude(){
        return longitude;
    }

    public String getHouseCode() {
        return houseCode;
    }

    public List<String> getRoommates(){
        return roommates;
    }

    public void addRoommateId(String _id){
        this.roommates.add(_id);
    }
}