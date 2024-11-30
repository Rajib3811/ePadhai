package com.ePadhai.controller.Course;

import com.ePadhai.constants.AppConstants;
import com.ePadhai.model.course.Video;
import com.ePadhai.payload.CustomMessage;
import com.ePadhai.service.course.VideoService;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    //upload video
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
	
	@GetMapping("/stream/{id}")
	public ResponseEntity<Resource>  streamVideo(
			@PathVariable Long id
			){
		logger.info("Searching videos with id:{} ... ", id);
		Video video = videoService.getVideoById(id);
		
		String contentType = video.getContenttype();
		String filePath = video.getFilePath();
		
		if(contentType == null) {
			contentType = "application/octet-stream";
		}
		logger.info("filePath: {}", filePath);
		Resource resource = new FileSystemResource(filePath);
		
		return ResponseEntity
				.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.body(resource);
		
	}
	
	//stream video in chunks
	
//	@GetMapping("/stream/range/{id}")
//	public ResponseEntity<Resource>  streamVideoRange(
//			@PathVariable Long id,
//			@RequestHeader(value = "Range", required = false) String range
//			){
//		logger.info("Searching videos with id:{} ... ", id);
//		logger.info("range: {}", range);
//		
//		Video video = videoService.getVideoById(id);
//		String contentType = video.getContenttype();
//		String filePath = video.getFilePath();
//		
//		Path path = Paths.get(filePath);
//		
//		
//		if(contentType == null) {
//			contentType = "application/octet-stream";
//		}
//		
//		//file length
//		long fileLength = path.toFile().length();
//		
//		if(range == null) {
//			Resource resource = new FileSystemResource(path);
//			return ResponseEntity
//					.ok()
//					.contentType(MediaType.parseMediaType(contentType))
//					.body(resource);
//		}
//		
//		
//		
//		String[] ranges = range.replace("bytes=", "").split("-");
//		
//		
//		long rangeStart = Long.parseLong(ranges[0]);
//		long rangeEnd;
//		
//		if(ranges.length>1) {
//			rangeEnd = Long.parseLong(ranges[1]);
//			
//		}else {
//			rangeEnd = fileLength-1;
//		}
//		
//		if(rangeEnd > fileLength-1) {
//			rangeEnd = fileLength-1;
//		}
//		
//		logger.info("start Range: {}, and End Range: {}", rangeStart, rangeEnd); 
//		
//		InputStream inputStream;
//		
//		try {
//			
//			inputStream = Files.newInputStream(path);
//			inputStream.skip(rangeStart);
//			
//		}catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//		}
//		
//		long contentLength = (rangeEnd - rangeStart)+1;
//		 
//		HttpHeaders httpHeaders = new HttpHeaders();
//		httpHeaders.add("Content-Range", "bytes "+ rangeStart + "-" + rangeEnd + "/" + fileLength);
//		httpHeaders.add("Cache-Control", "no-cahce, no-store, must-revalidate");
//		httpHeaders.add("Pragma", "no-cahce");
//		httpHeaders.add("Expires", "0");
//		httpHeaders.add("X-Content-Type-Options", "nosniff");
//		httpHeaders.setContentLength(contentLength);
//		
//		
//		return ResponseEntity
//				.status(HttpStatus.PARTIAL_CONTENT)
//				.headers(httpHeaders)
//				.contentType(MediaType.parseMediaType(contentType))
//				.body(new InputStreamResource(inputStream));
//		
//	}
	
	
	@GetMapping("/stream/range/{id}")
	public ResponseEntity<Resource>  streamVideoRange(
			@PathVariable Long id,
			@RequestHeader(value = "Range", required = false) String range
			){
		logger.info("Searching videos with id:{} ... ", id);
		logger.info("range: {}", range);
		
		Video video = videoService.getVideoById(id);
		String contentType = video.getContenttype();
		String filePath = video.getFilePath();
		
		Path path = Paths.get(filePath); 
		
		
		if(contentType == null) {
			contentType = "application/octet-stream";
		}
		
		//file length
		long fileLength = path.toFile().length();
		
		if(range == null) {
			Resource resource = new FileSystemResource(path);
			return ResponseEntity
					.ok()
					.contentType(MediaType.parseMediaType(contentType))
					.body(resource);
		}
		
		
		
		String[] ranges = range.replace("bytes=", "").split("-");
		
		
		long rangeStart = Long.parseLong(ranges[0]);
		long rangeEnd = (rangeStart + AppConstants.CHUNK_SIZE) - 1;
		
		if(rangeEnd >= fileLength) {
			rangeEnd = fileLength-1;
		}
		
		logger.info("start Range: {}, and End Range: {}", rangeStart, rangeEnd); 
		
		
		
		try(InputStream inputStream  = Files.newInputStream(path)) {
			
			inputStream.skip(rangeStart);
			
			long contentLength = (rangeEnd - rangeStart)+1;
			
			byte[] data = new byte[(int)contentLength];
			int read = inputStream.read(data, 0, data.length);
			logger.info("{} number of bytes is read", read);
			
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add("Content-Range", "bytes "+ rangeStart + "-" + rangeEnd + "/" + fileLength);
			httpHeaders.add("Cache-Control", "no-cahce, no-store, must-revalidate");
			httpHeaders.add("Pragma", "no-cahce");
			httpHeaders.add("Expires", "0");
			httpHeaders.add("X-Content-Type-Options", "nosniff");
			httpHeaders.setContentLength(contentLength);
			
			
			return ResponseEntity
					.status(HttpStatus.PARTIAL_CONTENT)
					.headers(httpHeaders)
					.contentType(MediaType.parseMediaType(contentType))
					.body(new ByteArrayResource(data));
			
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
	}
	
	@GetMapping
	public ResponseEntity<List<Video>> getVideos(){
		List<Video> videos = videoService.getAllVideos();
		
		if(videos == null || videos.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(videos); 
	}
	
	
	

}
