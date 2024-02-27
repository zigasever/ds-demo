package com.example.springbootdg.controller;

import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springbootdg.model.ValueMapping;
import com.hazelcast.core.HazelcastInstance;

@RestController
public class CommandController {
    
    @Autowired
    private HazelcastInstance hazelcastInstance;

    private ConcurrentMap<String,String> retrieveMap() {
        return hazelcastInstance.getMap("map");
    }

    @PostMapping("/put")
    public ResponseEntity<String> put(@RequestBody ValueMapping req) {
        retrieveMap().put(req.getKey(), req.getValue());
        return ResponseEntity.ok(null);
    }

    @GetMapping("/get")
    public ResponseEntity<String> get(@RequestParam(value = "key") String key) {
        String value = retrieveMap().get(key);
        return ResponseEntity.ok(value);
    }
}
