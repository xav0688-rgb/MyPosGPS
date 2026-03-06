package com.mypos.gpslogger;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int GPS_PERMISSION_CODE = 100;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        setupWebView();

        // Demande permissions GPS au démarrage
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

        // Javascript obligatoire
        settings.setJavaScriptEnabled(true);

        // IndexedDB et stockage local
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);

        // Géolocalisation
        settings.setGeolocationEnabled(true);

        // Accès fichiers locaux
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);

        // Zoom
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(false);

        // Cache
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // WebViewClient : reste dans l'app
        webView.setWebViewClient(new WebViewClient());

        // WebChromeClient : gère les permissions GPS côté JS
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onGeolocationPermissionsShowPrompt(
                    String origin,
                    GeolocationPermissions.Callback callback) {

                // Accorde automatiquement la géolocalisation à la page locale
                callback.invoke(origin, true, false);
            }

        });

    }

    // ======================
    // CHARGEMENT DE LA PAGE
    // ======================

    private void loadApp() {
        // Charge depuis les assets de l'APK
        webView.loadUrl("file:///android_asset/index.html");
    }

    // ======================
    // PERMISSIONS GPS
    // ======================

    private boolean hasGpsPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
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

                Toast.makeText(
                        this,
                        "⚠️ Permission GPS refusée — l'app ne fonctionnera pas correctement.",
                        Toast.LENGTH_LONG
                ).show();

                // Charge quand même (l'app affichera une erreur GPS)
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
