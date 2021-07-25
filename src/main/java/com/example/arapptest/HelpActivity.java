package com.example.arapptest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
    }

    public void pmInfo(View v) {
        Uri uri = Uri.parse("https://www.epa.gov/pm-pollution/particulate-matter-pm-basics");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void ozoneInfo(View v) {
        Uri uri = Uri.parse("https://www.epa.gov/ground-level-ozone-pollution/ground-level-ozone-basics#effects");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void aqiInfo(View v) {
        Uri uri = Uri.parse("https://www.airnow.gov/aqi/aqi-basics/");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}