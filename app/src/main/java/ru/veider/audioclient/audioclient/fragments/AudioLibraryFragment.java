package ru.veider.audioclient.audioclient.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import ru.veider.audioclient.audioclient.R;
import ru.veider.audioclient.audioclient.fragments.dummy.MediaModel;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link AudioLibraryListener}
 * interface.
 */
@RuntimePermissions
public class AudioLibraryFragment extends Fragment {

//    private static MediaModel model;
    private FloatingActionButton fabStorage;
    private RecyclerView recyclerView;
    private AudioLibraryRecyclerViewAdapter adapter;

    AudioLibraryListener libraryListener;

    public AudioLibraryFragment() {
    }

    // TODO: Customize parameter initialization
    public static AudioLibraryFragment newInstance() {
        return new AudioLibraryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audiolibrary_list, container, false);

        fabStorage = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.recyclerView);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));


        adapter = new AudioLibraryRecyclerViewAdapter(new AudioLibraryRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull MediaModel mediaModel, int position) {
                Toast.makeText(getContext(), "toast", Toast.LENGTH_SHORT).show();
//                MediaPlayerFragment.newInstance(mediaModel, position);
                ((AudioLibraryListener)getActivity()).onAudioLibraryListener(mediaModel, position);
//                Intent intent = new Intent(getActivity(), MediaPlayerFragment.class);
//                intent.putExtra("MediaModel", 2);
//                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        fabStorageClick();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AudioLibraryFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public ArrayList<File> findAudioBooks(File root) {
        ArrayList<File> al = new ArrayList<>();
        File[] files = root.listFiles();
        Log.w("TAG", root.getAbsolutePath());
        if (files == null) {
            Toast.makeText(getActivity(), "Files not found", Toast.LENGTH_SHORT).show();
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
                AudioLibraryFragmentPermissionsDispatcher.loadFromExternalWithPermissionCheck(AudioLibraryFragment.this);
            }
        });
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void loadFromExternal() { // reading a file
        ArrayList<File> myBooks = findAudioBooks(Environment.getExternalStoragePublicDirectory("Download"));
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
        new AlertDialog.Builder(getContext())
                .setMessage("In order to proceed you need to provide storage permission")
//                .setPositiveButton(R.string.button_allow, (dialog, button) -> request.proceed())
//                .setNegativeButton(R.string.button_deny, (dialog, button) -> request.cancel())
                .show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AudioLibraryListener) {
            libraryListener = (AudioLibraryListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AudioLibraryListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        libraryListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface AudioLibraryListener {
        void onAudioLibraryListener(MediaModel mediaModel, int position);
    }
}