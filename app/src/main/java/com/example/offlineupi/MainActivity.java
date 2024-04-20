package com.example.offlineupi;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.offlineupi.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;



public class MainActivity extends Menu{
    private static final String UID = "*99";
    private static final String Balance = "*3#";
    private static final String sendMoney = "*1";
    private static final String toBankAccount = "*5#";

    private static final String transaction = "*6";
    private static final String recentTrancation = "*1#";

    private static final String toUPIid = "*3#";

    ActivityMainBinding binding;
    private static final int REQUEST_PHONE_CALL = 1;

    public boolean isFirstTime = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        SharedPreferences prefs = getSharedPreferences("login_state", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLogin", true);
        editor.apply();





        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedLang = sharedPref.getString(LANG_KEY, "en");

        if (isDeviceRooted()) {
            finishAffinity();
            Toast.makeText(this, "Your device lack the security to run this app", Toast.LENGTH_SHORT).show();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        }

        String myValue = Menu.getMyString();
        setLocale(savedLang);

        binding.searchCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });


        binding.searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContactsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });


    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isFirstTime) {
            isFirstTime = false;
            String myValue = Menu.getMyString();
            setLocale(myValue);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PHONE_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied. Phone functionality may not work.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //root checker
    public boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3() || isGooglePlayServicesAvailable();
    }

    private boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private boolean checkRootMethod2() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            process.getOutputStream().close();
            int exitValue = process.waitFor();
            return exitValue == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkRootMethod3() {
        File file = new File("/system/app/Magisk.apk");
        return file.exists();
    }


    private boolean isGooglePlayServicesAvailable() {
        int availability = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        return availability != ConnectionResult.SUCCESS;
    }

    private static boolean checkCustomRecovery() {
        // List of common custom recovery filenames
        String[] customRecoveryFiles = {
                "/proc/last_kmsg",  // File often modified by custom recoveries
                "/etc/recovery.fstab" // Another file that may indicate a custom recovery
                // Add more filenames as needed
        };

        for (String filename : customRecoveryFiles) {
            if (new File(filename).exists()) {
                return true; // Found a file associated with custom recovery

            }
        }

        return false; // No file associated with custom recovery found
    }

    //root checker4

    public void onMenuClick(View view){
        Intent intent = new Intent(MainActivity.this, Menu.class);
        startActivity(intent);
    }



    public void onScannerClick(View view){
        Intent i = new Intent(MainActivity.this, ScanQRActivity.class);
        startActivity(i);
    }

    public void onPhoneClick(View view){
        Intent i = new Intent(MainActivity.this, toPhone.class);
        startActivity(i);
    }

    public void onUPIClick(View view){
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + Uri.encode(UID + sendMoney + toUPIid)));
        startActivity(intent);
    }
    public void onBankClick(View view){
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + Uri.encode(UID + sendMoney + toBankAccount)));
        startActivity(intent);
    }

    public void onRecentTransactionClick(View view){
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + Uri.encode(UID + transaction + recentTrancation)));
        startActivity(intent);
    }

    public void onCheckBalanceClick(View view){
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + Uri.encode(UID + Balance)));
        startActivity(intent);
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
