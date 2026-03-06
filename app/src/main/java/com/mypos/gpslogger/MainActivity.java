package com.mypos.gpslogger;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int GPS_PERMISSION_CODE = 100;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        setupWebView();

        if (!hasGpsPermission()) {
            requestGpsPermission();
        } else {
            loadApp();
        }
    }

    // ======================
    // CONFIGURATION WEBVIEW
    // ======================

    private void setupWebView() {

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // Pont JS → Java pour l'export fichier
        webView.addJavascriptInterface(new ExportBridge(), "AndroidExport");

        webView.setWebViewClient(new WebViewClient());

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(
                    String origin,
                    GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });
    }

    // ======================
    // PONT JAVASCRIPT → JAVA
    // ======================

    public class ExportBridge {

        @JavascriptInterface
        public void saveJSON(String jsonData) {

            try {
                String timestamp = new SimpleDateFormat(
                    "yyyyMMdd_HHmmss", Locale.getDefault()
                ).format(new Date());

                String filename = "gps_export_" + timestamp + ".json";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10+ : MediaStore (pas besoin de permission)
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Downloads.DISPLAY_NAME, filename);
                    values.put(MediaStore.Downloads.MIME_TYPE, "application/json");
                    values.put(MediaStore.Downloads.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS);

                    Uri uri = getContentResolver().insert(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

                    if (uri != null) {
                        try (OutputStream os = getContentResolver().openOutputStream(uri)) {
                            os.write(jsonData.getBytes("UTF-8"));
                        }
                    }

                } else {
                    // Android 9 et moins : accès direct
                    File dir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS);
                    dir.mkdirs();
                    File file = new File(dir, filename);
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        fos.write(jsonData.getBytes("UTF-8"));
                    }
                }

                // Notification sur le thread UI
                runOnUiThread(() ->
                    Toast.makeText(
                        MainActivity.this,
                        "✅ Exporté : " + filename + "\n📁 Dossier Téléchargements",
                        Toast.LENGTH_LONG
                    ).show()
                );

            } catch (Exception e) {
                runOnUiThread(() ->
                    Toast.makeText(
                        MainActivity.this,
                        "❌ Erreur export : " + e.getMessage(),
                        Toast.LENGTH_LONG
                    ).show()
                );
            }
        }
    }

    // ======================
    // CHARGEMENT DE LA PAGE
    // ======================

    private void loadApp() {
        webView.loadUrl("file:///android_asset/index.html");
    }

    // ======================
    // PERMISSIONS GPS
    // ======================

    private boolean hasGpsPermission() {
        return ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestGpsPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                },
                GPS_PERMISSION_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == GPS_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadApp();
            } else {
                Toast.makeText(this,
                    "⚠️ Permission GPS refusée",
                    Toast.LENGTH_LONG).show();
                loadApp();
            }
        }
    }

    // ======================
    // BOUTON RETOUR
    // ======================

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
