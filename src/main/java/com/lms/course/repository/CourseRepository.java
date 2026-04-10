package com.lms.course.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.course.model.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {

	List<Course> findByTutorId(Long tutorId);

}
