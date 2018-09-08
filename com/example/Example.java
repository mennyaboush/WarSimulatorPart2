
package com.example;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Example {

    @SerializedName("war")
    @Expose
    private War war;

    public War getWar() {
        return war;
    }

    public void setWar(War war) {
        this.war = war;
    }

}
