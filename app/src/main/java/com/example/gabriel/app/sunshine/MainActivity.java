package com.example.gabriel.app.sunshine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(MainActivity.class.getSimpleName(), "onCreate");
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(MainActivity.class.getSimpleName(), "onPause");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(MainActivity.class.getSimpleName(), "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(MainActivity.class.getSimpleName(), "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(MainActivity.class.getSimpleName(), "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(MainActivity.class.getSimpleName(), "onDestroy");
    }
}
