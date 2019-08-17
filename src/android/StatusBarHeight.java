package com.anrip.cordova;

import org.json.JSONException;

import android.app.Activity;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

public class StatusBarHeight extends CordovaPlugin {

    @Override
    public boolean execute(final String action, final CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
        final Activity activity = this.cordova.getActivity();

        if ("height".equals(action)) {
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
