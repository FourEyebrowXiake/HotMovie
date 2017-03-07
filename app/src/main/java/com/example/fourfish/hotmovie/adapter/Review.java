package com.example.fourfish.hotmovie.adapter;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fourfish on 2017/3/5.
 */

public class Review {
    @SerializedName("author")
    public String author;

    @SerializedName("content")
    public String content;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
