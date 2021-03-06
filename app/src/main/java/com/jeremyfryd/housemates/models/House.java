package com.jeremyfryd.housemates.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremy on 12/20/16.
 */

@Parcel
public class House {
    public String name;
    public String latitude;
    public String longitude;
    public List<String> roommates = new ArrayList<>();
    public String houseCode;

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