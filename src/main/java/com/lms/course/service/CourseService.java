package com.lms.course.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lms.course.dto.CourseUploadDto;
import com.lms.course.model.Account;
import com.lms.course.model.Course;
import com.lms.course.repository.AccountRepository;
import com.lms.course.repository.CourseRepository;
import com.lms.course.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {

	private final CourseRepository courseRepository;
	private final AccountRepository accountRepository;

	public ApiResponse uploadCourse(CourseUploadDto courseUploadDto, MultipartFile file, Account account) {

		try {
			Optional<Account> tutorOpt = accountRepository.findById(courseUploadDto.getTutorId());

			if (tutorOpt.isEmpty()) {
				return ApiResponse.error("Tutor not found");
			}
			
			if(!tutorOpt.get().getRole().name().equals("TUTOR")) {
				return ApiResponse.error("Provided tutorId does not belong to a tutor");
			}

			Course course = new Course();
			course.setTutorId(courseUploadDto.getTutorId());
			course.setTitle(courseUploadDto.getTitle());
			course.setDescription(courseUploadDto.getDesc());
			course.setPrice(courseUploadDto.getPrice());
			course.setCategory(courseUploadDto.getCategory());
			course.setContent(file.getBytes());
			courseRepository.save(course);

			return ApiResponse.success("Course uploaded successfully", course);
		} catch (Exception e) {
			return ApiResponse.error("Failed to upload course: " + e.getMessage());
		}
	}

	public ApiResponse updateCourse(Long courseId, CourseUploadDto courseUploadDto, MultipartFile file, Account account) {
		try {
			Optional<Course> courseOpt = courseRepository.findById(courseId);

			if (courseOpt.isEmpty()) {
				return ApiResponse.error("Course not found");
			}

			Course course = courseOpt.get();
			course.setTitle(courseUploadDto.getTitle());
			course.setDescription(courseUploadDto.getDesc());
			course.setPrice(courseUploadDto.getPrice());
			course.setCategory(courseUploadDto.getCategory());
			course.setContent(file.getBytes());
			courseRepository.save(course);

			return ApiResponse.success("Course updated successfully", course);
		}catch (Exception e) {
			return ApiResponse.error("Failed to update course: " + e.getMessage());
		}
	}

	public ApiResponse deleteCourse(Long courseId, Account account) {
		try {
			Optional<Course> courseOpt = courseRepository.findById(courseId);

			if (courseOpt.isEmpty()) {
				return ApiResponse.error("Course not found");
			}

			courseRepository.deleteById(courseId);
			return ApiResponse.success("Course deleted successfully");
		}catch (Exception e) {
			return ApiResponse.error("Failed to delete course: " + e.getMessage());
		}
	}

	public ApiResponse getTutorCourses(Long tutorId, Account account) {
		try {
			Optional<Account> tutorOpt = accountRepository.findById(tutorId);

			if (tutorOpt.isEmpty()) {
				return ApiResponse.error("Tutor Not Found", Collections.emptyList(), HttpStatus.NOT_FOUND.value());
			}
			return ApiResponse.success("Courses retrieved successfully", courseRepository.findByTutorId(tutorId));
		}catch (Exception e) {
			return ApiResponse.error("Failed to retrieve courses: " + e.getMessage());
		}
	}

	public ApiResponse getAllCourses() {
		try {
			return ApiResponse.success("Courses retrieved successfully", courseRepository.findAll());
		}catch (Exception e) {
			return ApiResponse.error("Failed to retrieve courses: " + e.getMessage());
		}
	}

}
