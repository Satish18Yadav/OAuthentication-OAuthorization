package com.satish.authandauthorization.oauth2_jan2025;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String Home(){
        return "This is our Home";
    }

    @GetMapping("/secured")
    public  String secured(){
        return "This is a secured URL";
    }


}
