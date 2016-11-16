package com.laslomas.abcanthur.laslomasmapapp;

import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.Color;
//import com.mapbox.mapboxandroiddemo.R;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.FillLayer;

import com.getbase.floatingactionbutton.FloatingActionButton;

import static com.mapbox.mapboxsdk.style.layers.Filter.eq;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this,getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MapboxMapOptions options = new MapboxMapOptions()
                .styleUrl("mapbox://styles/abcanthur/civc0eniz001u2ipkbejiocds")
                .camera(new CameraPosition.Builder()
                        .target(new LatLng(43.103416, -91.009995))
                        .zoom(16)
                        .bearing(0)
                        .tilt(30)
                        .build());

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                FillLayer myLayer = new FillLayer("yard-layer", "composite");
                myLayer.setSourceLayer("Cropland");
                myLayer.setProperties(fillColor(Color.parseColor("#0000bb"))
                );
                myLayer.setFilter(eq("Type", "Yard"));
                mapboxMap.addLayer(myLayer);

                FloatingActionButton add_elements = (FloatingActionButton) findViewById(R.id.add_elements);
                add_elements.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, "Display a blue yard", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        FillLayer yard = mapboxMap.getLayerAs("yard-layer");
                        if (yard != null) {
                            if (!colorsEqual(yard.getFillColorAsInt(),
                                    ContextCompat.getColor(MainActivity.this, R.color.yardBlue))) {
                                yard.setProperties(
                                        fillColor(ContextCompat.getColor(MainActivity.this, R.color.yardBlue))
                                );
                            } else {
                                yard.setProperties(
                                        fillColor(Color.parseColor("#0000bb"))
                                );
                            }
                        }
                    }
                });

                FloatingActionButton currLocation = (FloatingActionButton) findViewById(R.id.fab_curr_location);
                currLocation.setSize(FloatingActionButton.SIZE_MINI);
                currLocation.setIcon(R.drawable.curr_location_icon);
                currLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fabMoveCamera(43.110692, -91.004770, mapboxMap); //this is the deer stand, change to user curr loc
                    }
                });

                FloatingActionButton returnHome = (FloatingActionButton) findViewById(R.id.fab_return_home);
                returnHome.setSize(FloatingActionButton.SIZE_MINI);
                returnHome.setIcon(R.drawable.home_icon);
                returnHome.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fabMoveCamera(43.103416, -91.009995, mapboxMap); //this is the deer stand, change to user curr loc
                    }
                });

                FloatingActionButton lastLocation = (FloatingActionButton) findViewById(R.id.fab_curr_location);
                lastLocation.setSize(FloatingActionButton.SIZE_MINI);
                lastLocation.setIcon(R.drawable.last_location);
                lastLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fabMoveCamera(43.111150, -91.010806, mapboxMap); //this is the old house, change to user last loc
                    }
                });
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_return_home) {
//            return true;
//        }
        if (id == R.id.action_layers) {
            return true;
        }
        if (id == R.id.layers_satellite) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    private boolean colorsEqual(int firstColor, int secondColor) {
        boolean equal = Math.abs(Color.red(firstColor) - Color.red(secondColor)) <= 10
                && Math.abs(Color.green(firstColor) - Color.green(secondColor)) <= 10
                && Math.abs(Color.blue(firstColor) - Color.blue(secondColor)) <= 10;

        Log.i(TAG, String.format("Comparing colors: %s, %s (%s, %s ,%s => %s)",
                firstColor,
                secondColor,
                Math.abs(Color.red(firstColor) - Color.red(secondColor)),
                Math.abs(Color.green(firstColor) - Color.green(secondColor)),
                Math.abs(Color.blue(firstColor) - Color.blue(secondColor)),
                equal)
        );
        return equal;
    }

    public void fabMoveCamera(double lat, double lon, MapboxMap mapboxMap) {
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(lat, lon)) // Sets the new camera position
                .zoom(17) // Sets the zoom
                .bearing(0) // Rotate the camera
                .tilt(50) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder

        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 4000);
    }


}
