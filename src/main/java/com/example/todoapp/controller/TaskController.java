package com.example.todoapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
public class TaskController {

    @GetMapping("/tasks")
    public List<Map<String, Object>> getTasks() {
        return Arrays.asList(
                Map.of("id", 1, "title", "Buy milk", "done", false),
                Map.of("id", 2, "title", "Do homework", "done", true),
                Map.of("id", 3, "title", "Walk the dog", "done", false)
        );
    }

    @GetMapping("/")
    public String root() {
        return "Todo app is running. Check /tasks";
    }
}
