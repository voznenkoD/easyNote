package org.eljetto.easynote;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    ImageView mImageView;
    ImageButton mButton;
    boolean isActive;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    private TextView timerValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.image);
        mButton = (ImageButton) findViewById(R.id.start);
        timerValue = (TextView) findViewById(R.id.timer);

        mButton.setOnClickListener(new View.OnClickListener() {
            ChangeImageTask changeImageTask;
            @Override
            public void onClick(View v) {
                isActive = !isActive;
                if (isActive) {
                    changeImageTask = new ChangeImageTask();
                    changeImageTask.execute();
                    mButton.setImageResource(R.drawable.pause);
                    startTime = SystemClock.uptimeMillis();
                    customHandler.postDelayed(updateTimerThread, 0);
                } else {
                    changeImageTask.cancel(true);
                    mButton.setImageResource(R.drawable.play);
                    timeInMilliseconds = 0;
                    customHandler.removeCallbacks(updateTimerThread);
                }
            }
        });
        super.onCreate(savedInstanceState);
    }

    private class ChangeImageTask extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... params) {
            while (isActive) {
                for (int i = 1; i < 6; i++) {
                    try {
                        publishProgress(i);
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                        timeInMilliseconds = 0;
                        customHandler.removeCallbacks(updateTimerThread);
                    }
                }
            }
            return "Stopped";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mImageView.setImageResource(getResources().getIdentifier("note" + values[0], "drawable", getPackageName()));
        }
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            timerValue.setText("" + mins + ":" + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }
    };

}