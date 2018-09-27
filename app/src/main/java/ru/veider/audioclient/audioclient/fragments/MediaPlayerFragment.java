package ru.veider.audioclient.audioclient.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.veider.audioclient.audioclient.NetworkModule;
import ru.veider.audioclient.audioclient.R;
import ru.veider.audioclient.audioclient.data.Api;
import ru.veider.audioclient.audioclient.data.Film;
import ru.veider.audioclient.audioclient.data.SearchResponse;
import ru.veider.audioclient.audioclient.fragments.dummy.MediaModel;

import static android.content.Context.MODE_PRIVATE;

public class MediaPlayerFragment extends Fragment {

    private static final String EXTRA_POS = "position";
    private static final String APP_PREFERENCES_NAME = "book";
    private static final String POSITION = "args:position";


    private SeekBar mPositionBar;
    private TextView mCurrentTime, mFullTime, bookName;
    private SharedPreferences sharedPreferences;
    private MediaPlayer mediaPlayer;
    private MediaModel mediaModel;
    private ImageButton mPlayButton, mNextButton, mBackButton;
    private ImageView mCoverView;
    int intFullTime;


    public MediaPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int position = getArguments().getInt(POSITION);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_media_player, container, false);
        mCoverView = view.findViewById(R.id.book);
        mCurrentTime = view.findViewById(R.id.current_time);
        mPlayButton = view.findViewById(R.id.playButton);
        mNextButton = view.findViewById(R.id.nextButton);
        mBackButton = view.findViewById(R.id.backButton);
        mFullTime = view.findViewById(R.id.full_time);
        bookName = view.findViewById(R.id.name);
        mPositionBar = view.findViewById(R.id.seekBar);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        int position = intent.getIntExtra(EXTRA_POS, 0);

        //TODO NPE, самое главное
        mediaPlayer = MediaPlayer.create(getContext(), Uri.parse(mediaModel.getUrl()));// npe

        int totalTime = mediaPlayer.getDuration();
        mPositionBar.setMax(totalTime);

        //Запоминание позиции книги
        sharedPreferences = getContext().getSharedPreferences(APP_PREFERENCES_NAME, MODE_PRIVATE);
        int curPos = sharedPreferences.getInt(EXTRA_POS, 0);
        if (!mediaModel.getName().equals(sharedPreferences.getString("name", null))) {
            curPos = 0;
        }

        mediaPlayer.seekTo(curPos);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bookName.setText(mediaModel.getName());

        //Слушатели кнопок
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPause(mediaPlayer, mPlayButton);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 30000);
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
            }
        });

        mPositionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                mediaPlayer.seekTo(mPositionBar.getProgress());
            }
        });

        //Thread(Update
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null){
                    try {
                        Message message = new Message();
                        message.what = mediaPlayer.getCurrentPosition();//временами баг
                        handler.sendMessage(message);

                        Thread.sleep(1000);
                    } catch (InterruptedException e){
                        showToast("InterruptedException" + e);
                    }
                }
            }
        }).start();

        if (mediaPlayer != null) {
            playPause(mediaPlayer, mPlayButton);
        }

        bookCover();

    }

    //утечка памяти, как исправить - не знаю
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            //Update mPositionBar
            mPositionBar.setProgress(currentPosition);

            //Update Labels
            String elapsedTime = createTimeLabel(currentPosition);
            mCurrentTime.setText(elapsedTime);

            intFullTime = mediaPlayer.getDuration(); //
            String fullTime = createTimeLabel(intFullTime);
            mFullTime.setText(fullTime);
        }
    };

    //вытягивание обложки из Google Play books
    private void bookCover(){
        final Api api = new NetworkModule().api();
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
                                        .into(mCoverView);
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

    //Вывод тостов
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    //Время
    private String createTimeLabel(int time) {
        String timeLabel;
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) {
            timeLabel += "0";
        }
        timeLabel += sec;
        return timeLabel;
    }

    //Основной метод плеера, тут находится основная логика плеера
    private void playPause(final MediaPlayer mediaPlayer, ImageButton mPlayButton) {
        //if need to pause
        if (mediaPlayer.isPlaying()) {
            //pause
            mPlayButton.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);

            saveCurrentPlaying(mediaModel.getName(), mediaPlayer.getCurrentPosition());

            mediaPlayer.pause();
        } else  {
            //play
            mPlayButton.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
            mediaPlayer.start();
        }
    }

    //сохранение текущей позиции
    private void saveCurrentPlaying(String name, int position) {
        sharedPreferences.edit().putString("name", name).putInt("position", position).apply();
    }

    public static void start(Context context, int position) { //ActivityNotFoundException
        context.startActivity(
                new Intent(context, MainFragment.class).putExtra(EXTRA_POS, position));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveCurrentPlaying(mediaModel.getName(), mediaPlayer.getCurrentPosition());
        playPause(mediaPlayer, mPlayButton);
        mediaPlayer.release();
        mediaPlayer = null;
//        handler.removeCallbacks();

    }

    public static MediaPlayerFragment newInstance(MediaModel mediaModel, int position){
        final MediaPlayerFragment fragment = new MediaPlayerFragment();
        Bundle args = new Bundle();
        args.putSerializable("123", mediaModel);
        args.putInt("124", position);
        fragment.setArguments(args);
        return fragment;
    }
}