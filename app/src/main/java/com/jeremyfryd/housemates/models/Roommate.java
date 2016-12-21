package com.jeremyfryd.housemates.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremy on 12/20/16.
 */

public class Roommate {
    private String name;
    private List<String> houseIds= new ArrayList<String>();
    private String roommateId;
    private String userId;

    public Roommate(String name, String houseId, String userId){
        this.name = name;
        this.houseIds.add(houseId);
        this.userId = userId;
    }

    public Roommate(){}

    public String getName(){
        return name;
    }

    public List<String> getHouseId(){
        return houseIds;
    }

    public void addHouseId(String houseId){
        houseIds.add(houseId);
    }

    public String getRoommateId() {
        return roommateId;
    }

    public void setRoommateId(String pushId) {
        this.roommateId = pushId;
    }


}