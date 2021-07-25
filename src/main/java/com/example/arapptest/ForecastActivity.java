package com.example.arapptest;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;

public class ForecastActivity extends AppCompatActivity {
    Double xsum;
    Double ysum ;
    Double xysum;
    Double x2sum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

    }

    public void calculateForecast() {
        ArrayList<String> x_axis=new ArrayList<String>();
        ArrayList<String> y_axis=new ArrayList<String>();




        Double m;
        Double c;

        int n=x_axis.size();
        for(int i=0;i<n;i++) {
            xsum=Double.parseDouble(x_axis.get(i))+xsum;
            ysum=Double.parseDouble(y_axis.get(i))+ysum;
            xysum=Double.parseDouble(x_axis.get(i))*Double.parseDouble(y_axis.get(i))+xysum;
            x2sum=Double.parseDouble(x_axis.get(i))*Double.parseDouble(x_axis.get(i))+x2sum;
        }
        m=(n*xysum-xsum*ysum)/(n*x2sum-xsum*xsum);
        c=(x2sum*ysum-xsum*xysum)/(x2sum*n-xsum*xsum);
    }


}
