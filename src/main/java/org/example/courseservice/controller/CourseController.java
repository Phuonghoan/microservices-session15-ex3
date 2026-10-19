package org.example.courseservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    // Danh sách khóa học lưu trong bộ nhớ
    private final List<Course> courses =
            new CopyOnWriteArrayList<>(List.of(
                    new Course(1L, "Java Spring Boot"),
                    new Course(2L, "Microservices"),
                    new Course(3L, "Spring Security JWT")
            ));

    private final AtomicLong nextId = new AtomicLong(4);

    // ==================================
    // STUDENT VÀ INSTRUCTOR ĐƯỢC XEM
    // ==================================

    @GetMapping
    @PreAuthorize(
            "hasAuthority('STUDENT') or hasAuthority('INSTRUCTOR')"
    )
    public List<Course> getAllCourses() {

        return List.copyOf(courses);
    }

    // ==================================
    // CHỈ INSTRUCTOR ĐƯỢC TẠO
    // ==================================

    @PostMapping
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public ResponseEntity<Course> createCourse(
            @RequestBody CourseRequest request
    ) {

        if (request.name() == null
                || request.name().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tên khóa học không được để trống"
            );
        }

        Course course = new Course(
                nextId.getAndIncrement(),
                request.name()
        );

        courses.add(course);

        System.out.println(
                "INSTRUCTOR đã tạo khóa học: " + course.name()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(course);
    }

    public record Course(
            Long id,
            String name
    ) {
    }

    public record CourseRequest(
            String name
    ) {
    }
}