# cordova-plugin-utils

Cordova Extra Utils

## Installation

This installation method requires cordova 5.0+

    cordova plugin add cordova-plugin-utils

# StatusBarHeight

Call this function to get the height of the statusbar.

    cdvUtils.getStatusBarHeight(onSuccess, onError);

    const onSuccess = (height) => {
        // do something with the statusbar height here
    }

    const onError = (error) => {
        // handle error state; should usually not ever activate
    }

## Supported Platforms

-   Android
