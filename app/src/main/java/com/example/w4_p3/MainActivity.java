package com.example.w4_p3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private SeekBar seekBar;
    private int seekBarProgress;
    public String prevPosDir = "A", largestPosDir;

    private double[] prevPos = new double[3];

    private double currAccel, prevVal;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    public void changeDisplayURL(String option) {
        String URL;
        if (option.equals("X")) URL = "https://www.ecosia.org/";
        else if (option.equals("Y")) URL = "https://www.dogpile.com/";
        else if (option.equals("Z")) URL = "https://webb.nasa.gov/";
        else if (option.equals("DIZZ ME!")) URL = "https://jumpingjaxfitness.files.wordpress.com/2010/07/dizziness.jpg ";

        // random 404 error msg page - FOR FUN
        else URL = "https://github.com/karenLee57/karenLee57.github.io-hangman";

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl(URL);
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            double[] currPos = new double[]{sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]};

            currAccel = Math.sqrt((currPos[0]*currPos[0] + currPos[1]*currPos[1] + currPos[2]*currPos[2]));
            double changeInAccel = Math.abs(currAccel - prevVal);
            prevVal = currAccel;

            double[] changedPos =  new double[3];
            for(int i = 0; i < changedPos.length; i++){
                changedPos[i] = Math.abs(currPos[i] - prevPos[i]);
                prevPos[i] = currPos[i];
            }

            double largestPosChange = changedPos[0]; largestPosDir = "X";
            if (changedPos[1] > largestPosChange) { largestPosChange = changedPos[1]; largestPosDir = "Y"; }
            if (changedPos[2] > largestPosChange) { largestPosChange = changedPos[2]; largestPosDir = "Z"; }

            if (largestPosChange > seekBarProgress && !prevPosDir.equals(largestPosDir)) {
                String toastText = ("Movement on the " + largestPosDir + " axis");

                // To test how much the change was & if seekBar works!
                // toastText = ("Movement on the " + largestPosDir + " axis by " + String.valueOf(largestPosChange));

                Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
                Log.i(largestPosDir, ("Change in " + largestPosDir + " axis"));
                changeDisplayURL(largestPosDir);
                prevPosDir = largestPosDir;
            }
            if (largestPosChange > 20) changeDisplayURL("DIZZ ME!");
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FIX ORIENTATION - PORTRAIT
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        textView = (TextView) findViewById(R.id.textView) ;

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBarProgress = 10; // DEFAULT VAL

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView.setText("Significant Change = " + String.valueOf(i));
                seekBarProgress = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }
}

