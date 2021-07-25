package com.example.arapptest;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import java.util.Iterator;
import com.esri.arcgisruntime.geometry.Envelope;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // object of ArFragment Class
    private ArFragment arCam;

    // helps to render the 3d model
    // only once when we tap the screen
    private int clickNo = 0;
    private int resetVal = 0;

    // annoying glboals
    ImageView touchText;
    TransformableNode model1;
    TransformableNode model2;
    TransformableNode model3;

    // randomize positions
    float max = 1.5f;
    float min = -1.5f;
    float range = max - min + 1;

    // Data shit
    private static final String TAG = MainActivity.class.getSimpleName();
    private String featureURL = "https://services3.arcgis.com/ZNt4X6ukQszuNWXe/arcgis/rest/services/CA_Sites_for_Khoa/FeatureServer/0";
    private ServiceFeatureTable mServiceFeatureTable;
    private FeatureLayer mFeatureLayer;
    private FusedLocationProviderClient fusedLocationClient;
    private Double longitude;
    private Double latitude;

    // Activity management
    public void mapView(View v) {
         Intent left = new Intent(getApplicationContext(), MapActivity.class);
         startActivity(left);
         overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void helpView(View v) {
        Intent right = new Intent(getApplicationContext(), HelpActivity.class);
        startActivity(right);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void reset(View v) {
        clickNo = 0;
        touchText = findViewById(R.id.tap);
        touchText.setVisibility(View.VISIBLE);
    }

    public void forecast(View v) {
        Intent left = new Intent(getApplicationContext(), ForecastActivity.class);
        startActivity(left);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public static boolean checkSystemSupport(Activity activity) {

        // checking whether the API version of the running Android >= 24
        // that means Android Nougat 7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String openGlVersion = ((ActivityManager) Objects.requireNonNull(activity.getSystemService(Context.ACTIVITY_SERVICE))).getDeviceConfigurationInfo().getGlEsVersion();

            // checking whether the OpenGL version >= 3.0
            if (Double.parseDouble(openGlVersion) >= 3.0) {
                return true;
            } else {
                Toast.makeText(activity, "App needs OpenGl Version 3.0 or later", Toast.LENGTH_SHORT).show();
                activity.finish();
                return false;
            }
        } else {
            Toast.makeText(activity, "App does not support required Build Version", Toast.LENGTH_SHORT).show();
            activity.finish();
            return false;
        }
    }

    // CountyName, aqi, category, ozone, pm2.5aqi, pm10

    private void searchForStation(final String searchString) {
        // clear any previous selections
        mFeatureLayer.clearSelection();
        // create objects required to do a selection with a query
        QueryParameters query = new QueryParameters();
        // call select features
        query.setWhereClause("StateName LIKE 'CA'");
        final ListenableFuture<FeatureQueryResult> future = mServiceFeatureTable.queryFeaturesAsync(query);
        // add done loading listener to fire when the selection returns
        future.addDoneListener(() -> {
            try {
                // call get on the future to get the result
                FeatureQueryResult result = future.get();
                // check there are some results
                Iterator<Feature> resultIterator = result.iterator();
                if (resultIterator.hasNext()) {
                    // get the extent of the first feature in the result to zoom to
                    Feature feature = resultIterator.next();
                    feature.getAttributes().get("OZONEPM_AQI");
                    feature.getAttributes().get("OZONE_AQI");

                } else {
                    Toast.makeText(this, "No nearby stations were found near: " + searchString, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                String error = "Feature search failed for: " + searchString + ". Error: " + e.getMessage();
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                Log.e(TAG, error);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                            //TextView text = (TextView) findViewById(R.id.lonng);
                            //text.setText(longitude.toString());
                            //TextView text2 = (TextView) findViewById(R.id.lat);
                            //text2.setText(latitude.toString());
                        }
                    }
                });

        mServiceFeatureTable = new ServiceFeatureTable(featureURL);
        mServiceFeatureTable.loadAsync();
        mServiceFeatureTable.addDoneLoadingListener(
                new Runnable() {
                    //@Override
                    public void run() {
                        // create the feature layer using the service feature table
                        mFeatureLayer = new FeatureLayer(mServiceFeatureTable);
                        searchForStation("test");
                    }
                }
        );

        if (checkSystemSupport(this)) {
            // ArFragment is linked up with its respective id used in the activity_main.xml
            arCam = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arCameraArea);

            arCam.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
                clickNo++;
                // the 3d model comes to the scene only
                // when clickNo is one that means once
                if (clickNo == 1) {
                    touchText = findViewById(R.id.tap);
                    touchText.setVisibility(View.GONE);
                    Anchor anchor = hitResult.createAnchor();
                    ModelRenderable.builder()
                            .setSource(this, R.raw.ozone)
                            .setIsFilamentGltf(true)
                            .build()
                            .thenAccept(modelRenderable -> Ozone(anchor, modelRenderable))
                            .exceptionally(throwable -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setMessage("Something is not right" + throwable.getMessage()).show();
                                return null;
                            });

                    ModelRenderable.builder()
                            .setSource(this, R.raw.pm2_5)
                            .setIsFilamentGltf(true)
                            .build()
                            .thenAccept(modelRenderable -> pm2_5(anchor, modelRenderable))
                            .exceptionally(throwable -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setMessage("Something is not right" + throwable.getMessage()).show();
                                return null;
                            });

                    ModelRenderable.builder()
                            .setSource(this, R.raw.pm_10)
                            .setIsFilamentGltf(true)
                            .build()
                            .thenAccept(modelRenderable -> pm_10(anchor, modelRenderable))
                            .exceptionally(throwable -> {
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setMessage("Something is not right" + throwable.getMessage()).show();
                                return null;
                            });
                }
            });
        }
    }

    private void Ozone(Anchor anchor, ModelRenderable modelRenderable) {

        // Creating a AnchorNode with a specific anchor
        AnchorNode anchorNode = new AnchorNode(anchor);

        // attaching the anchorNode with the ArFragment
        anchorNode.setParent(arCam.getArSceneView().getScene());

        int ozoneDataSize = 15;

        for (int i = 0; i < ozoneDataSize; i++) {
            // attaching the anchorNode with the TransformableNode
            model1 = new TransformableNode(arCam.getTransformationSystem());
            model1.setParent(anchorNode);
            // attaching the 3d model with the TransformableNode
            // that is already attached with the node
            model1.setRenderable(modelRenderable);
            model1.select();

            if (resetVal == 1) {
                model1.setRenderable(null);
            }

            float rand1 = (float) (Math.random() * range) + min;
            float rand2 = (float) (Math.random() * range) + min;
            float rand3 = (float) (Math.random() * range) + min;

            model1.setLocalPosition(new Vector3(rand1, rand2 , rand3));
            model1.setLocalRotation(new Quaternion(rand1 + rand3, rand2 + rand1 , rand3 + rand2,0));
        }
    }

    private void pm2_5(Anchor anchor, ModelRenderable modelRenderable) {

        AnchorNode anchorNode = new AnchorNode(anchor);

        anchorNode.setParent(arCam.getArSceneView().getScene());

        int pm2DataSize = 15;

        for (int i = 0; i < pm2DataSize; i++) {
            model2 = new TransformableNode(arCam.getTransformationSystem());
            model2.setParent(anchorNode);

            model2.setRenderable(modelRenderable);
            model2.select();

            if (resetVal == 1) {
                model2.setRenderable(null);
            }

            float rand1 = (float) (Math.random() * range) + min;
            float rand2 = (float) (Math.random() * range) + min;
            float rand3 = (float) (Math.random() * range) + min;

            model2.setLocalPosition(new Vector3(rand1, rand2 , rand3));
            model2.setLocalRotation(new Quaternion(rand1 + rand3, rand2 + rand1 , rand3 + rand2,0));
        }
    }

    private void pm_10(Anchor anchor, ModelRenderable modelRenderable) {

        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arCam.getArSceneView().getScene());

        int pm5DataSize = 15;

        for (int i = 0; i < pm5DataSize; i++) {
            model3 = new TransformableNode(arCam.getTransformationSystem());
            model3.setParent(anchorNode);

            model3.setRenderable(modelRenderable);
            model3.select();

            if (resetVal == 1) {
                model3.setRenderable(null);
            }

            float rand1 = (float) (Math.random() * range) + min;
            float rand2 = (float) (Math.random() * range) + min;
            float rand3 = (float) (Math.random() * range) + min;

            model3.setLocalPosition(new Vector3(rand1, rand2 , rand3));
            model3.setLocalRotation(new Quaternion(rand1 + rand3, rand2 + rand1 , rand3 + rand2,0));
        }
    }
}