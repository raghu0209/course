package com.lms.course.controller;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lms.course.dto.CourseUploadDto;
import com.lms.course.model.Account;
import com.lms.course.service.CourseService;
import com.lms.course.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

	private final CourseService courseService;

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('TUTOR')")
	public ApiResponse uploadCourse(@RequestBody CourseUploadDto courseUploadDto, @RequestPart("file") MultipartFile file, @RequestAttribute("AuthorisedUser") Account account) {
		return courseService.uploadCourse(courseUploadDto, file, account);
	}
	
	@PostMapping(value = "/update/{courseId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('TUTOR')")
	public ApiResponse updateCourse(@PathVariable Long courseId, @RequestBody CourseUploadDto courseUploadDto, @RequestPart("file") MultipartFile file, @RequestAttribute("AuthorisedUser") Account account) {
		return courseService.updateCourse(courseId, courseUploadDto, file, account);
		
	}
	
	@DeleteMapping("delete/{courseId}")
	public ApiResponse deleteCourse(@PathVariable Long courseId, @RequestAttribute("AuthorisedUser") Account account) {
		return courseService.deleteCourse(courseId, account);
	}	
	
	@GetMapping("/tutorCourses/{tutorId}")
	@PreAuthorize("hasRole('TUTOR') or hasRole('ADMIN')")
	public ApiResponse getTutorCourses(@PathVariable Long tutorId, @RequestAttribute("AuthorisedUser") Account account) {
		return courseService.getTutorCourses(tutorId, account);
	}
	
	@GetMapping
	@PreAuthorize("hasAnyRole('TUTOR', 'STUDENT', 'ADMIN')")
	public ApiResponse getAllCourses() {
		return courseService.getAllCourses();
	}
}
