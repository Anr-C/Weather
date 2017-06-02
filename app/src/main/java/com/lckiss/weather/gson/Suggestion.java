package com.lckiss.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 17-5-10.
 */

public class Suggestion {

    @SerializedName("comf")
    public  Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    @SerializedName("drsg")
    public Dressing dress;

    @SerializedName("flu")
    public Cold cold;

    public class Comfort{
        @SerializedName("txt")
        public String info;
    }
    public class CarWash{
        @SerializedName("txt")
        public String info;
    }
    public class Sport{
        @SerializedName("txt")
        public String info;
    }
    public class Dressing{
        @SerializedName("txt")
        public String info;
    }

    public class Cold{
        @SerializedName("txt")
        public String info;
    }
}
