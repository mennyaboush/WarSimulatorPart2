
package com.example;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DestructedLanucher {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("destructTime")
    @Expose
    private String destructTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDestructTime() {
        return destructTime;
    }

    public void setDestructTime(String destructTime) {
        this.destructTime = destructTime;
    }

}
