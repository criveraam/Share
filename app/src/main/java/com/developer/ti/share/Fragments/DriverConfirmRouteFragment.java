package com.developer.ti.share.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.developer.ti.share.Helper.Config;
import com.developer.ti.share.R;

public class DriverConfirmRouteFragment extends Fragment implements View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private View rootView;
    private Button _btnConfirmRoute;

    public DriverConfirmRouteFragment() {
        // Required empty public constructor
    }

    public static DriverConfirmRouteFragment newInstance(String param1, String param2) {
        DriverConfirmRouteFragment fragment = new DriverConfirmRouteFragment();
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
        _btnConfirmRoute = (Button) rootView.findViewById(R.id.button_confirm_route_validate);
        _btnConfirmRoute.setOnClickListener(this);
        setToolbarTitle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driver_confirm_route, container, false);
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
    public void onClick(View v) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        switch (v.getId()){
            case R.id.button_confirm_route_validate:
                Config.replaceFragmentWithAnimation(Config.F_HOME, "fragment", transaction);
                break;
            case R.id.image_view_back_navigation:
                Config.replaceFragmentBackWithAnimation(Config.F_DRIVE_PUSHING, "fragmento", transaction);
                break;
        }
    }

    private void arguments(){}

    private void setToolbarTitle(){
        TextView _titleTop;
        ImageView _arrowBack;
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.top_title_center);
        _titleTop = (TextView) actionBar.getCustomView().findViewById(R.id.text_view_title);
        _arrowBack = (ImageView) actionBar.getCustomView().findViewById(R.id.image_view_back_navigation);
        _titleTop.setText("Confirmar Ruta");
        _arrowBack.setVisibility(View.VISIBLE);
        _arrowBack.setOnClickListener(this);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
