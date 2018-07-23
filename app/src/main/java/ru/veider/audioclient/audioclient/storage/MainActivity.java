package ru.veider.audioclient.audioclient.storage;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import ru.veider.audioclient.audioclient.R;
import ru.veider.audioclient.audioclient.fragments.AudioLibraryFragment;
import ru.veider.audioclient.audioclient.fragments.MainFragment;
import ru.veider.audioclient.audioclient.fragments.MediaPlayerFragment;
import ru.veider.audioclient.audioclient.fragments.dummy.MediaModel;

public class MainActivity extends AppCompatActivity implements AudioLibraryFragment.AudioLibraryListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null){
            openFragment(MainFragment.newInstance(), false);
        }
    }

    private void openFragment(Fragment fragment, boolean addToBackStack){
        final FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        if(addToBackStack){
            transaction
                    .addToBackStack(null)
                    .setCustomAnimations(
                            R.anim.slide_in_right, R.anim.slide_out_left,
                            R.anim.slide_in_left, R.anim.slide_out_right
                    );
        }

        transaction
                .add(R.id.content, fragment)
                .commit();
    }

    @Override
    public void onAudioLibraryListener(MediaModel mediaModel, int position) {
        openFragment(MediaPlayerFragment.newInstance(), true);
    }
}