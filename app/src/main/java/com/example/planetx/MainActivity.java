package com.example.planetx;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * This simple and main Activity contains the FragmentContainerView which hosts all fragments used in this application.
 * It utilizes Android Jetpack's Navigation component for creating fragments and navigation among fragments. See res/navigation/nav_graph.xml
 * The background for the default configuration is also set in this class's layout.
 *
 * The images setup_background and planet_x_street_view are not mine. They were found online and used solely for the purpose aesthetic.
 * In order to generate a QR code (valid or not), you may visit https://codesandbox.io/s/yp1pmpjo4z?file=/index.js
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}