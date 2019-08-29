package org.apache.cordova.engine;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.WebChromeClient;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

class UploadHandler {

    public static Uri[] savedCaptureFileUri;

    public static WebChromeClient.FileChooserParams fileChooserParams;

    public static Uri[] parseResult(int resultCode, Intent intent) {
        if (savedCaptureFileUri != null) {
            return savedCaptureFileUri;
        }
        return fileChooserParams.parseResult(resultCode, intent);
    }

    public static Intent createIntent(WebChromeClient.FileChooserParams params) {
        fileChooserParams = params;
        savedCaptureFileUri = null;

        String acceptType = getAcceptTypesValue();

        List<Intent> intentList = new ArrayList<Intent>();

        if (acceptType.isEmpty() || acceptType.contains("image/")) {
            intentList.add(createCameraIntent());
        }

        if (acceptType.isEmpty() || acceptType.contains("video/")) {
            intentList.add(createCamcorderIntent());
        }

        if (acceptType.isEmpty() || acceptType.contains("audio/")) {
            intentList.add(createSoundRecorderIntent());
        }

        Intent[] intents = intentList.toArray(new Intent[intentList.size()]);
        return createChooserIntent(intents);
    }

    private static String getAcceptTypesValue() {
        String[] acceptTypes = fileChooserParams.getAcceptTypes();
        StringBuffer acceptTypez = new StringBuffer();
        for (int i = 0; i < acceptTypes.length; i++) {
            acceptTypez.append(acceptTypes[i]);
        }
        return acceptTypez.toString();
    }

    private static Intent createChooserIntent(Intent[] intents) {
        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
        chooser.putExtra(Intent.EXTRA_INTENT, fileChooserParams.createIntent());
        return chooser;
    }

    private static File createCaptureFile(String ext) {
        File imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(imagesDir.getPath() + "/" + timeStamp + "." + ext);
    }

    private static Intent createCameraIntent() {
        savedCaptureFileUri = new Uri[] { Uri.fromFile(createCaptureFile("jpg")) };
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, savedCaptureFileUri[0]);
        return intent;
    }

    private static Intent createCamcorderIntent() {
        savedCaptureFileUri = new Uri[] { Uri.fromFile(createCaptureFile("mp4")) };
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, savedCaptureFileUri[0]);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        return intent;
    }

    private static Intent createSoundRecorderIntent() {
        return new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
    }

}
