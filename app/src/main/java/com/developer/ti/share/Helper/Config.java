package com.developer.ti.share.Helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.developer.ti.share.Fragments.DriverConfirmRouteFragment;
import com.developer.ti.share.Fragments.DriverDestinationaFragment;
import com.developer.ti.share.Fragments.DriverOriginFragment;
import com.developer.ti.share.Fragments.DriverPushingRouteFragment;
import com.developer.ti.share.Fragments.HomeFragment;
import com.developer.ti.share.R;

/**
 * Created by tecnicoairmovil on 12/06/17.
 */

public class Config {

    public static Fragment F_HOME = new HomeFragment();
    public static Fragment F_DRIVE_ORIGIN = new DriverOriginFragment();
    public static Fragment F_DRIVE_DESTINATION = new DriverDestinationaFragment();
    public static Fragment F_DRIVE_PUSHING = new DriverPushingRouteFragment();
    public static Fragment F_DRIVE_CONFIRM_ROUTE = new DriverConfirmRouteFragment();

    public static void replaceFragmentWithAnimation(android.support.v4.app.Fragment fragment, String tag, FragmentTransaction transaction){
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.content, fragment);
        transaction.commit();
    }

    public static void replaceFragmentBackWithAnimation(android.support.v4.app.Fragment fragment, String tag, FragmentTransaction transaction){
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.content, fragment);
        transaction.addToBackStack(tag);
        transaction.commit();
    }
}
