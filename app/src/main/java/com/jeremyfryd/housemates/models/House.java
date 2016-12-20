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
    private String code;
    private List<String> roommates = new ArrayList<String>();
    private String houseId;

    public House(String name, String latitude, String longitude){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String pushId) {
        this.houseId = pushId;
    }

    public String getCode() {
        return code;
    }

    public List<String> getRoommates(){
        return roommates;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void addRoommateId(String _id){
        this.roommates.add(_id);
    }
}