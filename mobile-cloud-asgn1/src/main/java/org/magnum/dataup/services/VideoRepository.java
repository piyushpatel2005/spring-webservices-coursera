package org.magnum.dataup.services;

import org.magnum.dataup.model.Video;

import java.util.Collection;

public interface VideoRepository {
    public Video addVideo(Video v);

    public Collection<Video> getVideos();

    public Video findVideoById(long id);
}
