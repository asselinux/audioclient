package ru.veider.audioclient.audioclient.storage;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import ru.veider.audioclient.audioclient.MediaPlayerActivity;
import ru.veider.audioclient.audioclient.R;
import ru.veider.audioclient.audioclient.SettingsActivity;
import ru.veider.audioclient.audioclient.recycler.MediaModel;
import ru.veider.audioclient.audioclient.recycler.MediaModelAdapter;

@RuntimePermissions
public class AudioLibrary extends AppCompatActivity {
    private FloatingActionButton fabStorage;

    private RecyclerView recyclerView;
    private MediaModelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_library);
        fabStorage = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        //старт плеера
        //заменить на старт фрагмента
        adapter = new MediaModelAdapter(new MediaModelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull MediaModel mediaModel, int position) {
                MediaPlayerActivity.start(AudioLibrary.this, position);
            }
        });
        recyclerView.setAdapter(adapter);

        fabStorageClick();

        AudioLibraryPermissionsDispatcher.loadFromExternalWithPermissionCheck(AudioLibrary.this);
    }

    public ArrayList<File> findAudioBooks(File root) {
        ArrayList<File> al = new ArrayList<>();
        File[] files = root.listFiles();
        Log.w("TAG", root.getAbsolutePath());
        if (files == null){
            Toast.makeText(this, "Files not found", Toast.LENGTH_SHORT).show();
        } else
        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                al.addAll(findAudioBooks(singleFile));
            } else {
                if (singleFile.getName().endsWith(".mp3")) {
                    al.add(singleFile);
                }
            }
        }
        return al;
    }

    private void fabStorageClick() {
        fabStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioLibraryPermissionsDispatcher.loadFromExternalWithPermissionCheck(AudioLibrary.this);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method


        AudioLibraryPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void loadFromExternal() { // reading a file
        ArrayList<File> myBooks = findAudioBooks(Environment.getExternalStoragePublicDirectory("AudioBooks"));
        List<MediaModel> list = new ArrayList<>();
        for (int i = 0; i < myBooks.size(); i++) {
            File file = myBooks.get(i);
            String name = file.getName().replace(".mp3", "").replace(".wav", "");
            list.add(new MediaModel(name, null, file.getAbsolutePath()));
        }
        repository = list;
        adapter.replaceAll(list);

    }

    public static List<MediaModel> repository;

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showRationaleForStorage(final PermissionRequest request) {
        showDialog();
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showDeniedLocation() {
        showDialog();
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showNeverAskForLocation() {
        showDialog();
    }

    private void showDialog() {
        new AlertDialog.Builder(this)
                .setMessage("In order to proceed you need to provide storage permission")
//                .setPositiveButton(R.string.button_allow, (dialog, button) -> request.proceed())
//                .setNegativeButton(R.string.button_deny, (dialog, button) -> request.cancel())
                .show();
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

    private void openMediaPlayerFragment(Fragment fragment, boolean addToBackStack){
        final FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        if (addToBackStack) {
            transaction
                    .addToBackStack(null)
                    .setCustomAnimations(
                            R.anim.slide_in_right, R.anim.slide_out_left,
                            R.anim.slide_in_left, R.anim.slide_out_right
                    );
        }

        //тут должен быть id FrameLayout из xml файла activity_main, который соединен с активити
        transaction
                .replace(R.id.content, fragment)//
                .commit();

    }
}