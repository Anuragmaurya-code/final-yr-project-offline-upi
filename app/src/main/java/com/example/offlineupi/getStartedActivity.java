package com.example.offlineupi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.offlineupi.databinding.ActivityGetStartedBinding;
import com.example.offlineupi.databinding.ActivityRegisterPageBinding;

import java.util.Locale;

public class getStartedActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ActivityGetStartedBinding binding;

    public static String lang = "en";
    public static final String LANG_KEY = "selected_language";
    private SharedPreferences sharedPref;

    private Spinner spinner;
    private static final String[] paths = {"English", "हिंदी (Hindi)", "தமிழ் (Tamil)", "മലയാളം (Malayalam)", "ಕನ್ನಡ (Kannada)", "తెలుగు (Telugu)"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGetStartedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        SharedPreferences prefs = getSharedPreferences("login_state", MODE_PRIVATE);
        boolean isLoggedIn  = prefs.getBoolean("isLogin", false );

        if (isLoggedIn){
            Intent i = new Intent(this,EnterPinActivity.class);
            startActivity(i);
        }

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale(lang);
                Intent i = new Intent(getStartedActivity.this,RegisterPage.class);
                startActivity(i);
            }
        });

        sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        spinner = binding.langSelect;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getStartedActivity.this,
                android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // Retrieve last selected language from SharedPreferences
        String lastSelectedLang = sharedPref.getString(LANG_KEY, "en");
        for (int i = 0; i < paths.length; i++) {
            if (paths[i].toLowerCase().contains(lastSelectedLang)) {
                spinner.setSelection(i);
                break;
            }
        }

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        switch (position) {
            case 0:
                lang = "en";
                break;
            case 1:
                lang = "hi";
                break;
            case 2:
                lang = "ta";
                break;
            case 3:
                lang = "ml";
                break;
            case 4:
                lang = "kn";
                break;
            case 5:
                lang = "te";
                break;
        }
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(LANG_KEY, lang);
        editor.apply();

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }


    public void setLocale(String Lang) {

        Locale locale = new Locale(Lang);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        recreate();
    }

}