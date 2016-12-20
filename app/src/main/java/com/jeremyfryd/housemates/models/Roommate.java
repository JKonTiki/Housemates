package com.jeremyfryd.housemates.models;

/**
 * Created by jeremy on 12/20/16.
 */

public class Roommate {
    private String name;
    private String houseId;
    private String roommateId;
    private String authId;

    public Roommate(String name, String houseId, String authId){
        this.name = name;
        this.houseId = houseId;
        this.authId = authId;
    }

    public Roommate(){}

    public String getName(){
        return name;
    }

    public String getHouseId(){
        return houseId;
    }

    public String getRoommateId() {
        return roommateId;
    }

    public void setRoommateId(String pushId) {
        this.roommateId = pushId;
    }


}