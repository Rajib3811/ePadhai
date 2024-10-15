package com.ePadhai.controller.Course;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CourseController {
	
	@GetMapping("/course")
	public String course() {
		return "course";
	}

}
