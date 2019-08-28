package org.apache.cordova.engine;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.WebChromeClient;

import java.io.File;

class UploadHandler {

    // 文件 platforms/android/CordovaLib/src/org/apache/cordova/engine/SystemWebChromeClient.java
    // 替换 fileChooserParams.createIntent() 为 UploadHandler.createIntent(fileChooserParams)

    public static Intent createIntent(WebChromeClient.FileChooserParams params) {
        String[] acceptTypes = params.getAcceptTypes();

        String mimeType = acceptTypes[0];

        if (mimeType.length() == 0) {
            mimeType = "*/*";
        }

        String capture = "filesystem";

        // Specified 'image/*'
        if (mimeType.equals("image/*")) {
            if (capture.equals("camera")) {
                return createCameraIntent();
            }
            Intent chooser = createChooserIntent(createCameraIntent());
            chooser.putExtra(Intent.EXTRA_INTENT, createOpenableIntent("image/*"));
            return chooser;
        }

        // Specified 'video/*'
        if (mimeType.equals("video/*")) {
            if (capture.equals("camcorder")) {
                return createCamcorderIntent();
            }
            Intent chooser = createChooserIntent(createCamcorderIntent());
            chooser.putExtra(Intent.EXTRA_INTENT, createOpenableIntent("video/*"));
            return chooser;
        }

        // Specified 'audio/*'
        if (mimeType.equals("audio/*")) {
            if (capture.equals("microphone")) {
                return createSoundRecorderIntent();
            }
            Intent chooser = createChooserIntent(createSoundRecorderIntent());
            chooser.putExtra(Intent.EXTRA_INTENT, createOpenableIntent("audio/*"));
            return chooser;
        }

        // No special, trigger the default file upload chooser.
        Intent chooser = createChooserIntent(createCameraIntent(), createCamcorderIntent(), createSoundRecorderIntent());
        chooser.putExtra(Intent.EXTRA_INTENT, createOpenableIntent("*/*"));
        return chooser;
    }

    private static Intent createChooserIntent(Intent... intents) {
        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
        return chooser;
    }

    private static Intent createOpenableIntent(String type) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType(type);
        return i;
    }

    private static Intent createCameraIntent() {
        File externalDataDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File cameraDataDir = new File(externalDataDir.getAbsolutePath() + "/app-photos");
        cameraDataDir.mkdirs();

        File cameraFile = new File(cameraDataDir.getPath() + "/" +  System.currentTimeMillis() + ".jpg");

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
        return i;
    }

    private static Intent createCamcorderIntent() {
        return new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
    }

    private static Intent createSoundRecorderIntent() {
        return new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
    }

}
