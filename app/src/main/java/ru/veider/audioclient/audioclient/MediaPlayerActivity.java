package ru.veider.audioclient.audioclient;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MediaPlayerActivity extends AppCompatActivity {

    private static final String EXTRA_URL = "url";

    private SeekBar timeSeekBar;
    private TextView numberForTrack, lastTime;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.name);

        Intent intent = getIntent();
        String url = intent.getStringExtra(EXTRA_URL);

        final MediaPlayer mediaPlayer = MediaPlayer.create(this, Uri.parse(url));
        mediaPlayer.setLooping(true);

        numberForTrack = findViewById(R.id.current_time);
        final ImageButton mPlayButton = findViewById(R.id.playButton);
        ImageButton mNextButton = findViewById(R.id.nextButton);
        ImageButton mBackButton = findViewById(R.id.backButton);
        lastTime = findViewById(R.id.full_time);

        int totalTime = mediaPlayer.getDuration();

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPause(mediaPlayer, mPlayButton);
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

        timeSeekBar = findViewById(R.id.seekBar);
        timeSeekBar.setMax(totalTime);

        //SeekBar
        timeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        mPlayButton.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
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

    private String createTimeLabel(int time) {
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

    private void playPause(final MediaPlayer mediaPlayer, ImageButton mPlayButton) {
        //if need to pause
        if (mediaPlayer.isPlaying() && timer != null) {
            //pause
            mPlayButton.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
            timer.cancel();
            timer = null;
            mediaPlayer.pause();
        } else if (timer == null) {
            //play
            mPlayButton.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
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
        }
    }

    public static void start(Context context, String url) {
        context.startActivity(new Intent(context, MediaPlayerActivity.class).putExtra(EXTRA_URL, url));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}