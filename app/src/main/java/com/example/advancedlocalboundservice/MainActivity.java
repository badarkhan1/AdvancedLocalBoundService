package com.example.advancedlocalboundservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements MyLocalBoundService.Callback {

    TextView status;
    EditText et;
    Button btnstart;
    ProgressBar progressBar;
    private static final String TAG = "MTAG";
    private MyLocalBoundService myService = null;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            myService = ((MyLocalBoundService.MyLocalServiceBinder) iBinder).getService();
            myService.setCallback(MainActivity.this);
            Log.d(TAG, "onServiceConnected: " + "Service Connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            btnstart.setEnabled(false);
            myService = null;
            Log.d(TAG, "onServiceDisconnected: " + "Service Disconnected");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this,MyLocalBoundService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(myService!=null){
            myService.setCallback(null);
            unbindService(connection);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et = (EditText) findViewById(R.id.et);
        status = (TextView) findViewById(R.id.status);
        btnstart = (Button) findViewById(R.id.btnstart);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        btnstart.setEnabled(true);
    }

    public void startMyLocalBoundService(View view) {
        if(myService != null && !et.getText().toString().equals("")){
            myService.doLongRunningOperation(Integer.parseInt(et.getText().toString()));
            btnstart.setEnabled(false);
        }
    }

    @Override
    public void onProgressUpdate(int progress) {
        status.setText(String.valueOf(progress));
        progressBar.setProgress(progress);
    }

    @Override
    public void onCompletion(String result) {
        status.setText(result);
        btnstart.setEnabled(true);
    }
}
