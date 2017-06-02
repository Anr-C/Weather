package com.lckiss.weather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lckiss.weather.gson.Forecast;
import com.lckiss.weather.gson.Weather;
import com.lckiss.weather.util.HttpUtil;
import com.lckiss.weather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView degreeCountyText;
    private TextView weatherInfoText;
    private LinearLayout forcastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView qltyText;
    private TextView comfortText;
    private TextView dessText;
    private TextView coldText;
    private TextView carWashText;
    private TextView sportText;

    //下拉刷新
    public SwipeRefreshLayout swipeRefresh;

    public DrawerLayout drawerLayout;
    private Button navButton;

    private String weatherIdCache;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏的特殊处理
        if(Build.VERSION.SDK_INT>=21){
            View decorView =getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_weather);

        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        degreeCountyText=(TextView)findViewById(R.id.degreeCounty);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forcastLayout = (LinearLayout) findViewById(R.id.forcast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        qltyText = (TextView) findViewById(R.id.qlty_text);

        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        dessText = (TextView) findViewById(R.id.dess_text);
        coldText=(TextView) findViewById(R.id.cold_text);
        sportText = (TextView) findViewById(R.id.spo_text);

        swipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navButton=(Button) findViewById(R.id.nav_button);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);

        if (weatherString != null) {
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherIdCache=weather.basic.weatherId;
//            Log.i("myinfo_Zero:",weatherIdCache);
            showWeatherInfo(weather);
        } else {
            //无缓存时去服务器查询天气
            weatherIdCache = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherIdCache);
//            Log.i("myinfo_First:",weatherIdCache);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherIdCache);
                Log.i("myinfo_Second:",weatherIdCache);
            }
        });

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * 根据天气id请求城市天气信息
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl="http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
//        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=1647cfa57ec54775a878e1d9745325c7";
        weatherIdCache=weatherId;
        Log.i("myinfo_E:",weatherId);
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    /**
     * 处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather) {
        if (weather!=null&& "ok".equals(weather.status)) {
            String cityName = weather.basic.cityName;
            String updateTime = weather.basic.update.updateTime.split(" ")[1];
            String degree = weather.now.temperature + "℃";
            String weatherInfo = weather.now.more.info;

            titleUpdateTime.setText("上次更新时间:"+updateTime);
            degreeText.setText(degree);
            degreeCountyText.setText(cityName);
            weatherInfoText.setText(weatherInfo);
            forcastLayout.removeAllViews();
            for (Forecast forcast : weather.forecastList) {
                View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forcastLayout, false);
                TextView dateText = (TextView) view.findViewById(R.id.data_text);
                TextView infoText = (TextView) view.findViewById(R.id.info_text);
                ImageView infoIcon = (ImageView) view.findViewById(R.id.info_icon);
                TextView maxMinText = (TextView) view.findViewById(R.id.max_min_text);
                TextView windtext=(TextView) view.findViewById(R.id.wind_text);
                dateText.setText(forcast.date);
                infoText.setText(forcast.more.info);
                String infoCode="icon"+forcast.more.infoCode;
             //动态获取天气图标
                int iconID=getResources().getIdentifier(infoCode, "drawable",  getPackageName());
                infoIcon.setImageResource(iconID);
                maxMinText.setText(forcast.temperature.max+"℃/"+forcast.temperature.min+"℃");

                windtext.setText(forcast.wind.dir+"("+forcast.wind.sc+")");
                forcastLayout.addView(view);
            }
            if (weather.aqi != null) {
                qltyText.setText(weather.aqi.city.qlty);
                aqiText.setText(weather.aqi.city.aqi);
                pm25Text.setText(weather.aqi.city.pm25);
            }
            String comFort = "舒适度: " + weather.suggestion.comfort.info;
            String carWash = "洗车指数: " + weather.suggestion.carWash.info;
            String dressing = "穿衣指数: " + weather.suggestion.dress.info;
            String cold = "感冒指数: " + weather.suggestion.cold.info;
            String sport = "运动建议: " + weather.suggestion.sport.info;
            comfortText.setText(comFort);
            dessText.setText(dressing);
            coldText.setText(cold);
            carWashText.setText(carWash);
            sportText.setText(sport);
            weatherLayout.setVisibility(View.VISIBLE);
        }else
        {
            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
        }
    }
}
