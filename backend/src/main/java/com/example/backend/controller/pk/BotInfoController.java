package com.example.backend.controller.pk;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/pk/")
public class BotInfoController {
    @RequestMapping("/getbotinfo/")
    public Map<String, String> getBotInfo(){
        Map<String, String> map = new HashMap<>();

        map.put("name", "tiger");
        map.put("rating", "1900");
        return map;
    }
}
