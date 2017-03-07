package com.example.fourfish.hotmovie.adapter;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fourfish on 2017/3/5.
 */

public class Youtube {
    @SerializedName("source")
    public String source;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
