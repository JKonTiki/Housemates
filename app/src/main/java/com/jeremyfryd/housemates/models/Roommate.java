package com.jeremyfryd.housemates.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremy on 12/20/16.
 */


//TODO refactor 'atHome' as boolean corresponding to each HouseId
//TODO add character avatar choice as a property of roommate object, so avatar choice displays universally
@Parcel
public class Roommate {
    public String name;
    public List<String> houseIds= new ArrayList<String>();
    public String roommateId;
    public String atHome;

    public Roommate(String name, String roommateId){
        this.name = name;
        this.roommateId = roommateId;
        this.atHome = "unavailable";
    }

    public Roommate(){}

    public String getName(){
        return name;
    }

    public List<String> getHouseIds(){
        return houseIds;
    }

    public void addHouseId(String houseId){
        houseIds.add(houseId);
    }

    public String getRoommateId() {
        return roommateId;
    }

    public String getAtHome(){
        return atHome;
    }

    public void isHome(boolean _isHome){
        if (_isHome){
            atHome = "true";

        } else{
            atHome = "false";
        }
    }

    public void setRoommateId(String pushId) {
        this.roommateId = pushId;
    }


}