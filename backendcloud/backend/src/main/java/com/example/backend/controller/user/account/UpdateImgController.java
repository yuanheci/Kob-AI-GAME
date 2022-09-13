package com.example.backend.controller.user.account;

import com.example.backend.service.impl.user.account.UpdateImgServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UpdateImgController {
    @Autowired
    private UpdateImgServiceImpl updateImgService;

    @PostMapping("/api/user/account/updateImg/")
    Map<String, String> updateImg(@RequestBody Map<String, String> data){  //以json格式接收，然后塞进Map中(依靠jackson实现)
        return updateImgService.updateImg(data);
    }
}
