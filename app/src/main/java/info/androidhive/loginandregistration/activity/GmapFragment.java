package info.androidhive.loginandregistration.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import info.androidhive.loginandregistration.Manifest;
import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.app.AppConfig;
import info.androidhive.loginandregistration.app.AppController;

import static android.content.ContentValues.TAG;

public class GmapFragment extends Fragment implements OnMapReadyCallback, OnMarkerClickListener {


    private Location location;
    private double latitude;
    private double longitude;


    private ProgressDialog pDialog;
    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gmaps, container,false);


    }


//    public void onLocationChanged(Location location) {
//            longitude = location.getLongitude();
//            latitude = location.getLatitude();





    @Override
    public void onMapReady(final GoogleMap googleMap) {


        final String tag_string_req = "req_places";

        final LocationManager manager = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE );
        Criteria criteria = new Criteria();



//        if ( ContextCompat.checkSelfPermission( getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
//            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//                displayPromptForEnablingGPS(getActivity());
//            } else{
//                location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
////                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000,10,);
//
//                if (location != null) {
//                    latitude = location.getLatitude();
//                    longitude = location.getLongitude();
//                }
//            }
//        }






        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);



        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_PLACEGET, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Getting place: " + response.toString());

                try {
                    JSONArray jArray = new JSONArray(response);
                    JSONObject jObj;
                    for (int i = 0; i < jArray.length(); i++) {
                        try {
                            jObj = jArray.getJSONObject(i);
                            LatLng marker = new LatLng(jObj.getDouble("latitude"), jObj.getDouble("longitude"));
                            googleMap.addMarker(new MarkerOptions().position(marker).title(jObj.getString("name"))).setTag(jObj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Loading Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {



        };

        /*if (ContextCompat.checkSelfPermission( getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
            }
            else {
                Toast.makeText(getActivity(), "Cannot show your current position.", Toast.LENGTH_SHORT).show();
                // Show rationale and request permission.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(54.6858486, 25.2877342), 15));
            }
        }
*/
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(54.6858486, 25.2877342), 15));

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);



        googleMap.setOnMarkerClickListener(this);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        JSONObject jObj = (JSONObject) marker.getTag();

        Log.d(TAG, " Response: " + jObj.toString());

        Bundle args = new Bundle();
        String jObjString = jObj.toString();
        args.putString("jObjString", jObjString);

        PlaceFragment PlaceFrag = new PlaceFragment();

        PlaceFrag.setArguments(args);

        marker.hideInfoWindow();

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.place_frame, PlaceFrag).commit();

        return true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapFragment fragment = (MapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);







    }

    public static void displayPromptForEnablingGPS(
            final Activity activity)
    {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Enable either GPS or any other location"
                + " service to find current location.  Click OK to go to"
                + " location services settings to let you do so."
                + " Then restart application please.";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }




}
