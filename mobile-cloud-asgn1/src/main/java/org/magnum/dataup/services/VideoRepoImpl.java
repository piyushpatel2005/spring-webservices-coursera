package org.magnum.dataup.services;

import org.magnum.dataup.model.Video;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class VideoRepoImpl implements VideoRepository {

    private List<Video> videoList = new CopyOnWriteArrayList<>();

    @Override
    public Video addVideo(Video v) {
//        long id = videoList.size() + 1;
//        v.setId(id);
//        v.setDataUrl(getDataUrl(id));
        videoList.add(v);
        v.setId(videoList.size());
        return v;
    }

    @Override
    public Collection<Video> getVideos() {
        return videoList;
    }

    @Override
    public Video findVideoById(long id) {
//        return videoList.stream().filter(video -> video.getId() == id).collect(Collectors.toList()).get(0);
        --id;
        if (id >= 0 && id < videoList.size())
            return videoList.get((int) id);
        else
            return null;
    }


}
