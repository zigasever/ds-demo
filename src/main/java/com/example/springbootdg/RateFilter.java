package com.example.springbootdg;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(1)
public class RateFilter implements Filter {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private Supplier<BucketConfiguration> bucketConfig;

    @Autowired
    private ProxyManager<String> proxyManager;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String key = httpRequest.getRemoteAddr();
        Bucket bucket = proxyManager.builder().build(key, bucketConfig);
        
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        
        System.out.println("remaining tokens: " + probe.getRemainingTokens());
        LOG.debug("remaining tokens: {}", probe.getRemainingTokens());

        if (probe.isConsumed()) {
            filterChain.doFilter(httpRequest, servletResponse);
        } else {
            HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
            httpResponse.setContentType("text/plain");
            httpResponse.setHeader("X-Rate-Limit-Retry-After-Seconds",
                    "" + TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));
            httpResponse.setStatus(429);
            httpResponse.getWriter().append("Too many requests");
        }
    }

}
