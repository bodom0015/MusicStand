package com.musicstand.lambert.musicstand;

import java.io.Serializable;

/**
 * Created by Mike on 7/3/2016.
 */
public class Track implements Serializable {
    /* Private members */
    private String artist;
    private String album;
    private String title;

    /* Public constructor(s) */
    public Track(String title) {
        this("", "", title);
    }

    public Track(String artist, String title) {
        this("", artist, title);
    }

    public Track(String album, String artist, String title) {
        this.setArtist(artist);
        this.setAlbum(album);
        this.setTitle(title);
    }

    public void merge(Track savedTrack) {
        this.setTitle(savedTrack.getTitle());
        this.setArtist(savedTrack.getArtist());
        this.setAlbum(savedTrack.getAlbum());
    }

    /* Getters and Setters */
    public String getArtist() {
        return this.artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    @Override
    public String toString() {
        if ("".equals(this.getAlbum())) {
            if ("".equals(this.getArtist())) {
                return this.getTitle();
            } else {
                return this.getArtist() + ": " + this.getTitle();
            }
        } else if ("".equals(this.getArtist())) {
            return this.getTitle() + " (" + this.getAlbum() + ")";
        } else {
            return this.getArtist() + ": " + this.getTitle() + " (" + this.getAlbum() + ")";
        }
    }

    @Override
    public int hashCode() {
        int result = 15;

        result += (37 * result) + this.getTitle().hashCode();
        result += (37 * result) + this.getArtist().hashCode();
        result += (37 * result) + this.getAlbum().hashCode();

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Track) {
            Track that = (Track) o;
            if (!this.getTitle().equals(that.getTitle())) {
                return false;
            } else if (!this.getArtist().equals(that.getArtist())) {
                return false;
            } else if (!this.getAlbum().equals(that.getAlbum())) {
                return false;
            }
            return true;
        }

        return false;
    }
}
