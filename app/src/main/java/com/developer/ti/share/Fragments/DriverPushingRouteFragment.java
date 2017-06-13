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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.developer.ti.share.Helper.Config;
import com.developer.ti.share.Helper.DirectionFinder;
import com.developer.ti.share.Helper.GPS;
import com.developer.ti.share.Interfaces.DirectionFinderListener;
import com.developer.ti.share.Models.Route;
import com.developer.ti.share.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class DriverPushingRouteFragment extends Fragment implements OnMapReadyCallback, DirectionFinderListener{
    private static final String TAG = DriverPushingRouteFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "origin";
    private static final String ARG_PARAM2 = "destination";
    private String mParam1;
    private String mParam2;
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyCjkrOzL5xwoQvjK9RQt9VQ4G9O33h3RVM";
    private View rootView;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mMapFragment;
    private GPS gps;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private TextView _tvOriginOrigin, _tvDestinationOrigin;
    private TextView _tvOriginAddress, _tvDestinationAddress;
    private Button _btnConfirmRoute;


    private OnFragmentInteractionListener mListener;

    public DriverPushingRouteFragment() {
        // Required empty public constructor
    }

    public static DriverPushingRouteFragment newInstance(String param1, String param2) {
        DriverPushingRouteFragment fragment = new DriverPushingRouteFragment();
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

        _tvOriginOrigin = (TextView) rootView.findViewById(R.id.text_view_origin_origin);
        _tvDestinationOrigin = (TextView) rootView.findViewById(R.id.text_view_destination_origin);
        _tvOriginAddress = (TextView) rootView.findViewById(R.id.text_view_origin_address);
        _tvDestinationAddress = (TextView) rootView.findViewById(R.id.text_view_destination_address);
        _btnConfirmRoute = (Button) rootView.findViewById(R.id.button_confirm_routes);

        _btnConfirmRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Fragment f = null;
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                f = new ConfirmRouteFragment();
                fragmentManager.beginTransaction().replace(R.id.content, f).commit();*/

                Fragment f = new ConfirmRouteFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Config.replaceFragmentWithAnimation(f, "fragment", transaction);
            }
        });


        if (getArguments() != null) {
            _tvOriginOrigin.setText("Origen");
            _tvOriginAddress.setText(getArguments().getString(ARG_PARAM1));
            _tvDestinationOrigin.setText("Destino");
            _tvDestinationAddress.setText(getArguments().getString(ARG_PARAM2));
        }

        try {
            new DirectionFinder(this,getArguments().getString(ARG_PARAM1), getArguments().getString(ARG_PARAM2)).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_driver_pushing_route, container, false);
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_confirm_route);
        mMapFragment.getMapAsync(this);
        return v;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
                    Fragment fragment = new DriverDestinationaFragment();
                    fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
                    return true;
                }
                return false;
            }
        });
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

    private String createUrl(String urlOrigin, String urlDestination) throws UnsupportedEncodingException {
        String urlOrigin1 = URLEncoder.encode(urlOrigin, "utf-8");
        String urlDestination1 = URLEncoder.encode(urlDestination, "utf-8");
        return DIRECTION_URL_API + "origin=" + urlOrigin1 + "&destination=" + urlDestination1;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        mGoogleMap = googleMap;

        LatLng hcmus = new LatLng(gps.getLatitude(), gps.getLongitude());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, 18));


        /*Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(hcmus)
                .title("Origen")
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.puntoa))
                .snippet("FROM")
        );*/

        //marker.showInfoWindow();*/
        mGoogleMap.setBuildingsEnabled(true);
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

        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);





    }

    public void send(boolean peticion, String newUrl) {
        RequestQueue cola = Volley.newRequestQueue(getContext());
        final JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, newUrl, new Response.Listener<JSONObject>() {
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

    private void response(JSONObject obj) {
        Log.e(TAG, "JSONObject: \n" + obj + "\n");

        try {
            JSONArray jsonArray = obj.getJSONArray("routes");
            JSONObject route = jsonArray.getJSONObject(0);
            JSONArray legs;
            JSONObject leg;
            JSONArray steps;
            JSONObject dist;
            Integer distance;
            if (route.has("legs")) {
                legs = route.getJSONArray("legs");
                Log.e(TAG, "jsonArray: " + legs) ;
                leg = legs.getJSONObject(0);
                steps = leg.getJSONArray("steps");
                Log.e(TAG, "steps: " + steps) ;
                int nsteps = steps.length();
                for (int i = 0; i < nsteps; i++) {
                    JSONObject step = steps.getJSONObject(i);


                    //Log.e(TAG, "----step : " + step) ;
                    /*if (step.has("distance")) {
                        dist = (JSONObject) step.get("distance");
                        Log.e(TAG, "dist: " + dist) ;
                        if (dist.has("value")) {
                            distance = (Integer) dist.get("value");
                            Log.e(TAG, "distance: " + distance);
                        }
                    }
                    */JSONObject jsonObject = step.getJSONObject("start_location");
                    Double lat = jsonObject.getDouble("lat");
                    Double lng = jsonObject.getDouble("lng");
                    Log.e(TAG, "----lat: " + lat + " ----lng: " + lng);
                    mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                    mGoogleMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(lat, lng))
                            .width(10)
                            .color(R.color.colorPrimary)
                            .geodesic(true));
                    // move camera to zoom on map
                    //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gps.getLatitude(), gps.getLongitude()),13));


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDirectionFinderStart() {
        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> route) {

        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route1 : route) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route1.startLocation, 16));
            //((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            //((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(route1.startLocation)
                    .title(route1.startAddress)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.puntoa))
                    .snippet("Origen"));
            originMarkers.add(mGoogleMap.addMarker(new MarkerOptions()
                    .position(route1.startLocation)
                    .title(route1.startAddress)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.puntoa))
                    .snippet("Origen")));



            Marker marker1 = mGoogleMap.addMarker(new MarkerOptions()
                    .title(route1.endAddress)
                    .position(route1.endLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.puntob))
                    .snippet("Destino"));


            destinationMarkers.add(mGoogleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.puntob))
                    .title(route1.endAddress)
                    .position(route1.endLocation)));


            //marker1.showInfoWindow();
            //marker.showInfoWindow();

            try{
                PolylineOptions polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(getResources().getColor(R.color.colorPrimary)).
                        width(10);

                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                //the include method will calculate the min and max bound.
                builder.include(marker.getPosition());
                builder.include(marker1.getPosition());
                LatLngBounds bounds = builder.build();
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                int padding = (int) (width * 0.15); // offset from edges of the map 10% of screen

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                mGoogleMap.animateCamera(cu);

                for (int i = 0; i < route1.points.size(); i++)
                    polylineOptions.add(route1.points.get(i));

                polylinePaths.add(mGoogleMap.addPolyline(polylineOptions));

            }catch (Exception e){
                e.printStackTrace();
            }





        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
