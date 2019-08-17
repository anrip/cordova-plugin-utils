# cordova-plugin-statusbar-height

## Installation

This installation method requires cordova 5.0+

    cordova plugin add cordova-plugin-statusbar-height

StatusBarHeight
=================

Call this function to get the height of the statusbar.

    StatusBarHeight(onSuccess, onError);

    const onSuccess = (height) => {
        // do something with the statusbar height here
    }

    const onError = (error) => {
        // handle error state; should usually not ever activate
    }

Supported Platforms
-------------------

- Android
