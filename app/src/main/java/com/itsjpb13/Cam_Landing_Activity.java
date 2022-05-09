package com.itsjpb13;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Cam_Landing_Activity extends AppCompatActivity {
    String dataUrl = "https://web6.seattle.gov/Travelers/api/Map/Data?zoomId=13&type=2";
    ListView trafList;
    ArrayList<Cam> camArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_landing);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Live Cams");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        trafList = findViewById(R.id.liveCamList);
        camArrayList = new ArrayList<>();
        CamAdapter myCamAdapter = new CamAdapter(this, camArrayList);
        trafList.setAdapter(myCamAdapter);
        ConnectivityManager myConnection = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(myConnection.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                myConnection.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {


            RequestQueue myQ = Volley.newRequestQueue(this);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, dataUrl, null, response -> {
                    try {
                        JSONArray features = response.getJSONArray("Features");
                        for (int i = 0; i<features.length(); i++) {
                            JSONObject locale = features.getJSONObject(i);
                            JSONArray coords = locale.getJSONArray("PointCoordinate");
                            double[] thisCoords = {coords.getDouble(0), coords.getDouble(1)};
                            JSONArray cams = locale.getJSONArray("Cameras");

                            for (int j = 0; j < cams.length(); j++){
                                JSONObject thisCam = cams.getJSONObject(j);
                                String thisUrl = thisCam.getString("ImageUrl");
                                String thisDescription = thisCam.getString("Description");
                                String thistype = thisCam.getString("Type");

                                camArrayList.add( new Cam(thisCoords, thisDescription, thisUrl, thistype));


                            }
                        }
                        myCamAdapter.notifyDataSetChanged();
                    }

                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                } , error -> Log.d("jsonError", "error: " + error.getMessage()));
        myQ.add(objectRequest);
        } else {
            Toast.makeText(this, "We're Sorry, you're not connected to the internet", Toast.LENGTH_LONG).show();
        }
    }


    class CamAdapter extends ArrayAdapter<Cam> {
        private final Context context;
        private final ArrayList<Cam> values;

        public CamAdapter(Context context, ArrayList<Cam> values){
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflation = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View trafficView = inflation.inflate(R.layout.traffic_row, parent, false);
            ImageView pic = trafficView.findViewById(R.id.livePic);
            TextView desc = trafficView.findViewById(R.id.liveDesc);
            Cam currCam = values.get(position);

            String imgurl = currCam.url;
            desc.setText(currCam.desc);
            Picasso.get().load(imgurl).into(pic);

            return trafficView;
        }

    }
}