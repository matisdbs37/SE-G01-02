package com.unizg.fer.categories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/categories")
public class CategoriesController {

    @Autowired
    public CategoriesService service;

    @GetMapping("/")
    public ResponseEntity<List<Categories>> getAllCategories() {
        var categories = service.getAll();
        return ResponseEntity.ok(categories);
    }

}
