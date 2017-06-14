package com.developer.ti.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import com.developer.ti.share.Fragments.DriverOriginFragment;
import com.developer.ti.share.Fragments.HomeFragment;
import com.developer.ti.share.Helper.Config;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView _titleTop;
    private BottomNavigationView _navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectFragment(item);
                    return true;
                case R.id.navigation_running:
                    selectFragment(item);
                    return true;
                case R.id.navigation_ride:
                    return true;
                case R.id.navigation_list:
                    return true;
                case R.id.navigation_dashboard:
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _navigation = (BottomNavigationView) findViewById(R.id.navigation);
        _navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        try{
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.top_title_center);
            _titleTop = (TextView) actionBar.getCustomView().findViewById(R.id.text_view_title);
            _titleTop.setText("Inicio");
        }catch (Exception e){
            e.printStackTrace();
        }
        selectFragment(_navigation.getMenu().getItem(0));
    }

    private void selectFragment(MenuItem item){
        Fragment fragmentoGenerico = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.content);
        switch (item.getItemId()){
            case R.id.navigation_home:
                fragmentoGenerico = new HomeFragment();
                break;
            case R.id.navigation_running:
                fragmentoGenerico = new DriverOriginFragment();
                break;
        }
        if (fragmentoGenerico != null){
            fragmentManager.beginTransaction().replace(R.id.content, fragmentoGenerico).addToBackStack("F_MAIN").commit();
        }
    }

    public void params(Fragment fragment, String addressOrigin, String locationOrigin,
                       String latDestination, String lngDestination, String origin, String destination){
        Bundle bundle = new Bundle();
        bundle.putString("addressOrigin", addressOrigin);
        bundle.putString("locationOrigin", locationOrigin);
        bundle.putString("latDestination", latDestination);
        bundle.putString("lngDestination", lngDestination);
        bundle.putString("origin", origin);
        bundle.putString("destination", destination);
        fragment.setArguments(bundle);
        /*getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, fragment)
                .setCustomAnimations(android.R.anim.slide_out_right, android.R.anim.slide_in_left)
                .commit();*/
        /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_in_left, android.R.anim.slide_in_left);
        ft.replace(R.id.content, fragment, "fragment");
        // Start the animated transition.
        ft.commit();*/
        if(fragment != null){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Config.replaceFragmentWithAnimation(fragment, "fragment", transaction);
        }
    }



}
