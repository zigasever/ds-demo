package com.example.springbootdg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;

@RestController()
@RequestMapping("/hazelcast")
public class HazelcastController {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @GetMapping("/cluster")
    public String getClusterInfo() {
        final String nl = System.lineSeparator();
        
        String res = "cluster " + hazelcastInstance.getCluster().toString() + " " +hazelcastInstance.getCluster().getClusterState().toString() + nl;
        for(Member m : hazelcastInstance.getCluster().getMembers()) {
            res += "    member " + m.getUuid().toString() + " addr: " + m.getAddress().toString() + " ver: " + m.getVersion().toString() + nl;
        }
        return res;
    }
    
}
