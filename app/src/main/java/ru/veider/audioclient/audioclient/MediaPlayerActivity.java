package ru.veider.audioclient.audioclient;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MediaPlayerActivity extends AppCompatActivity {

    private SeekBar timeSeekBar;
    private TextView numberForTrack, lastTime;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setLooping(true);

        numberForTrack = findViewById(R.id.allDuration);
        final Button mPlayButton = findViewById(R.id.playButton);
        Button mStopButton = findViewById(R.id.stopButton);
        Button mNextButton = findViewById(R.id.nextBook);
        Button mBackButton = findViewById(R.id.backBook);
        lastTime = findViewById(R.id.lastTime);

        int totalTime = mediaPlayer.getDuration();

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPause(mediaPlayer, mPlayButton);
                Toast.makeText(getApplicationContext(), "Play", Toast.LENGTH_SHORT).show();
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Перемотка песен назад
            }
        });

        mBackButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000);
                return false;
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Перемотка песен вперёд
            }
        });

        mNextButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
                return false;
            }
        });

        timeSeekBar = findViewById(R.id.seekBarForBook);
        timeSeekBar.setMax(totalTime);

        //SeekBar
        timeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
//                    timeSeekBar.setProgress(progress);
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(timeSeekBar.getProgress());
            }
        });
    }

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) {
            timeLabel += "0";
        }
        timeLabel += sec;
        return timeLabel;
    }

    private void playPause(final MediaPlayer mediaPlayer, Button mPlayButton) {

        if (mediaPlayer.isPlaying() && timer != null) {
            mPlayButton.setText(">");
            timer.cancel();
            timer = null;
            mediaPlayer.pause();
        } else if (timer == null) {
            mediaPlayer.start();
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {//
                @Override
                public void run() {
                    timeSeekBar.setProgress(mediaPlayer.getCurrentPosition());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String elapsedTime = createTimeLabel(mediaPlayer.getCurrentPosition());
                            numberForTrack.setText(elapsedTime);
                            String fullTime = createTimeLabel(mediaPlayer.getDuration());
                            lastTime.setText(fullTime);
                        }
                    });
                }
            }, 0, 1000);
            mPlayButton.setText("||");
        }
    }
}