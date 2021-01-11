package com.li;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class MyshopPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyshopPortalApplication.class, args);
    }

}
