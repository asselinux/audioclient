package ru.veider.audioclient.audioclient.recycler;

public class MediaModel {

    private final String name;
    private final String image;
    private final String url;

    public MediaModel(String name, String image, String url) {
        this.name= name;
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
