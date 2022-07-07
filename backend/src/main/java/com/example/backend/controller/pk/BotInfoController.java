package com.example.backend.controller.pk;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/pk")
public class BotInfoController {
    @RequestMapping("/getbotinfo")
    public List<Map<String, String>> getBotInfo(){
        List<Map<String, String>> list = new LinkedList<>();
        Map<String, String> map1 = new HashMap<>();
        map1.put("rsh", "1500");
        map1.put("jbb", "1800");

        Map<String, String> map2 = new HashMap<>();
        map2.put("tom", "1600");
        map2.put("mike", "1300");
        list.add(map1);
        list.add(map2);
        return list;
    }
}
