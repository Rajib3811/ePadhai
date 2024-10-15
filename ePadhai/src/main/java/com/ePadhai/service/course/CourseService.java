package com.ePadhai.service.course;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ePadhai.model.course.Course;
import com.ePadhai.repository.course.CourseRepository;

@Service
public class CourseService {
	
	private final CourseRepository courseRepository;

	public CourseService(CourseRepository courseRepository) {
		super();
		this.courseRepository = courseRepository;
	}
	
	public List<Course> getAllCourses(){
		return courseRepository.findAll();
	}
	
	public void saveCourse(Course course) {
		courseRepository.save(course);
	}
	
	public Course getCourseById(Long id) {
		return courseRepository.findById(id).orElseThrow();
	}

}
