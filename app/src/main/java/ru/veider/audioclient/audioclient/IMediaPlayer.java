package ru.veider.audioclient.audioclient;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.ImageButton;

public interface IMediaPlayer {
    void playPause(final MediaPlayer mediaPlayer, ImageButton mPlayButton);
    void saveCurrentPlaying(String name, int position);
    String createTimeLabel(int time);
    void showToast(String message);

    void bookCover();
}
