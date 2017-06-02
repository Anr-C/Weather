package com.lckiss.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 17-5-10.
 */

public class Forecast {
public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;


    public Wind wind;

    public class Temperature{
        public String max;
        public String min;
    }
    public  class More{
        @SerializedName("txt_d")
        public String info;

        @SerializedName("code_d")
        public String infoCode;
    }

    public class Wind{
        public String dir;
        public String sc;
    }

}
