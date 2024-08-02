package com.example.agricarea;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agricarea.database.DatabaseHelper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class signup extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    DatabaseHelper mDatabase;
    private TextView tvBirthdate;
    private Spinner countrySpinner;
    Button bt;
    EditText usern,pwd;
    TextView tl;
    ArrayList<User> allusers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        tvBirthdate = findViewById(R.id.tvBirthdate);
        bt=findViewById(R.id.btnSignUp);
        countrySpinner = findViewById(R.id.countrySpinner);

        // Load countries from CSV
        List<String> countries = loadCountriesFromCSV();

        // Set up spinner adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        // Set up adapter for SearchView
        mDatabase = new DatabaseHelper(this);
        usern=findViewById(R.id.username);
        pwd=findViewById(R.id.password);
        tl=findViewById(R.id.linkLogin);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String u = usern.getText().toString();
                String p = pwd.getText().toString();
                String d = tvBirthdate.getText().toString();
                String c = countrySpinner.getSelectedItem().toString();

                if (u.isEmpty() || p.isEmpty() || d.isEmpty() || c.isEmpty()) {
                    // Show alert dialog for empty fields
                    AlertDialog.Builder alert = new AlertDialog.Builder(signup.this);
                    alert.setMessage("Please fill in all fields");
                    alert.setTitle("Error");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Dismiss dialog if OK is clicked
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                } else if (mDatabase.checkEmail(u)) {
                    // Show alert dialog if user already exists
                    AlertDialog.Builder alert = new AlertDialog.Builder(signup.this);
                    alert.setMessage("User already exists, please try again");
                    alert.setTitle("Error");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Dismiss dialog if OK is clicked
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                } else {
                    // Add user to database and navigate to profile activity
                    mDatabase.addUsers(new User(u, p, c, d));
                    Toast.makeText(signup.this, "Signing Up...", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(signup.this, profile.class);
                    i.putExtra("email",u);
                    i.putExtra("city",c);
                    startActivity(i);
                    finishAffinity();
                }
            }
        });
        tl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(signup.this,login.class);
                startActivity(it);
            }
        });

    }

    private List<String> loadCitiesFromCSV() {
        List<String> countries = new ArrayList<>();
        Set<String> countrySet = new HashSet<>(); // To avoid duplicate countries

        try {
            InputStream inputStream = getAssets().open("tunisian_cities.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length > 1) {
                    String country = tokens[1].trim();
                    if (!countrySet.contains(country)) {
                        countrySet.add(country);
                        countries.add(country);
                    }
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return countries;
    }
    private List<String> loadCountriesFromCSV() {
        List<String> countries = new ArrayList<>();
        Set<String> countrySet = new HashSet<>(); // To avoid duplicate countries

        try {
            InputStream inputStream = getAssets().open("tunisian_cities.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length > 1) {
                    String country = tokens[1].trim();
                    if (!countrySet.contains(country)) {
                        countrySet.add(country);
                        countries.add(country);
                    }
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return countries;
    }
    public void onSelectBirthdate(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
        tvBirthdate.setText(selectedDate);
    }
}