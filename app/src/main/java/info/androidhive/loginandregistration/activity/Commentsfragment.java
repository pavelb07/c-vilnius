package info.androidhive.loginandregistration.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.app.AppConfig;
import info.androidhive.loginandregistration.app.AppController;
import info.androidhive.loginandregistration.helper.SQLiteHandler;

import static android.content.ContentValues.TAG;


public class Commentsfragment extends Fragment {

    JSONObject jObj;
    String uid;
    String username;

    EditText comment_add;

    ListView commentsv;

    ImageButton send_com;
    ImageButton close_com;

    class Comments {
        String username;
        String Post_date;
        String Comment;

        public Comments(String username, String Post_date, String Comment){
            this.username = username;
            this.Post_date = Post_date;
            this.Comment = Comment;
        }
    }


    ArrayList<Comments> comments = new ArrayList<Comments>();



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {





        try {


            String jObjString=getArguments().getString("jObjString");
            jObj= new JSONObject(jObjString);

            SQLiteHandler db = new SQLiteHandler(getActivity().getApplicationContext());

            // Fetching user details from SQLite
            HashMap<String, String> user = db.getUserDetails();

            uid = user.get("uid");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            getComments(jObj.getString("pid"));
        } catch (JSONException e){
            e.printStackTrace();
        }


        return inflater.inflate(R.layout.fragment_comment, container,false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        send_com = (ImageButton) getActivity().findViewById(R.id.send_comm_bttn);
        close_com = (ImageButton) getActivity().findViewById(R.id.close_comm);

        comment_add = (EditText) getActivity().findViewById(R.id.comment_add);

        send_com.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comm = comment_add.getText().toString().trim();

                try {
                    if (!comm.isEmpty())
                        commentPlace(uid, jObj.getString("pid"), comm);
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

        close_com.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                String jObjString = jObj.toString();
                args.putString("jObjString", jObjString);

                PlaceFragment PlaceFrag = new PlaceFragment();

                PlaceFrag.setArguments(args);

                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.place_frame, PlaceFrag).commit();
            }
        });




        commentsv = (ListView) getActivity().findViewById(R.id.comments_list);


        CommentsAdapter adapter = new CommentsAdapter(getActivity(), comments);

        commentsv.setAdapter(adapter);



    }

    private void getComments (final String pid){
        String tag_string_req = "req_comment";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_COMMENTSGET, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Checking Response: " + response.toString());

                try {
                    JSONArray jArray = new JSONArray(response);


                    JSONObject jObjC;
                    for (int i = 0; i < jArray.length(); i++) {
                        try {



                            jObjC = jArray.getJSONObject(i);
                            getUsername(jObjC.getString("uid"));

                            Comments Commentary = new Comments(username, jObjC.getString("post_date"),jObjC.getString("Commentary"));

                            comments.add(Commentary);



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

    private void commentPlace (final String uid, final String pid, final String comment) {
        String tag_string_req = "req_comment";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_COMMENTPLACE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Checking Response: " + response.toString());
                comment_add.setText(response.toString());

                try {
                    JSONObject jObjComm = new JSONObject(response);
                    boolean error = jObjComm.getBoolean("error");

                    if (!error){
                        Toast.makeText(getActivity().getApplicationContext(), "Thank you for a comment", Toast.LENGTH_LONG).show();

                    } else {


                        String errorMsg = jObjComm.getString("error_msg");

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
                params.put("comment", comment);

                return params;
            }

        };


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void getUsername (final String uid) {
        String tag_string_req = "req_username";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETUSERNAME, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Checking Response: " + response.toString());

                try {
                    JSONObject jObjU = new JSONObject(response);

                    username = jObjU.getString("username");

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

                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public class CommentsAdapter extends ArrayAdapter<Comments> {
        public CommentsAdapter(Context context, ArrayList<Comments> comments) {
            super(context, 0, comments);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Comments comment = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.commentslist, parent, false);
            }
            // Lookup view for data population
            TextView username = (TextView) convertView.findViewById(R.id.username_com);
            TextView post_date = (TextView) convertView.findViewById(R.id.post_date);
            TextView commentary = (TextView) convertView.findViewById(R.id.Commentary);
            // Populate the data into the template view using the data object
            username.setText(comment.username);
            post_date.setText(comment.Post_date);
            commentary.setText(comment.Comment);
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
