package com.example.springbootdg.controller;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

     @GetMapping(value = {"/get", "/get/{key}"})
    public ResponseEntity<String> get(@PathVariable Optional<String> key) {
        if (key.isPresent()) {
            String value = retrieveMap().get(key.get());
            return ResponseEntity.ok(value);
        } else {
            return ResponseEntity.ok(retrieveMap().values().toString());
        }
    }
}
