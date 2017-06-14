package com.developer.ti.share.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class DriverDestinationaFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener{
    private static final String TAG = DriverOriginFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "addressOrigin";
    private static final String ARG_PARAM2 = "locationOrigin";
    private static final String ARG_PARAM3 = "latDestination";
    private static final String ARG_PARAM4 = "lngDestination";
    private String mParam1;
    private String mParam2;
    private String mParam3;
    private String mParam4;
    private View rootView;
    private TextView _tvOriginAddress, _tvOriginLocation;
    private TextView _tvDestinationAddress, _tvDestinationLocation;
    private Button _btnConfirmRoute;
    private String URL = "http://maps.googleapis.com/maps/api/geocode/json?";
    private GoogleMap mGoogleMap;
    private SupportMapFragment mMapFragment;
    private Context mContext;
    private GPS gps;
    private OnFragmentInteractionListener mListener;

    public DriverDestinationaFragment() {
        // Required empty public constructor
    }

    public static DriverDestinationaFragment newInstance(String param1, String param2) {
        DriverDestinationaFragment fragment = new DriverDestinationaFragment();
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
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);
            Log.e(TAG, "Argumentos --> " + getArguments());
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        gps = new GPS(getContext());

        _tvOriginAddress = (TextView) rootView.findViewById(R.id.text_view_origin_address);
        _tvOriginLocation = (TextView) rootView.findViewById(R.id.text_view_origin_place);
        _tvDestinationAddress = (TextView) rootView.findViewById(R.id.text_view_destination_address);
        _tvDestinationLocation = (TextView) rootView.findViewById(R.id.text_view_destination_place);
        _btnConfirmRoute = (Button) rootView.findViewById(R.id.button_confirm_route);

        arguments();
        setToolbarTitle();

        _btnConfirmRoute.setOnClickListener(this);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_driver_destinationa, container, false);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_destination);
        mMapFragment.getMapAsync(this);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_confirm_route:
                MainActivity m = (MainActivity) getContext();
                String origin = _tvOriginAddress.getText().toString();
                String destination = _tvDestinationAddress.getText().toString();
                m.params(Config.F_DRIVE_PUSHING, "", "", "", "", origin, destination);
                break;

            case R.id.image_view_back_navigation:
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Config.replaceFragmentBackWithAnimation(Config.F_DRIVE_ORIGIN, "fragment", transaction);
                break;
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
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    Fragment fragment = new DriverOriginFragment();
                    fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
                    return true;
                }
                return false;
            }
        });
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

        if(getArguments() != null){
            Double lat = Double.valueOf(getArguments().getString(ARG_PARAM3));
            Double lng = Double.valueOf(getArguments().getString(ARG_PARAM4));
            try {
                String NEW_URL = createUrl(String.valueOf(lat), String.valueOf(lng));
                send(true, NEW_URL);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            LatLng latLng = new LatLng(lat, lng);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
            mGoogleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador))
                    .title("Ubicación")
                    .position(hcmus)
            );
        }

        mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                LatLng location= mGoogleMap.getCameraPosition().target;
                MarkerOptions marker = new MarkerOptions().position(location).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador));
                mGoogleMap.clear();
                mGoogleMap.addMarker(marker);
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                //mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 20));
                //mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(location));
                //mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
                        _tvDestinationAddress.setText("" + val);
                    }

                    if(i == array.length()-3){
                        _tvDestinationLocation.setText(val);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void arguments(){
        if(getArguments() != null){
            _tvOriginAddress.setText(getArguments().getString(ARG_PARAM1));
            _tvOriginLocation.setText(getArguments().getString(ARG_PARAM2));
        }
    }

    private void setToolbarTitle(){
        TextView _titleTop;
        ImageView _arrowBack;
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.top_title_center);
        _titleTop = (TextView) actionBar.getCustomView().findViewById(R.id.text_view_title);
        _arrowBack = (ImageView) actionBar.getCustomView().findViewById(R.id.image_view_back_navigation);
        _titleTop.setText("Crear ruta");
        _arrowBack.setVisibility(View.VISIBLE);
        _arrowBack.setOnClickListener(this);
    }
}
