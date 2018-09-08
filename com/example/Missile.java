
package com.example;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Missile {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("destination")
    @Expose
    private String destination;
    @SerializedName("launchTime")
    @Expose
    private String launchTime;
    @SerializedName("flyTime")
    @Expose
    private String flyTime;
    @SerializedName("damage")
    @Expose
    private String damage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getLaunchTime() {
        return launchTime;
    }

    public void setLaunchTime(String launchTime) {
        this.launchTime = launchTime;
    }

    public String getFlyTime() {
        return flyTime;
    }

    public void setFlyTime(String flyTime) {
        this.flyTime = flyTime;
    }

    public String getDamage() {
        return damage;
    }

    public void setDamage(String damage) {
        this.damage = damage;
    }

}
