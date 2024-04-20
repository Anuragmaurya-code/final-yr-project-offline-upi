package com.example.offlineupi;// ... (existing imports)

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.offlineupi.databinding.ActivityToUpiBinding;

import android.Manifest;

import java.util.Locale;


public class toUPI extends AppCompatActivity {

    ActivityToUpiBinding binding;
    private static final int REQUEST_PHONE_CALL = 1;

    private static final String UID = "*99";
    private static String sendMoney = "*1";
    private static String toUPI = "*3#";

    public boolean isFirstTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityToUpiBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        String myValue = Menu.getMyString();
        setLocale(myValue);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isFirstTime) {
            isFirstTime = false;
            String myValue = Menu.getMyString();
            setLocale(myValue);
            recreate();

        }
    }

    public void onPayButtonClick(View view) {
        String upiID = binding.upiID.getText().toString().trim();
        String dialString = UID + sendMoney + toUPI ;
        if (!upiID.isEmpty()) {
            // Check for CALL_PHONE permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                // If permission is granted, make the call
                dialPhoneNumber(dialString);
            } else {
                // Request permission to make the call
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
            }
        } else {
            if (upiID.isEmpty()) {
                Toast.makeText(this, "Please enter a UPI ID.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PHONE_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, make the call
                onPayButtonClick(null);
            } else {
                Toast.makeText(this, "Permission denied. Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dialPhoneNumber(String dial) {
        String upiID = binding.upiID.getText().toString().trim();
        copyToClipboard();
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + Uri.encode(dial)));
        startActivity(intent);
    }

    private void copyToClipboard(){
        String textToCopy = binding.upiID.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", textToCopy);
        clipboard.setPrimaryClip(clip);
        showOverlay(binding.getRoot());
        Toast.makeText(toUPI.this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private void showOverlay(View anchorView) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.overlay_layout, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                600,
                400
        );

        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, -500);
        popupView.postDelayed(new Runnable() {
            public void run() {
                popupWindow.dismiss();
            }
        }, 6000); // 6 seconds
    }

    public void setLocale(String Lang) {
        Locale newLocale = new Locale(Lang);
        Locale currentLocale = getResources().getConfiguration().locale;

        if (!newLocale.equals(currentLocale)) {
            Locale.setDefault(newLocale);

            Resources resources = getResources();
            Configuration configuration = resources.getConfiguration();
            configuration.setLocale(newLocale);
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());

            // Restart the activity to apply the language change
            recreate();
        }
    }
}
