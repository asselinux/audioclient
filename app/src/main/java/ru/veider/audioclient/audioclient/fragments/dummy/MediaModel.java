package ru.veider.audioclient.audioclient.fragments.dummy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO не может найти медиамодель
public class MediaModel implements Serializable{
    private final String name;
    private final String image;
    private final String url;

    public MediaModel(String name, String image, String url) {
        this.name = name;
        this.image = image;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }
}