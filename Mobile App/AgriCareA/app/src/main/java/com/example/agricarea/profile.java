package com.example.agricarea;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import com.example.agricarea.database.DatabaseHelper;

public class profile extends AppCompatActivity {
    TextView e, p, b;
    RadioButton r1, r2;
    RadioGroup rg;
    String txt;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView meteoImageView = findViewById(R.id.meteo);
        ImageView leafImageView = findViewById(R.id.leaf);
        ImageView encyImageView = findViewById(R.id.ency);
        ImageView editImage = findViewById(R.id.edit);

        // Create floating animations
        startFloatingAnimation(meteoImageView, 0);
        startFloatingAnimation(leafImageView, 1000);
        startFloatingAnimation(encyImageView, 2000);
        startFloatingAnimation(editImage, 3000);

        db = new DatabaseHelper(this);
        Toolbar toolbar = findViewById(R.id.tb);
        setSupportActionBar(toolbar);

        e = findViewById(R.id.tvUsername);
        p = findViewById(R.id.tvCity);
        b = findViewById(R.id.tvBirth);

        Intent intent = getIntent();
        Bundle t = intent.getExtras();
        if (t != null) {
            txt = t.getString("email");
            e.setText(txt);
            User user = db.findUsers(txt);
            if (user != null) {
                p.setText(user.getCity());
                b.setText(user.getBirth());
            }
        }

        meteoImageView.setOnClickListener(v -> {
            Intent intm = new Intent(profile.this, meteo.class);
            startActivity(intm);
        });

        editImage.setOnClickListener(v -> {
            Intent wik = new Intent(profile.this, encyclo.class);
            startActivity(wik);
        });

        leafImageView.setOnClickListener(v -> {
            Intent il = new Intent(profile.this, MainActivity.class);
            startActivity(il);
        });

        encyImageView.setOnClickListener(v -> {
            Intent il = new Intent(profile.this, ChatBotActivity.class);
            startActivity(il);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Logout");
        menu.add(0, 2, 0, "Quit");
        menu.add(0, 3, 0, "Infos");
        menu.add(0,4,0,"Edit Your Profile");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Intent it = new Intent(profile.this, HomeActivity.class);
                startActivity(it);
                finish();
                break;
            case 2:
                finish();
                break;
            case 3:
                Intent inf = new Intent(profile.this, Infos.class);
                startActivity(inf);
                break;
            case 4:
                addTaskDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startFloatingAnimation(ImageView imageView, long delay) {
        // Move the image view up and down
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "translationX", -20f, 20f, -20f);
        animator.setDuration(3000); // Duration in milliseconds
        animator.setInterpolator(new LinearOutSlowInInterpolator());
        animator.setStartDelay(delay); // Delay animation
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.start();
    }

    private void addTaskDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.edit_profile, null);
        final EditText nameField = subView.findViewById(R.id.enter_name);
        final EditText noField = subView.findViewById(R.id.enter_phno);
        rg = subView.findViewById(R.id.rg);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("EDIT YOUR PROFILE");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("CONFIRM", (dialog, which) -> {
            final String name = nameField.getText().toString();
            final String ph_no = noField.getText().toString();
            int checkedRadioButtonId = rg.getCheckedRadioButtonId();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(ph_no)) {
                Toast.makeText(profile.this, "Empty Fields", Toast.LENGTH_LONG).show();
            } else if (!name.equals(ph_no)) {
                Toast.makeText(profile.this, "Uncompatible Fields", Toast.LENGTH_LONG).show();
            } else {
                User user = db.findUsers(txt);
                if (user != null) {
                    int userId = user.getId();
                    if (checkedRadioButtonId == R.id.radiouser) {
                        User newContact = new User(userId, name, user.getPwd(), user.getCity(), user.getBirth());
                        db.updateContacts(newContact);
                        e.setText(name);
                    } else {
                        User newContact = new User(userId, txt, name, user.getCity(), user.getBirth());
                        db.updateContacts(newContact);
                    }
                }
            }
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> {
            Toast.makeText(profile.this, "EDIT CANCELLED", Toast.LENGTH_LONG).show();
        });
        builder.show();
    }
}
