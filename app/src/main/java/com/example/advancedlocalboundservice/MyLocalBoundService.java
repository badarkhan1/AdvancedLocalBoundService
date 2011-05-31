package com.example.advancedlocalboundservice;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

public class MyLocalBoundService extends Service {

    private MyLocalServiceBinder myLocalBinder = new MyLocalServiceBinder();
    private static final int NOTIFICATION_ID = 1001;
    private static final String TAG = "MTAG";
    private Callback callback;
    private int total = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return myLocalBinder;
    }

    class MyLocalServiceBinder extends Binder{
        MyLocalBoundService getService(){
            return MyLocalBoundService.this;
        }
    }

    private Notification buildNotification(){
        return new NotificationCompat.Builder(this)
                .setContentTitle("MyLocalBoundService")
                .setContentText("I am a local bound service")
                .build();
    }

    public void setCallback(Callback callback){
        this.callback = callback;
    }

    public void doLongRunningOperation(int input){
        new MyTask().execute(input);
    }

    private class MyTask extends AsyncTask<Integer,Integer,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startForeground(NOTIFICATION_ID,buildNotification());
        }

        @Override
        protected String doInBackground(Integer... integers) {
            total = integers[0];
            for (int i = 0; i < integers[0]; i++) {
                publishProgress((i+1));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "doInBackground: " + (i+1));
            }

            return "Operation Complete";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if(values.length>0){
                for (Integer val : values){
                    if(callback!=null && values.length>0){
                        callback.onProgressUpdate((int)(((double)val/total)*100));
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(callback!=null){
                callback.onCompletion(s);
            }
            stopForeground(true);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            stopForeground(true);
        }
    }

    interface Callback{
        void onProgressUpdate(int progress);
        void onCompletion(String result);
    }
}












