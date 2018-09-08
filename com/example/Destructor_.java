
package com.example;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Destructor_ {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("destructedLanucher")
    @Expose
    private List<DestructedLanucher> destructedLanucher = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DestructedLanucher> getDestructedLanucher() {
        return destructedLanucher;
    }

    public void setDestructedLanucher(List<DestructedLanucher> destructedLanucher) {
        this.destructedLanucher = destructedLanucher;
    }

}
