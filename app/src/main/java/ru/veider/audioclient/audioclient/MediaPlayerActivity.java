package ru.veider.audioclient.audioclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.veider.audioclient.audioclient.data.Api;
import ru.veider.audioclient.audioclient.data.Film;
import ru.veider.audioclient.audioclient.data.SearchResponse;
import ru.veider.audioclient.audioclient.recycler.MediaModel;
import ru.veider.audioclient.audioclient.storage.AudioLibrary;

public class MediaPlayerActivity extends AppCompatActivity {
    //TODO shared preference, запоминание позиции

    private static final String EXTRA_URL = "url";
    private static final String EXTRA_NAME = "name";
    private static final String EXTRA_POS = "position";
    private static final String APP_PREFERENCES_NAME = "book";

    private SeekBar timeSeekBar;
    private TextView mCurrentTime, lastTime, bookName;
    private Timer timer;
    private SharedPreferences sharedPreferences;
    private MediaPlayer mediaPlayer;
    private MediaModel mediaModel;
    private ImageButton mPlayButton, mNextButton, mBackButton;
    private ImageView coverView;
    private Thread updateSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        Intent intent = getIntent();
//        String url = intent.getStringExtra(EXTRA_URL);
//        String name = intent.getStringExtra(EXTRA_NAME);
        int position = intent.getIntExtra(EXTRA_POS, 0);

        mediaModel = AudioLibrary.repository.get(position);
        mediaPlayer = MediaPlayer.create(this, Uri.parse(mediaModel.getUrl()));

        coverView = findViewById(R.id.book);
        mCurrentTime = findViewById(R.id.current_time);
        mPlayButton = findViewById(R.id.playButton);
        mNextButton = findViewById(R.id.nextButton);
        mBackButton = findViewById(R.id.backButton);
        lastTime = findViewById(R.id.full_time);
        bookName = findViewById(R.id.name);
        timeSeekBar = findViewById(R.id.seekBar);

//        updateSeekBar = new Thread(){
//            @Override
//            public void run() {
//                int totalDuration = mediaPlayer.getDuration();
//                int currentPosition = 0;
//                timeSeekBar.setMax(totalDuration);
//                while (currentPosition < totalDuration){
//                    try {
//                        sleep(500);
//                        currentPosition = mediaPlayer.getCurrentPosition();
//                        timeSeekBar.setProgress(currentPosition);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                //super.run();
//
//            }
//        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        int totalTime = mediaPlayer.getDuration();

        bookName.setText(mediaModel.getName());

        sharedPreferences = getSharedPreferences(APP_PREFERENCES_NAME, MODE_PRIVATE);
        int curPos = sharedPreferences.getInt(EXTRA_POS, 0);
        if (!mediaModel.getName().equals(sharedPreferences.getString("name", null))) {
            curPos = 0;
        }

        mediaPlayer.seekTo(curPos);

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPause(mediaPlayer, mPlayButton);
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 30000);
            }
        });

        timeSeekBar.setMax(totalTime);

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

        playPause(mediaPlayer, mPlayButton);

        final Api api = new NetworkModule().api();

        //вытягивание обложки из Google Play books
        api.searchFilm(mediaModel.getName()).enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<SearchResponse> call, @NonNull Response<SearchResponse> response) {
                if (response.isSuccessful()) {
                    final SearchResponse body = response.body();

                    if (body.items == null || body.items.isEmpty()) {
                        showToast("Can't find image");
                        return;
                    }

                    final String id = body.items.get(0).id;

                    api.getFilm(id).enqueue(new Callback<Film>() {
                        @Override
                        public void onResponse(@NonNull Call<Film> call, @NonNull Response<Film> response) {
                            if (response.isSuccessful()) {
                                final String imageUrl = response.body().volumeInfo.imageLinks.medium;
                                if (imageUrl == null) {
                                    showToast("Empty imageUrl");
                                    return;
                                }
                                Picasso.get()
                                        .load(imageUrl)
                                        .placeholder(R.drawable.ic_photo_size_select_actual_black_24dp)
                                        .into(coverView);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Film> call, @NonNull Throwable t) {
                            showToast("onFailure");
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchResponse> call, @NonNull Throwable t) {
                showToast("onFailure");
            }
        });


    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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

            saveCurrentPlaying(mediaModel.getName(), mediaPlayer.getCurrentPosition());

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
                            mCurrentTime.setText(elapsedTime);
                            String fullTime = createTimeLabel(mediaPlayer.getDuration());
                            lastTime.setText(fullTime);
                        }
                    });
                }
            }, 0, 1000);
        }
    }

    private void saveCurrentPlaying(String name, int position) {
        sharedPreferences.edit().putString("name", name).putInt("position", position).apply();
    }

    public static void start(Context context, int position) {
        context.startActivity(
                new Intent(context, MediaPlayerActivity.class).putExtra(EXTRA_POS, position));
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveCurrentPlaying(mediaModel.getName(), mediaPlayer.getCurrentPosition());
        playPause(mediaPlayer, mPlayButton);
        mediaPlayer.release();
        mediaPlayer = null;
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        saveCurrentPlaying(mediaModel.getName(), mediaPlayer.getCurrentPosition());
//        playPause(mediaPlayer, mPlayButton);
//        mediaPlayer.release();
//        mediaPlayer = null;
//    }
}