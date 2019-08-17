package com.anrip.cordova;

import org.json.JSONException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import android.app.Activity;
import android.util.Log;

public class StatusBarHeight extends CordovaPlugin {

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        final Activity activity = this.cordova.getActivity();

        if (action.equals("height")) {
            float height = 0;
            int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                float scaleRatio = activity.getResources().getDisplayMetrics().density;
                height = activity.getResources().getDimension(resourceId) / scaleRatio;
            }
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, height));
            return true;
        }

        return false;
    }

}
