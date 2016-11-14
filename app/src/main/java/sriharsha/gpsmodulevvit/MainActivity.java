package sriharsha.gpsmodulevvit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity  {
    LocationManager locationmanager;
    String lat="", lon="",name="";
    float speed;
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    EditText bus_route;


    // url to create new product
    private static String url_save_location = "http://103.22.174.221/track/track.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        bus_route = (EditText) findViewById(R.id.inputName);
        Button save= (Button)findViewById(R.id.btn_save_location);
        name = bus_route.getText().toString();


    }

    public void saveLocation(View view) {
        Toast.makeText(this,"Saving Coordinates",Toast.LENGTH_SHORT).show();
        //new SaveLocation().execute();
        new Thread()
        {
            public void run()
            {
                MainActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        LocationManager locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
                        LocationListener locationListener = new LocationListener() {
                            public void onLocationChanged(Location location) {
                                // Called when a new location is found by the network location provider.
                                lat = Double.toString(location.getLatitude());
                                lon = Double.toString(location.getLongitude());
                                speed=location.getSpeed();
                                TextView tv = (TextView) findViewById(R.id.output_text);
                                tv.setText("Your Location is:" + lat + "--" + lon);
                            }

                            public void onStatusChanged(String provider, int status, Bundle extras) {}
                            public void onProviderEnabled(String provider) {}
                            public void onProviderDisabled(String provider) {}
                        };
                        try{
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        }
                        catch(SecurityException e)
                        {
                            e.printStackTrace();
                        }


                        // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("Bus_route", name));
                        params.add(new BasicNameValuePair("Lat", lat));
                        params.add(new BasicNameValuePair("Lon", lon));
                        params.add(new BasicNameValuePair("Spe",Float.toString(speed)));

                        // getting JSON Object
                        // Note that save url accepts POST method
                        JSONObject json;
                        json = jsonParser.makeHttpRequest(url_save_location,"GET", params);

                        // check log cat fro response
                        Log.d("Create Response", json.toString());

                        // check for success tag
                        try {
                            int success = json.getInt(TAG_SUCCESS);

                            if (success == 1) {
                                // successfully saved location
                                //Intent i = new Intent(getApplicationContext(), ResponseActivity.class);

                                //startActivity(i);
                                Toast.makeText(MainActivity.this,"Location Saved",Toast.LENGTH_SHORT);
                                // closing this screen
                                //finish();
                            } else {
                                Toast.makeText(MainActivity.this,"Error saving location",Toast.LENGTH_SHORT);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }//Do your UI operations like dialog opening or Toast here
                    }
                });
            }
        }.start();
    }

   /*class SaveLocation extends AsyncTask<String,Void,Void>
    {

        @Override
        protected Void doInBackground(String... args) {

            LocationManager locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    lat = Double.toString(location.getLatitude());
                    lon = Double.toString(location.getLongitude());
                    speed=location.getSpeed();
                    TextView tv = (TextView) findViewById(R.id.output_text);
                    tv.setText("Your Location is:" + lat + "--" + lon);
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {}
                public void onProviderEnabled(String provider) {}
                public void onProviderDisabled(String provider) {}
            };
            try{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
            catch(SecurityException e)
            {
                e.printStackTrace();
            }


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Bus_route", name));
            params.add(new BasicNameValuePair("Lat", lat));
            params.add(new BasicNameValuePair("Lon", lon));
            params.add(new BasicNameValuePair("spe",Float.toString(speed)));

            // getting JSON Object
            // Note that save url accepts POST method
            JSONObject json;
            json = jsonParser.makeHttpRequest(url_save_location,"GET", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully saved location
                    //Intent i = new Intent(getApplicationContext(), ResponseActivity.class);

                    //startActivity(i);
                    Toast.makeText(MainActivity.this,"Location Saved",Toast.LENGTH_SHORT);
                    // closing this screen
                    finish();
                } else {
                    Toast.makeText(MainActivity.this,"Error saving location",Toast.LENGTH_SHORT);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


    }*/
}

