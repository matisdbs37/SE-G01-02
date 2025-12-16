package com.unizg.fer.content;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v2/content")
public class ContentController {

    @Autowired
    ContentService service;
    

    @GetMapping("/type/{text}")
    public ResponseEntity<List<Content>> getContentByType(@PathVariable String text) {
        var type = ContentType.getType(text);
        var contents = service.findAllByType(type);
        return ResponseEntity.ok(contents);
    }

    @GetMapping("/{title}")
    public ResponseEntity<Content> getContentById(@PathVariable String title) {
        var content = service.findByTitle(title);
        return ResponseEntity.ok(content);
    }

}
