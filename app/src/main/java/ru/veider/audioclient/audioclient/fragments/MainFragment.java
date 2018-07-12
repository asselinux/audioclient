package ru.veider.audioclient.audioclient.fragments;

import android.Manifest;
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
import ru.veider.audioclient.audioclient.MediaPlayerActivity;
import ru.veider.audioclient.audioclient.R;
import ru.veider.audioclient.audioclient.recycler.MediaModel;
import ru.veider.audioclient.audioclient.recycler.MediaModelAdapter;


@RuntimePermissions
public class MainFragment extends Fragment {

    private FloatingActionButton fabStorage;
    private RecyclerView recyclerView;
    private MediaModelAdapter adapter;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //id
        fabStorage = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.recyclerView);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // NPE
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        adapter = new MediaModelAdapter(new MediaModelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull MediaModel mediaModel, int position) {
                MediaPlayerActivity.start(getActivity(), position);
            }
        });
        recyclerView.setAdapter(adapter);

        fabStorageClick();


    }


    public ArrayList<File> findAudioBooks(File root) {
        ArrayList<File> al = new ArrayList<>();
        File[] files = root.listFiles();
        Log.w("TAG", root.getAbsolutePath());
        if (files == null){
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
                MainFragmentPermissionsDispatcher.loadFromExternalWithPermissionCheck(MainFragment.this);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
        MainFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
//
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
        new AlertDialog.Builder(getActivity())
                .setMessage("In order to proceed you need to provide storage permission")
//                .setPositiveButton(R.string.button_allow, (dialog, button) -> request.proceed())
//                .setNegativeButton(R.string.button_deny, (dialog, button) -> request.cancel())
                .show();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Intent intent = new Intent(getActivity(), SettingsActivity.class);
//            startActivity(intent);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    // TODO: Rename method, update argument and hook method into UI event

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
}