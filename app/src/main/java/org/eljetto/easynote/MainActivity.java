package org.eljetto.easynote;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.eljetto.easynote.metronome.Metronome;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ImageView mImageView;
    ImageButton mButton;
    boolean isActive;
    private long startTime = 0L;
    private Handler timerHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    private TextView timerValue;
    private Metronome metronome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.image);
        mButton = (ImageButton) findViewById(R.id.start);
        timerValue = (TextView) findViewById(R.id.timer);

        try {
            metronome = new Metronome(this);
            metronome.setBeatsInBar(4);
            metronome.setBpm(120);
            metronome.setPlay(false);
        } catch (IOException e) {
            //TODO pop up handler
        }

        mButton.setOnClickListener(new View.OnClickListener() {
            ChangeImageTask changeImageTask;
            @Override
            public void onClick(View v) {
                isActive = !isActive;
                if (isActive) {
                    metronome.setPlay(true);
                    changeImageTask = new ChangeImageTask();
                    changeImageTask.execute();
                    mButton.setImageResource(R.drawable.pause);
                    startTime = SystemClock.uptimeMillis();
                    timerHandler.postDelayed(updateTimerThread, 0);
                } else {
                    metronome.setPlay(false);
                    changeImageTask.cancel(true);
                    mButton.setImageResource(R.drawable.play);
                    timeInMilliseconds = 0;
                    timerHandler.removeCallbacks(updateTimerThread);
                }
            }
        });
        super.onCreate(savedInstanceState);
    }

    private class ChangeImageTask extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... params) {
            metronome.play();//TODO fix issue
            while (isActive) {
                for (int i = 1; i < 6; i++) {
                    try {
                        publishProgress(i);
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                        timeInMilliseconds = 0;
                        timerHandler.removeCallbacks(updateTimerThread);
                    }
                }
            }
            return "Stopped";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mImageView.setImageResource(getResources().getIdentifier("note" + values[0], "drawable", getPackageName()));
        }

        @Override
        protected void onCancelled() {
            timerValue.setText("" + 0 + ":" + String.format("%02d", 0));
            super.onCancelled();
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
            timerHandler.postDelayed(this, 0);
        }
    };
}