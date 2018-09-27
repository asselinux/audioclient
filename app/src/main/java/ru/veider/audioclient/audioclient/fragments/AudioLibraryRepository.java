package ru.veider.audioclient.audioclient.fragments;

import android.annotation.SuppressLint;

import java.util.HashMap;
import java.util.Map;

import ru.veider.audioclient.audioclient.fragments.dummy.MediaModel;

//not used
public class AudioLibraryRepository {
    private static final AudioLibraryRepository INSTANCE = new AudioLibraryRepository();

    public static AudioLibraryRepository getINSTANCE() {
        return INSTANCE;
    }

    private Map<Integer, MediaModel> storage;

    @SuppressLint("UseSparseArrays")
    private AudioLibraryRepository(){
        this.storage = new HashMap<>();
    }

    public MediaModel getById(int id){
        return storage.get(id);
    }

    public void save(MediaModel model){
//        storage.put()
    }
}