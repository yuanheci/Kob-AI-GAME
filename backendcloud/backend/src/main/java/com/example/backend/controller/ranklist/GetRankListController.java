package com.example.backend.controller.ranklist;

import com.alibaba.fastjson.JSONObject;
import com.example.backend.service.ranklist.GetRankListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GetRankListController {
    @Autowired
    private GetRankListService GetRankListService;

    @GetMapping("/api/ranklist/getlist")
    public JSONObject getlist(@RequestParam Map<String, String> data){
        Integer page = Integer.parseInt(data.get("page"));
        return GetRankListService.getList(page);
    }


}
