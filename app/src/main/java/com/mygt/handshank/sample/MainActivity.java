package com.mygt.handshank.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.ui).setOnClickListener(this);
        findViewById(R.id.number).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ui:
                startActivity(new Intent(this, UITestActivity.class));
                break;
            case R.id.number:
                startActivity(new Intent(this, DeviceControlActivity.class));
                break;
        }
    }
}
