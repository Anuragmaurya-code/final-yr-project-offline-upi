package com.example.offlineupi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.offlineupi.databinding.ActivityToBankAccountBinding;
import com.example.offlineupi.databinding.ActivityToPhoneBinding;

import java.util.Locale;

public class toBankAccount extends AppCompatActivity {

    ActivityToBankAccountBinding binding;

    private static final int REQUEST_PHONE_CALL = 1;
    private static final String UID = "*99";
    private static String sendMoney = "*1";
    private static String toBankAccount = "*5";

    private static final String reCheck = "*1#";

    public boolean isFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityToBankAccountBinding.inflate(getLayoutInflater());
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
        String BankAccountNumber = binding.BankAccountNumber.toString().trim();
        String IFSCCode = binding.IFSCCode.getText().toString().trim();
        String amount = binding.amount.getText().toString().trim();
        String dialString = UID + sendMoney + toBankAccount + "*" + IFSCCode + "*" + BankAccountNumber + "*" + amount + reCheck;
        if (!BankAccountNumber.isEmpty() && !amount.isEmpty() && !IFSCCode.isEmpty()) {
            // Check for CALL_PHONE permission
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                // If permission is granted, make the call
                dialPhoneNumber(dialString);
            } else {
                // Request permission to make the call
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
            }
        } else {
            if (BankAccountNumber.isEmpty()){
                Toast.makeText(this, "Please enter Bank Account Number.", Toast.LENGTH_SHORT).show();
            }
            else if (IFSCCode.isEmpty()){
                Toast.makeText(this, "Please enter IFSC code.", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Please enter the amount", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PHONE_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, make the call
                String BankAccountNumber = binding.BankAccountNumber.toString().trim();
                String IFSCCode = binding.IFSCCode.getText().toString().trim();
                String amount = binding.amount.getText().toString().trim();
                String dialString = UID + sendMoney + toBankAccount + "*" + IFSCCode + "*" + BankAccountNumber + "*" + amount + reCheck;
                dialPhoneNumber(dialString);
            } else {
                Toast.makeText(this, "Permission denied. Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void dialPhoneNumber(String dial) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + Uri.encode(dial)));
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