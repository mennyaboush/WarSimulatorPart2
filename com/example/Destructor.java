
package com.example;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Destructor {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("destructdMissile")
    @Expose
    private DestructdMissile destructdMissile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DestructdMissile getDestructdMissile() {
        return destructdMissile;
    }

    public void setDestructdMissile(DestructdMissile destructdMissile) {
        this.destructdMissile = destructdMissile;
    }

}
