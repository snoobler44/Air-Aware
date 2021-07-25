package com.example.arapptest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;

public class MapActivity extends AppCompatActivity {

    private MapView mMapView;

    public void moreInfo(View v) {
        Uri uri = Uri.parse("https://trsqvvo228shs7ma.maps.arcgis.com/apps/webappviewer/index.html?id=7031a83654a54c75b9dd74aff8465442");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mMapView = findViewById(R.id.mapView);
        Portal portal = new Portal("https://www.arcgis.com", false);
        String itemid = "2f3cf29c5f044ee2a397954aedac2ea1";
        PortalItem portalItem = new PortalItem(portal, itemid);
        ArcGISMap map = new ArcGISMap(portalItem);
        mMapView.setMap(map);
    }

    @Override
    protected void onPause() {
        mMapView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.resume();
    }

    @Override
    protected void onDestroy() {
        mMapView.dispose();
        super.onDestroy();
    }


    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}