package org.apache.cordova.engine;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.WebChromeClient.FileChooserParams;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class UploadHandler {

    public static File createdCaptureFile;

    public static FileChooserParams fileChooserParams;

    public static Uri[] parseResult(int resultCode, Intent intent) {
        if (createdCaptureFile != null && createdCaptureFile.exists()) {
            Uri uri = Uri.fromFile(createdCaptureFile);
            createdCaptureFile = null;
            return new Uri[] { uri };
        }
        createdCaptureFile = null;

        if (intent.getClipData() != null && intent.getClipData().getItemCount() > 1) {
            int itemCount = intent.getClipData().getItemCount();
            Uri[] result = new Uri[itemCount];
            for (int i = 0; i < itemCount; i++) {
                result[i] = intent.getClipData().getItemAt(i).getUri();
            }
            return result;
        }

        return fileChooserParams.parseResult(resultCode, intent);
    }

    public static Intent createIntent(FileChooserParams params) {
        fileChooserParams = params;

        if (createdCaptureFile != null) {
            return null;
        }

        String acceptType = getAcceptTypesValue();

        ArrayList<Intent> intentList = new ArrayList<Intent>();

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

    private static File createCaptureFile(String ext) {
        File imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(imagesDir.getPath() + "/" + timeStamp + "." + ext);
    }

    private static Intent createChooserIntent(Intent[] intents) {
        Intent fileChooser = fileChooserParams.createIntent();
        if (fileChooserParams.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE) {
            fileChooser.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        Intent actionChooser = new Intent(Intent.ACTION_CHOOSER);
        actionChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
        actionChooser.putExtra(Intent.EXTRA_INTENT, fileChooser);
        return actionChooser;
    }

    private static Intent createCameraIntent() {
        createdCaptureFile = createCaptureFile("jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createdCaptureFile));
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

    private static Intent createCamcorderIntent() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        return intent;
    }

    private static Intent createSoundRecorderIntent() {
        return new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
    }

}
