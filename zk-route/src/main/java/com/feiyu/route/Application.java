package com.feiyu.route;


import com.feiyu.interfaces.ISequenceService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

/**
 * @author Zhuff
 */
@SpringBootApplication
@EnableDubbo
public class Application implements SpringApplicationRunListener {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}

@Component
class Test implements CommandLineRunner {
    @DubboReference
    private ISequenceService sequenceService;
    @Override
    public void run(String... args) throws Exception {
    }
}