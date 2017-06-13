package com.developer.ti.share.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.developer.ti.share.Helper.Config;
import com.developer.ti.share.Helper.GPS;
import com.developer.ti.share.MainActivity;
import com.developer.ti.share.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

public class DriverOriginFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener{
    private static final String TAG = DriverOriginFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private View rootView;
    private LinearLayout _llContenedorLocation;
    private Button _btnDestination;
    private TextView _tvOrigin, _tvAddress, _tvLocation;
    private static final int RESULT_OK = -1;
    private static final int RESULT_CANCELED = 0;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mMapFragment;
    private Context mContext;
    private GPS gps;
    private String URL = "http://maps.googleapis.com/maps/api/geocode/json?";
    private int a = 0, b = 0;
    private OnFragmentInteractionListener mListener;

    public DriverOriginFragment() {
        // Required empty public constructor
    }

    public static DriverOriginFragment newInstance(String param1, String param2) {
        DriverOriginFragment fragment = new DriverOriginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;

        gps = new GPS(getContext());

        _llContenedorLocation = (LinearLayout) rootView.findViewById(R.id.linear_layout_location);
        _btnDestination = (Button) rootView.findViewById(R.id.button_destination);
        _tvOrigin = (TextView) rootView.findViewById(R.id.text_view_origin);
        _tvAddress = (TextView) rootView.findViewById(R.id.text_view_address);
        _tvLocation = (TextView) rootView.findViewById(R.id.text_view_place);

        _llContenedorLocation.setOnClickListener(this);
        _btnDestination.setOnClickListener(this);

        TextView _titleTop;
        ImageView _arrowBack;
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.top_title_center);
            _titleTop = (TextView) actionBar.getCustomView().findViewById(R.id.text_view_title);
            _arrowBack = (ImageView) actionBar.getCustomView().findViewById(R.id.image_view_back_navigation);
            _titleTop.setText("Crear ruta");
            _arrowBack.setVisibility(View.GONE);

        _arrowBack.setOnClickListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_driver_origin, container, false);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        return v;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        mGoogleMap = googleMap;

        LatLng hcmus = new LatLng(gps.getLatitude(), gps.getLongitude());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, 18));
        mGoogleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador))
                .title("Ubicación")
                .position(hcmus)
        );

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

        try {
            String URL_PARAMS_API = createUrl(String.valueOf(gps.getLatitude()), String.valueOf(gps.getLongitude()));
            send(true, URL_PARAMS_API);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                LatLng location= mGoogleMap.getCameraPosition().target;
                MarkerOptions marker = new MarkerOptions().position(location).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador));
                mGoogleMap.clear();
                mGoogleMap.addMarker(marker);
                mGoogleMap.addMarker(marker);
                //mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 20));
                //mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(location));
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                //Log.e(TAG, "Coordenadasd : " + location);
                try {
                    String NEW_URL = createUrl(String.valueOf(location.latitude), String.valueOf(location.longitude));
                    //send(true, NEW_URL);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linear_layout_location:
                a = 1;
                findPlace();
                break;
            case R.id.button_destination:
                b = 2;
                findPlace();
                break;
        }
    }

    public void findPlace() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(getActivity());
            startActivityForResult(intent, 1);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                LatLng hcmus = new LatLng(PlaceAutocomplete.getPlace(getContext(), data).getLatLng().latitude, PlaceAutocomplete.getPlace(getContext(), data).getLatLng().longitude);
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                String lat = String.valueOf(place.getLatLng().latitude);
                String lng = String.valueOf(place.getLatLng().longitude);
                if(a == 1){
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, 18));
                    mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
                    try {
                        String NEW = createUrl(lat,lng);
                        send(true, NEW);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    a = 0;
                }

                if(b == 2){
                    Log.e(TAG, "Cambio de fragmento");
                    Fragment f = new DriverDestinationaFragment();
                    MainActivity m = (MainActivity) getContext();
                    m.params(f, _tvAddress.getText().toString(), _tvLocation.getText().toString(), lat, lng, "", "");
                    b = 0;
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                // TODO: Handle the error.
                Log.e(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                Log.e(TAG, "resultCanceled: " + resultCode);
            }
        }
    }

    private String createUrl(String val1, String val2) throws UnsupportedEncodingException {
        return URL + "latlng=" + val1 + "," + val2 + "&region=es&sensor=true";
    }

    public void send(boolean peticion, String newUrl){
        RequestQueue cola = Volley.newRequestQueue(getContext());
        final JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, newUrl, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                response(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "---" + error);
            }
        });
        cola.add(stringRequest);
    }

    private void response(JSONObject obj){
        Log.e(TAG, "1: \n" + obj + "\n");
        try {
            JSONArray array = obj.getJSONArray("results");
            String direcion = array.getString(0);
            for (int i = 0; i < array.length(); i++){
                JSONObject json = null;
                try{
                    JSONObject a = array.getJSONObject(i);
                    String val =  a.getString("formatted_address");
                    if(i == 0){
                        _tvOrigin.setText("Origen");
                        _tvAddress.setText("" + val);
                    }

                    if(i == array.length()-3){
                        _tvLocation.setText(val);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
