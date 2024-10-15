package com.ePadhai.controller.Course;

import com.ePadhai.model.course.Video;
import com.ePadhai.payload.CustomMessage;
import com.ePadhai.service.course.VideoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/videos")
@CrossOrigin("*")
public class VideoController {

    private Logger logger = LoggerFactory.getLogger(VideoController.class);
    private final VideoService videoService;
    

    public VideoController(VideoService videoService) {
		super();
		this.videoService = videoService;
	}


	@PostMapping
    public ResponseEntity<?> saveVideo(
                        @RequestParam("file") MultipartFile file,
                        @RequestParam("title") String title,
                        @RequestParam("description") String description
                       ){

        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);

        Video savedVideo = videoService.saveVideo(video, file);
//        Video savedVideo = video;
        logger.info("savedVideo: {}", savedVideo);

        if(savedVideo != null){
            return ResponseEntity.created(null).body(savedVideo);
        }


        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                                    new CustomMessage("Could Not Create!",
                                    		false,
                                    		HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    );

    }

}
