package com.example.offlineupi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity{

    private static final int REQUEST_READ_CONTACTS = 1;
    private ArrayList<String> contactList;
    private ListView listViewContacts;
    private ArrayList<String> filteredContacts; // Variable to hold filtered contacts

    public String selectedContactNumber;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        listViewContacts = findViewById(R.id.listViewContacts);
        contactList = new ArrayList<>();
        filteredContacts = new ArrayList<>(); // Initialize filteredContacts

        SearchView searchView = findViewById(R.id.searchView);

        // Request focus on the SearchView
        searchView.requestFocus();

        // Show the keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        } else {
            loadContacts();
        }

        listViewContacts.setOnItemClickListener((parent, view, position, id) -> {
            // Extract contact number from the clicked item
            String[] contactInfo;
            if (filteredContacts != null && filteredContacts.size() > position) {
                contactInfo = filteredContacts.get(position).split(": ");
            } else {
                contactInfo = contactList.get(position).split(": ");
            }
            selectedContactNumber = contactInfo[1];
            Toast.makeText(ContactsActivity.this, "Selected contact number: " + selectedContactNumber, Toast.LENGTH_SHORT).show();

            // Store the selected contact number using SharedPreferences
            SharedPreferences prefs = getSharedPreferences("contact_number", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("contactNumber", selectedContactNumber);
            editor.apply();

            // Start the new activity
            Intent i = new Intent(ContactsActivity.this, toPhone.class);
            startActivity(i);
            overridePendingTransition(0, 0);
        });

    }

    private void loadContacts() {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor != null) {
            ArrayList<String> tempContactList = new ArrayList<>();
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                // Remove spaces from the phone number
                number = number.replaceAll("\\s+", "");
                String contactInfo = name + ": " + number;
                if (!tempContactList.contains(contactInfo)) {
                    tempContactList.add(contactInfo);
                }
            }
            cursor.close();

            contactList.clear(); // Clear previous list

            contactList.addAll(tempContactList);
            filteredContacts.addAll(contactList); // Initially, filteredContacts will have all contacts
            updateAdapter(contactList); // Update adapter with all contacts
        } else {
            Toast.makeText(this, "No contacts found.", Toast.LENGTH_SHORT).show();
        }

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterContacts(newText);
                return true;
            }
        });
    }

    private void filterContacts(String query) {
        filteredContacts.clear(); // Clear previous filtered contacts
        for (String contact : contactList) {
            if (contact.toLowerCase().contains(query.toLowerCase())) {
                filteredContacts.add(contact);
            }
        }
        updateAdapter(filteredContacts); // Update adapter with filtered contacts
    }

    private void updateAdapter(final ArrayList<String> contacts) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.list_item_contact, R.id.textViewName, contacts) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.list_item_contact, null);
                }
                TextView textViewName = view.findViewById(R.id.textViewName);
                TextView textViewNumber = view.findViewById(R.id.textViewNumber);
                String[] contactInfo = getItem(position).split(": ");
                textViewName.setText(contactInfo[0]);
                textViewNumber.setText(contactInfo[1]);
                return view;
            }
        };

        listViewContacts.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts();
            } else {
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
