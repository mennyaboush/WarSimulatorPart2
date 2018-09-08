
package com.example;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DestructdMissile {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("destructAfterLaunch")
    @Expose
    private String destructAfterLaunch;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDestructAfterLaunch() {
        return destructAfterLaunch;
    }

    public void setDestructAfterLaunch(String destructAfterLaunch) {
        this.destructAfterLaunch = destructAfterLaunch;
    }

}
