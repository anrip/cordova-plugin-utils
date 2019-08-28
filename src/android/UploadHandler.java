package org.apache.cordova.engine;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.WebChromeClient;

import java.io.File;

class UploadHandler {

    public static Intent createIntent(WebChromeClient.FileChooserParams params) {
        String[] acceptTypes = params.getAcceptTypes();

        String acceptType = acceptTypes[0];
        if (acceptType.length() > 0) {
            acceptType = acceptType.split("/")[0];
        }

        // Specified 'image/*'
        if (acceptType.equals("image")) {
            if (params.isCaptureEnabled()) {
                return createCameraIntent();
            }
            Intent chooser = createChooserIntent(createCameraIntent());
            chooser.putExtra(Intent.EXTRA_INTENT, createOpenableIntent("image/*"));
            return chooser;
        }

        // Specified 'video/*'
        if (acceptType.equals("video")) {
            if (params.isCaptureEnabled()) {
                return createCamcorderIntent();
            }
            Intent chooser = createChooserIntent(createCamcorderIntent());
            chooser.putExtra(Intent.EXTRA_INTENT, createOpenableIntent("video/*"));
            return chooser;
        }

        // Specified 'audio/*'
        if (acceptType.equals("audio")) {
            if (params.isCaptureEnabled()) {
                return createSoundRecorderIntent();
            }
            Intent chooser = createChooserIntent(createSoundRecorderIntent());
            chooser.putExtra(Intent.EXTRA_INTENT, createOpenableIntent("audio/*"));
            return chooser;
        }

        // No special, trigger the default file upload chooser.
        Intent chooser = createChooserIntent(createCameraIntent(), createCamcorderIntent(), createSoundRecorderIntent());
        chooser.putExtra(Intent.EXTRA_INTENT, params.createIntent());
        return chooser;
    }

    private static Intent createChooserIntent(Intent... intents) {
        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
        return chooser;
    }

    private static Intent createOpenableIntent(String type) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(type);
        return intent;
    }

    private static Intent createCameraIntent() {
        File externalDataDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File cameraDataDir = new File(externalDataDir.getAbsolutePath() + "/app-photos");
        cameraDataDir.mkdirs();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        File cameraFile = new File(cameraDataDir.getPath() + "/" + System.currentTimeMillis() + ".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
        return intent;
    }

    private static Intent createCamcorderIntent() {
        return new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
    }

    private static Intent createSoundRecorderIntent() {
        return new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
    }

}
