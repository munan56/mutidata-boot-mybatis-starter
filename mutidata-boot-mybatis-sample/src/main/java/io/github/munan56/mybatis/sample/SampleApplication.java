package io.github.munan56.mybatis.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SampleApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(SampleApplication.class, args);
        System.out.println(run);
    }

}
