package com.thefatherinc.mydomrf;

import com.google.gson.annotations.SerializedName;

public class RequestGetNews {
    @SerializedName("news")
    public String[] news;
}