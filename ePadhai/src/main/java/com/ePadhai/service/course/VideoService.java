package com.ePadhai.service.course;

import com.ePadhai.model.course.Video;
import com.ePadhai.repository.course.VideoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoService {
	
	private Logger logger = LoggerFactory.getLogger(VideoService.class);
	
	@Value("${files.video}")
	private String DIR;
	
    private final VideoRepository videoRepository;
    


    public VideoService(VideoRepository videoRepository) {
		super();
		this.videoRepository = videoRepository;
	}

	

    @PostConstruct
    public void init(){
        File file = new File(DIR);

        if(!file.exists()){
            file.mkdir();
            logger.info("video storage folder Created!!");
        }else{
            logger.info("video storage folder already Created!!");
        }

    }

    public Video saveVideo(Video video, MultipartFile file){

        try {


            //original file name (e.g., abc.mp4)
            String filename = file.getOriginalFilename();
            String contentType = file.getContentType();
            InputStream inputStream = file.getInputStream();

            //folder path: create
            String cleanedFolderName = StringUtils.cleanPath(DIR);
            String cleanedFileName = StringUtils.cleanPath(filename);



            //folder path with file name
            Path path = Paths.get(cleanedFolderName, cleanedFileName);
            logger.info("The Path is: {}", path);
            logger.info("The path.toString() is: {}", path.toString());
            logger.info("The path.toAbsolutePath() is: {}", path.toAbsolutePath());

            //copy file to the folder
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);

            //video meta data
            video.setContenttype(contentType);
            video.setFilePath(path.toString());

            logger.info("file path: {}", video.getFilePath());

            //meta data save
            return videoRepository.save(video);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Video getVideoByTitle(String title){
       return videoRepository.findByTitle(title).orElseThrow();
    }

    public Video getVideoById(Long id){
        return videoRepository.findById(id).orElseThrow(()-> new RuntimeException("Video Not Found!!!"));
    }

    public List<Video> getAllVideos(){
        return videoRepository.findAll();
    }
}
