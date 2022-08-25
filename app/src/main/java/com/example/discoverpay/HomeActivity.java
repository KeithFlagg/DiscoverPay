package com.example.discoverpay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;

        //on click opens the new activity
        switch(v.getId()){
            case R.id.loginButton: i = new Intent(this, PaymentActivity.class); startActivity(i); break;
            default:break;
        }
    }

    @Override
    public void onBackPressed() {
    }
}
