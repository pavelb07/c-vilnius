package info.androidhive.loginandregistration.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.app.AppConfig;
import info.androidhive.loginandregistration.app.AppController;
import info.androidhive.loginandregistration.helper.SQLiteHandler;

import static android.content.ContentValues.TAG;


public class PlaceFragment extends Fragment {

    TextView Name;
    TextView Description;
    TextView Address;
    TextView rateV;
    RatingBar rate;
    Button fav;
    Button GetToLoc;
    Button comment;

    ProgressDialog pDialog;

    JSONObject jObj;

    boolean check;
    boolean checkR;
    double latitude;
    double longitude;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {






        try {
            String jObjString=getArguments().getString("jObjString");
            jObj= new JSONObject(jObjString);

            SQLiteHandler db = new SQLiteHandler(getActivity().getApplicationContext());

            // Fetching user details from SQLite
            HashMap<String, String> user = db.getUserDetails();

            String uid = user.get("uid");

            rateCheck(uid, jObj.getString("pid"));

        } catch (JSONException e) {
            e.printStackTrace();
        }



        return inflater.inflate(R.layout.place_fragment, container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Name = (TextView) getActivity().findViewById(R.id.name_view);
        Description = (TextView) getActivity().findViewById(R.id.description_view);
        Address = (TextView) getActivity().findViewById(R.id.address_view);

        Button close = (Button) getActivity().findViewById(R.id.close);
        fav = (Button) getActivity().findViewById(R.id.favorites_bttn);
        GetToLoc = (Button) getActivity().findViewById(R.id.get_to_place);
        comment = (Button) getActivity().findViewById(R.id.comments_bttn);

        rate = (RatingBar) getActivity().findViewById(R.id.ratingBar);
        rateV = (TextView) getActivity().findViewById(R.id.rating_view);

        try {
            Name.setText(jObj.getString("name"));
            Address.setText(jObj.getString("address"));
            Description.setText(jObj.getString("description"));

            String lat = jObj.getString("latitude");
            String longi = jObj.getString("longitude");

            latitude = Double.parseDouble(lat);
            longitude = Double.parseDouble(longi);

            placeRating(jObj.getString("pid"));

//            SQLiteHandler db = new SQLiteHandler(getActivity().getApplicationContext());
//
//            // Fetching user details from SQLite
//            HashMap<String, String> user = db.getUserDetails();
//
//            String uid = user.get("uid");

//            checkFavorite(uid,jObj.getString("pid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                String jObjString = jObj.toString();
                args.putString("jObjString", jObjString);

                Commentsfragment CommentFrag = new Commentsfragment();

                CommentFrag.setArguments(args);

                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.place_frame, CommentFrag).commit();
            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().remove(PlaceFragment.this).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
            }
        });


        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteHandler db = new SQLiteHandler(getActivity().getApplicationContext());

                // Fetching user details from SQLite
                HashMap<String, String> user = db.getUserDetails();

                String uid = user.get("uid");

                try {
                    addFavorite(uid,jObj.getString("pid"));
                    fav.setText("Remove from favorites");
                    check = true;

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


        rate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                SQLiteHandler db = new SQLiteHandler(getActivity().getApplicationContext());

                // Fetching user details from SQLite
                HashMap<String, String> user = db.getUserDetails();

                String uid = user.get("uid");

                try {
                    ratePlace(uid,jObj.getString("pid"),ratingBar.getRating()+"");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        GetToLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "http://maps.google.com/maps?daddr=" + latitude + "," + longitude + " (" + Name.getText() + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                try
                {
                    startActivity(intent);
                }
                catch(ActivityNotFoundException ex)
                {
                    try
                    {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(unrestrictedIntent);
                    }
                    catch(ActivityNotFoundException innerEx)
                    {
                        Toast.makeText(getActivity(), "Please install a maps application", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }

    private void addFavorite(final String uid, final String pid) {
        //
        String tag_string_req = "req_favorites";

//        pDialog.setMessage("Adding to Favorites ...");
//        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FAVORITESADD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Adding Response: " + response.toString());
//                hideDialog();

                try {
                    JSONObject jObjFav = new JSONObject(response);
                    boolean error = jObjFav.getBoolean("error");
                    if (!error) {
                        // Favorites successfully stored in MySQL
                        Toast.makeText(getActivity().getApplicationContext(), "Added to Favorites!", Toast.LENGTH_LONG).show();


                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObjFav.getString("error_msg");
                        Toast.makeText(getActivity().getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Adding Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
//                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to add favorites url
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", uid);
                params.put("pid", pid);

                return params;
            }

        };



        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void removeFavorite(final String uid, final String pid) {
        //
        String tag_string_req = "req_favorites";

//        pDialog.setMessage("Adding to Favorites ...");
//        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FAVORITESREMOVE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Removing Response: " + response.toString());
//                hideDialog();

                try {
                    JSONObject jObjFav = new JSONObject(response);
                    boolean error = jObjFav.getBoolean("error");
                    Name.setText(response.toString());
                    if (!error) {
                        // Favorites successfully removed from MySQL
                        Toast.makeText(getActivity().getApplicationContext(), "Removed from favorites.", Toast.LENGTH_LONG).show();


                    } else {

                        String errorMsg = "Remove failed";
                        Toast.makeText(getActivity().getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Adding Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
//                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to add favorites url
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", uid);
                params.put("pid", pid);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void checkFavorite(final String uid, final String pid){
        String tag_string_req = "req_favoritescheck";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_FAVORITESCHECK, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Checking Response: " + response.toString());
                Name.setText(response.toString());

                try {
                    JSONObject jObjFavCheck = new JSONObject(response);
                    check = jObjFavCheck.getBoolean("error");

                    if (check){
                        fav.setText("Remove from Favorites");
                        fav.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SQLiteHandler db = new SQLiteHandler(getActivity().getApplicationContext());

                                // Fetching user details from SQLite
                                HashMap<String, String> user = db.getUserDetails();

                                String uid = user.get("uid");

                                try {
                                    removeFavorite(uid,jObj.getString("pid"));
                                    fav.setText("Add to favorites");
                                    check = false;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } else {
                        fav.setText("Add to favorites");
                        fav.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SQLiteHandler db = new SQLiteHandler(getActivity().getApplicationContext());

                                // Fetching user details from SQLite
                                HashMap<String, String> user = db.getUserDetails();

                                String uid = user.get("uid");

                                try {
                                    addFavorite(uid,jObj.getString("pid"));
                                    fav.setText("Remove from favorites");
                                    check = true;

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Adding Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
//                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to add favorites url
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", uid);
                params.put("pid", pid);

                return params;
            }

        };


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void placeRating (final String pid) {
        String tag_string_req = "req_rating";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PLACERATING, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Checking Response: " + response.toString());

                try {
                    JSONObject jObjRateCheck = new JSONObject(response);
                    checkR = jObjRateCheck.getBoolean("check");

                    if (!jObjRateCheck.getString("rate").equals("null")){
                        rateV.setText(jObjRateCheck.getString("rate") + "/" + jObjRateCheck.getString("rate_num"));

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Rating check Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
//                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to add favorites url
                Map<String, String> params = new HashMap<String, String>();
                params.put("pid", pid);

                return params;
            }

        };


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void rateCheck (final String uid, final String pid) {
        String tag_string_req = "req_ratingcheck";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_RATECHECK, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Checking Response: " + response.toString());

                try {
                    JSONObject jObjRateCheck = new JSONObject(response);
                    checkR = jObjRateCheck.getBoolean("check");

                    if (check){
                        float rating = Float.parseFloat(jObjRateCheck.getString("rate"));
                        rate.setRating(rating);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Rating check Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
//                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to add favorites url
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", uid);
                params.put("pid", pid);

                return params;
            }

        };


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void ratePlace(final String uid, final String pid, final String rate){

        String tag_string_req = "req_rates";

//        pDialog.setMessage("Adding to Favorites ...");
//        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_RATEPLACE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Rating Response: " + response.toString());
//                hideDialog();

                try {
                    JSONObject jObjRat = new JSONObject(response);
                    boolean error = jObjRat.getBoolean("error");
                    if (!error) {
                        // Successfully rated
                        Toast.makeText(getActivity().getApplicationContext(), jObjRat.getString("info_msg"), Toast.LENGTH_LONG).show();
                        placeRating(jObj.getString("pid"));
                    } else {


                        String errorMsg = jObjRat.getString("You can rate only once");

                        Toast.makeText(getActivity().getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Rating Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
//                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to add favorites url
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", uid);
                params.put("pid", pid);
                params.put("rate", rate);

                return params;
            }

        };


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


}
