package com.example.offlineupi;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SetPinActivity extends AppCompatActivity {

    private EditText pinEditText;
    private EditText confirmPinEditText;

    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pin);

        confirmPinEditText = findViewById(R.id.editTextConfirmPin);
        pinEditText = findViewById(R.id.editTextPin);
        submitButton = findViewById(R.id.btnSubmit); // Initialize submitButton

        setupSubmitButton();
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredPin = pinEditText.getText().toString();
                String confirmedPin = confirmPinEditText.getText().toString(); // Retrieve confirmed PIN

                if (enteredPin.length() != 4 || confirmedPin.length() != 4) {
                    // PIN length is not 4 digits for either PIN
                    Toast.makeText(SetPinActivity.this, "PIN must be 4 digits", Toast.LENGTH_SHORT).show();
                } else if (!enteredPin.equals(confirmedPin)) {
                    // Entered PIN does not match confirmed PIN
                    Toast.makeText(SetPinActivity.this, "PINs do not match", Toast.LENGTH_SHORT).show();
                } else {
                    // PIN is valid and confirmed
                    try {
                        String encryptedPin = EncryptionHelper.encrypt(enteredPin);
                        storePinAndSecurityQuestions(encryptedPin);
                        Toast.makeText(SetPinActivity.this, "PIN and security questions set successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SetPinActivity.this, SecurityQuestionActivity.class));
                        finish(); // Finish the activity after setting the PIN
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(SetPinActivity.this, "Error encrypting PIN", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void storePinAndSecurityQuestions(String pin) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("PIN", pin);
        editor.apply();
    }
}






















