package org.apache.cordova.engine;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient.FileChooserParams;
import android.webkit.WebView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;

class MyWebChromeClient extends SystemWebChromeClient {

    private static final String LOG_TAG = "MyWebChromeClient";
    private static final int FILECHOOSER_RESULTCODE = 5173;

    private FileChooserParams fileChooserParams;

    private static File createdCaptureFile;

    public MyWebChromeClient(SystemWebViewEngine parentEngine) {
        super(parentEngine);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onShowFileChooser(WebView webView, final ValueCallback<Uri[]> filePathsCallback, final FileChooserParams fileChooserParams) {
        this.fileChooserParams = fileChooserParams;
        Intent intent = createIntent();
        try {
            parentEngine.cordova.startActivityForResult(new CordovaPlugin() {
                @Override
                public void onActivityResult(int requestCode, int resultCode, Intent intent) {
                    Uri[] result = parseResult(resultCode, intent);
                    LOG.d(LOG_TAG, "Receive file chooser URL: " + result);
                    filePathsCallback.onReceiveValue(result);
                    createdCaptureFile = null;
                }
            }, intent, FILECHOOSER_RESULTCODE);
        } catch (ActivityNotFoundException e) {
            LOG.w("No activity found to handle file chooser intent.", e);
            filePathsCallback.onReceiveValue(null);
            createdCaptureFile = null;
        }
        return true;
    }

    public Intent createIntent() {
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

    public Uri[] parseResult(int resultCode, Intent intent) {
        if (createdCaptureFile != null && createdCaptureFile.exists()) {
            Uri uri = Uri.fromFile(createdCaptureFile);
            return new Uri[] { uri };
        }

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

    private String getAcceptTypesValue() {
        String[] acceptTypes = fileChooserParams.getAcceptTypes();
        StringBuffer acceptTypez = new StringBuffer();
        for (int i = 0; i < acceptTypes.length; i++) {
            acceptTypez.append(acceptTypes[i]);
        }
        return acceptTypez.toString();
    }

    private File createCaptureFile(String ext) {
        File imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(imagesDir.getPath() + "/" + timeStamp + "." + ext);
    }

    private Intent createChooserIntent(Intent[] intents) {
        Intent fileChooser = fileChooserParams.createIntent();
        if (fileChooserParams.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE) {
            fileChooser.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        Intent actionChooser = new Intent(Intent.ACTION_CHOOSER);
        actionChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
        actionChooser.putExtra(Intent.EXTRA_INTENT, fileChooser);
        return actionChooser;
    }

    private Intent createCameraIntent() {
        createdCaptureFile = createCaptureFile("jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createdCaptureFile));
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intent;
    }

    private Intent createCamcorderIntent() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        return intent;
    }

    private Intent createSoundRecorderIntent() {
        return new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
    }

}
