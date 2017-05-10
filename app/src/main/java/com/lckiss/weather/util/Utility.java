package com.lckiss.weather.util;

import android.text.TextUtils;

import com.lckiss.weather.db.City;
import com.lckiss.weather.db.County;
import com.lckiss.weather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 17-5-10.
 */

public class Utility {
/**
 * 解析和处理服务器返回的省级数据
 *
 */
public static boolean handleProvicesResponse(String response)
{
    if(!TextUtils.isEmpty(response))
    {
        try{
            JSONArray allProvinces=new JSONArray(response);
            for (int i=0; i<allProvinces.length();i++){
                JSONObject provceObject=allProvinces.getJSONObject(i);
                Province province=new Province();
                province.setProvinceName(provceObject.getString("name"));
                province.setProvinceCode(provceObject.getInt("id"));
                province.save();
            }
            return true;
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    return false;
}
/**
 * 解析和处理服务器返回的市级数据
 */
public static boolean handleCitiesResponse(String response,int provinceId){

    if(!TextUtils.isEmpty(response))
    {
        try{
            JSONArray allProvinces=new JSONArray(response);
            for (int i=0; i<allProvinces.length();i++){
                JSONObject cityObject=allProvinces.getJSONObject(i);
                City city=new City();
                city.setCityName(cityObject.getString("name"));
                city.setCityCode(cityObject.getInt("id"));
                city.setProvinceId(provinceId);
                city.save();
            }
            return true;
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    return false;

}

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountiseResponse(String response,int cityId){

        if(!TextUtils.isEmpty(response))
        {
            try{
                JSONArray allProvinces=new JSONArray(response);
                for (int i=0; i<allProvinces.length();i++){
                    JSONObject countyObject=allProvinces.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;

    }
}
