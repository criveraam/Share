package com.developer.ti.share.Interfaces;

import com.developer.ti.share.Models.Route;

import java.util.List;

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
