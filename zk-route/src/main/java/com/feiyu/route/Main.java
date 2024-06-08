package com.feiyu.route;


import com.feiyu.interfaces.ISequenceService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.time.Duration;

@SpringBootApplication
@EnableDubbo
public class Main implements SpringApplicationRunListener {
    public static void main(String[] args) {
        SpringApplication.run(Main.class);
    }
}

@Component
class Test implements CommandLineRunner {
    @DubboReference
    private ISequenceService sequenceService;
    @Override
    public void run(String... args) throws Exception {
        System.out.println(sequenceService.gen(123L));
    }
}