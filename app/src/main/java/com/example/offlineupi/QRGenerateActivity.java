package com.example.offlineupi;

import static java.util.Collections.replaceAll;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.example.offlineupi.databinding.ActivityMenuBinding;
import com.example.offlineupi.databinding.ActivityQrgenerateBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRGenerateActivity extends AppCompatActivity {

    ActivityQrgenerateBinding binding;

    private static String UPIpay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrgenerateBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String name = preferences.getString("myName", "");
        String upiId = preferences.getString("myUPIid", "");


        if (upiId.isEmpty()) {
            // Set default value if UPI ID is empty
            upiId = "Enter UPI id";
        }

        binding.myName.setText("Name: " + name);
        binding.myUPIid.setText("UPI ID: " + upiId);


        UPIpay = "upi://pay?pa=" + upiId + "&pn=" + name;
        generateQRCode(UPIpay);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(QRGenerateActivity.this, Menu.class);
                startActivity(i);
            }
        });


    }

    private void generateQRCode(String text) {
        binding.loadingProgressBar.setVisibility(View.VISIBLE); // Show loading bar
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        new Thread(() -> {
            try {
                BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 512, 512);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bitmap.setPixel(x, y, bitMatrix.get(x, y) ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white));
                    }
                }
                runOnUiThread(() -> {
                    binding.qrImageView.setImageBitmap(bitmap);
                    binding.qrImageView.setVisibility(View.VISIBLE);
                    binding.loadingProgressBar.setVisibility(View.GONE); // Hide loading bar
                });
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }).start();
    }
}