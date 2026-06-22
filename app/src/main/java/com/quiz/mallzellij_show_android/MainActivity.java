package com.quiz.mallzellij_show_android;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialButton logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }
}