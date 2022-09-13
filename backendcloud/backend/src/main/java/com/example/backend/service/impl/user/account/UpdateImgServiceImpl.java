package com.example.backend.service.impl.user.account;

import com.example.backend.mapper.UserMapper;
import com.example.backend.pojo.User;
import com.example.backend.service.impl.utils.UserDetailsImpl;
import com.example.backend.service.user.account.UpdateImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UpdateImgServiceImpl implements UpdateImgService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public Map<String, String> updateImg(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        String url = data.get("url");
        System.out.println(user);

        Map<String, String> map = new HashMap<>();

        if(url == null || url.length() == 0) {
            map.put("error_message", "头像地址不能为空");
            return map;
        }

        user.setPhoto(url);
        userMapper.updateById(user);
        map.put("error_message", "success");
        return map;
    }
}
