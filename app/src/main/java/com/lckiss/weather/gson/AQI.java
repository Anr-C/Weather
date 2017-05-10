package com.lckiss.weather.gson;

import java.security.PublicKey;

/**
 * Created by root on 17-5-10.
 */

public class AQI {

    public AQICity city;

    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
