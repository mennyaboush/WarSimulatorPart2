
package com.example;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MissileLaunchers {

    @SerializedName("launcher")
    @Expose
    private List<Launcher> launcher = null;

    public List<Launcher> getLauncher() {
        return launcher;
    }

    public void setLauncher(List<Launcher> launcher) {
        this.launcher = launcher;
    }

}
