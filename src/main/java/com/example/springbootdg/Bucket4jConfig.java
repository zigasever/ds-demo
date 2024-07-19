package com.example.springbootdg;

import java.time.Duration;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.core.HazelcastInstance;

import io.github.bucket4j.BandwidthBuilder;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ClientSideConfig;
import io.github.bucket4j.grid.hazelcast.HazelcastProxyManager;

@Configuration
public class Bucket4jConfig {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Bean
    public Supplier<BucketConfiguration> bucketConfig() {
        return () -> BucketConfiguration.builder()
                .addLimit(
                    BandwidthBuilder.builder().capacity(250).refillIntervally(250, Duration.ofSeconds(1)).build())
                .build();
                
    }

    @Bean
    public HazelcastProxyManager<String> hcProxyMAnager() {
        return new HazelcastProxyManager<>(hazelcastInstance.getMap("buckets"),
                ClientSideConfig.getDefault());

    }
}
