package com.lms.course.dto;

import lombok.Data;

@Data
public class CourseUploadDto {
	private Long tutorId;
	private String title;
	private String desc;
	private Double price;
	private String category;
	private String status;
}
