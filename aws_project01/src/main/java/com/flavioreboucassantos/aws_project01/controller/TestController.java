package com.flavioreboucassantos.aws_project01.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.Inet4Address;
import java.net.UnknownHostException;


@RestController
@RequestMapping("/api/test")
public class TestController {
    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/dog/{name}")
    public ResponseEntity<?> dogTest(@PathVariable String name) {
        LOG.info("Test controller - name: {}", name);

        return ResponseEntity.ok("Name: " + name);
    }

    @GetMapping("/myip")
    public ResponseEntity<?> myIP() {
        String data = "GET /myip \n";
        try {
            data += "Address: " + Inet4Address.getLocalHost().getHostAddress().toString() + "\n";
            data += "HostName: " + Inet4Address.getLocalHost().getHostName().toString() + "\n\n";
            LOG.info(data);
            return ResponseEntity.ok(data);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

}
