package info.androidhive.loginandregistration.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.app.AppConfig;
import info.androidhive.loginandregistration.app.AppController;
import info.androidhive.loginandregistration.helper.SQLiteHandler;


public class AddFragment extends Fragment {

    private static final String TAG = AddFragment.class.getSimpleName();

    private EditText inputPlaceName;
    private EditText inputDescription;
    private TextView addressV;
    private TextView coordinates;
    private Button ChooseLoc;
    private Button Finish;

    private ProgressDialog pDialog;

    String address;
    String[] myString;
    String Vilnius = ", Vilnius, Lithuania";

    Double latitude;
    Double longitude;

    Context cont = this.getActivity();


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        cont = this.getActivity();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_fragment, container,false);


    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        inputPlaceName = (EditText) getView().findViewById(R.id.place_name);
        inputDescription = (EditText) getView().findViewById(R.id.description);

        myString = getResources().getStringArray(R.array.ChooseLoc);
        ChooseLoc = (Button) getView().findViewById(R.id.add_location_bttn);

        addressV = (TextView) getView().findViewById(R.id.address);
        coordinates = (TextView) getView().findViewById(R.id.coordinates);

        Finish = (Button) getView().findViewById(R.id.finish_bttn);



        ChooseLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(cont)
                        .title("Enter address")
                        .content("Enter as much detailed address as you can (House Number, Street Direction, Street Name, Street Suffix, City, State, Zip, Country)")
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT)
                        .input("Example: V. Gerulaicio g. 1", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                address = input.toString();

                                if (address != null) {
                                    try {
                                        Geocoder geocoder = new Geocoder(cont);
                                        List<Address> addresses;
                                        addresses = geocoder.getFromLocationName(address, 1);
                                        if (addresses.size() > 0) {
                                            latitude = addresses.get(0).getLatitude();
                                            longitude = addresses.get(0).getLongitude();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Log.e("IO", "IO" + e);
                                    }

                                    addressV.setText(address+Vilnius);
                                    coordinates.setText(latitude + ", " + longitude);
                                }
                            }
                        })
                        .negativeText("Cancel")
                        .show();

//                new MaterialDialog.Builder(cont)
//                        .title("Choose option")
//                        .items(myString)
//                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
//                            @Override
//                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//                                int q = which;
//
//                                switch (q) {
//                                    case 0:
//
//                                        break;
//                                    case 1:
//
//                                        new MaterialDialog.Builder(cont)
//                                                .title("Enter address")
//                                                .content("Enter as much detailed address as you can (House Number, Street Direction, Street Name, Street Suffix, City, State, Zip, Country)")
//                                                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT)
//                                                .input("Example: V. Gerulaicio g. 1", "", new MaterialDialog.InputCallback() {
//                                                    @Override
//                                                    public void onInput(MaterialDialog dialog, CharSequence input) {
//                                                        address = input.toString();
//
//                                                        if (address != null) {
//                                                            try {
//                                                                Geocoder geocoder = new Geocoder(cont);
//                                                                List<Address> addresses;
//                                                                addresses = geocoder.getFromLocationName(address, 1);
//                                                                if (addresses.size() > 0) {
//                                                                    latitude = addresses.get(0).getLatitude();
//                                                                    longitude = addresses.get(0).getLongitude();
//                                                                }
//                                                            } catch (IOException e) {
//                                                                e.printStackTrace();
//                                                                Log.e("IO", "IO" + e);
//                                                            }
//
//                                                            addressV.setText(address+Vilnius);
//                                                            coordinates.setText(latitude + ", " + longitude);
//                                                        }
//                                                    }
//                                                })
//                                                .negativeText("Cancel")
//                                                .show();
//                                        break;
//                                    case 2:
//                                        break;
//                                    case 3:
//                                        break;
//                                }
//
//                                return true;
//                            }
//                        })
//                        .positiveText("Choose")
//                        .show();
            }
        });

        Finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String placename = inputPlaceName.getText().toString().trim();
                String description = inputDescription.getText().toString().trim();
                String addressFull = addressV.getText().toString().trim();
                String latitudeS = latitude.toString().trim();
                String longitudeS = longitude.toString().trim();


                if (!placename.isEmpty() && !description.isEmpty() && !addressFull.isEmpty() && !latitudeS.isEmpty() && !longitudeS.isEmpty()) {
                    registerPlace(placename, addressFull, latitudeS, longitudeS, description);
                } else {
                    Toast.makeText(cont.getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

    }


    private void registerPlace(final String name, final String address, final String latitude, final String longitude,
                                       final String description) {
                //
                String tag_string_req = "req_register";

                pDialog.setMessage("Registering Place ...");
                showDialog();

                StringRequest strReq = new StringRequest(Request.Method.POST,
                        AppConfig.URL_PLACEADD, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Register Response: " + response.toString());
                        hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");
                            if (!error) {
                                // Place successfully stored in MySQL

                                Toast.makeText(getActivity().getApplicationContext(), "Place successfully registered!", Toast.LENGTH_LONG).show();
                                inputPlaceName.setText("");
                                inputDescription.setText("");
                                addressV.setText("Address");
                                coordinates.setText("Coordinates");

                            } else {

                                // Error occurred in registration. Get the error
                                // message
                                String errorMsg = jObj.getString("error_msg");
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
                        Log.e(TAG, "Registration Error: " + error.getMessage());
                        Toast.makeText(getActivity().getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting params to register url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("name", name);
                        params.put("address", address);
                        params.put("latitude", latitude);
                        params.put("longitude", longitude);
                        params.put("description", description);

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

