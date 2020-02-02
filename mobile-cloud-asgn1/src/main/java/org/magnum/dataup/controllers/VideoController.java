package org.magnum.dataup.controllers;

import org.magnum.dataup.VideoFileManager;
import org.magnum.dataup.VideoSvcApi;
import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.magnum.dataup.services.VideoRepoImpl;
import org.magnum.dataup.services.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class VideoController {

    @Autowired
    private VideoRepository videoRepo = new VideoRepoImpl();
    private VideoFileManager videoFileManager;
    private final AtomicLong ATOMIC_LONG = new AtomicLong(0L);

    @RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH, method = RequestMethod.GET)
    public Collection<Video> getVideoList() {
        return videoRepo.getVideos();
    }

    @RequestMapping(value = VideoSvcApi.VIDEO_SVC_PATH, method = RequestMethod.POST)
    public @ResponseBody
    Video addVideo(@RequestBody Video v) {
//        v.setId(ATOMIC_LONG.incrementAndGet());

        videoRepo.addVideo(v);
        v.setDataUrl(getDataUrl(v.getId()));
        return v;
    }

    @RequestMapping(value = VideoSvcApi.VIDEO_DATA_PATH, method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<VideoStatus> setVideoData(@PathVariable(VideoSvcApi.ID_PARAMETER) long id, @RequestParam(VideoSvcApi.DATA_PARAMETER) MultipartFile videoData) {

        VideoStatus status = new VideoStatus(VideoStatus.VideoState.READY);
        try {
            videoFileManager = VideoFileManager.get();
            Video video = null;
            System.out.println("running findvideo");
            video = videoRepo.findVideoById(id);
            System.out.println(video);
            if (video == null)
                return new ResponseEntity<VideoStatus>(status, HttpStatus.NOT_FOUND);
            videoFileManager.saveVideoData(video, videoData.getInputStream());
        } catch (IOException io) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<VideoStatus>(status, HttpStatus.OK);
    }

    @RequestMapping(value = VideoSvcApi.VIDEO_DATA_PATH, method = RequestMethod.GET)
    public @ResponseBody
    void getData(@PathVariable(VideoSvcApi.ID_PARAMETER) long id,
                 HttpServletResponse hsr) throws IOException {
        int x = 1;
        System.out.println(x);
        // TODO Auto-generated method stub
        try {
            videoFileManager = VideoFileManager.get();
            Video video = videoRepo.findVideoById(id);
            if (video != null) {
                videoFileManager.copyVideoData(video, hsr.getOutputStream());
            } else {
                hsr.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            hsr.setStatus(HttpServletResponse.SC_NOT_FOUND);
            e.printStackTrace();
        } catch (Exception e) {
            hsr.setStatus(HttpServletResponse.SC_NOT_FOUND);
            e.printStackTrace();
        }
    }

    private String getDataUrl(long videoId) {
        String url = getUrlBaseForLocalServer() + "/video/" + videoId + "/data";
        return url;
    }

    private String getUrlBaseForLocalServer() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String base = "http://" + request.getServerName() + ((request.getServerPort() != 80) ? ":" + request.getServerPort() : "");
        return base;
    }
}
