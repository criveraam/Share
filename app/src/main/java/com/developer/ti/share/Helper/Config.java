package com.developer.ti.share.Helper;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;

import com.developer.ti.share.R;

/**
 * Created by tecnicoairmovil on 12/06/17.
 */

public class Config {

    public static void replaceFragmentWithAnimation(android.support.v4.app.Fragment fragment, String tag, FragmentTransaction transaction){
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.content, fragment);
        transaction.addToBackStack(tag);
        transaction.commit();
    }
}
