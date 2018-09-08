
package com.example;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Launcher {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("isHidden")
    @Expose
    private String isHidden;
    @SerializedName("missile")
    @Expose
    private List<Missile> missile = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(String isHidden) {
        this.isHidden = isHidden;
    }

    public List<Missile> getMissile() {
        return missile;
    }

    public void setMissile(List<Missile> missile) {
        this.missile = missile;
    }

}
