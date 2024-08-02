package com.example.agricarea;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.example.agricarea.database.DatabaseHelper;

public class login extends AppCompatActivity {
    Button b;
    EditText use,pwd;
    DatabaseHelper mDatabase;
    TextView ts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mDatabase = new DatabaseHelper(this);

        b=findViewById(R.id.btn);
        use=findViewById(R.id.username);
        pwd=findViewById(R.id.password);
        ts=findViewById(R.id.link1);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String u = use.getText().toString();
                String p = pwd.getText().toString();
                if (u.isEmpty() || p.isEmpty()) {
                    // Show alert dialog for empty fields
                    AlertDialog.Builder alert = new AlertDialog.Builder(login.this);
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
                } else if (!mDatabase.checkEmailPassword(u, p)) {
                    // Show alert dialog if user already exists
                    AlertDialog.Builder alert = new AlertDialog.Builder(login.this);
                    alert.setMessage("User doesn't exist exists, please try again");
                    alert.setTitle("Error");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Dismiss dialog if OK is clicked
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }else {
                    // Add user to database and navigate to profile activity
                    Toast.makeText(login.this, "Logging In...", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(login.this, profile.class);
                    i.putExtra("email",u);
                    startActivity(i);
                    finishAffinity();
                }
            }

        });

        ts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(login.this,signup.class);
                startActivity(it);

            }
        });
    }

}