package com.example.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.mapper.UserMapper;
import com.example.backend.pojo.User;
import com.example.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> register(String username, String password, String confirmedpassword) {
        Map<String, String> map = new HashMap<>();
        if(username == null){
            map.put("error_message", "用户名不能为空");
            return map;
        }

        if(password == null || confirmedpassword == null){
            map.put("error_message", "密码不能为空");
            return map;
        }

        username = username.trim();
        if(username.length() == 0) {
            map.put("error_message", "用户名不能为空");
            return map;
        }

        if(username.length() > 100){
            map.put("error_message", "用户名长度不能大于100");
            return map;
        }

        if(password.length() == 0 || confirmedpassword.length() == 0){
            map.put("error_message", "密码不能为空");
            return map;
        }

        if(password.length() > 100 || confirmedpassword.length() > 100){
            map.put("error_message", "密码长度不能大于100");
            return map;
        }

        if(!password.equals(confirmedpassword)){
            map.put("error_message", "两次输入的密码不一致");
            return map;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("username", username);
        List<User> users = userMapper.selectList(queryWrapper);
        if(!users.isEmpty()){
            map.put("error_message", "用户名已存在");
            return map;
        }

        //此时可以把信息存入数据库
        String encodePassword = passwordEncoder.encode(password);
        String photo = "https://cdn.acwing.com/media/user/profile/photo/171471_lg_2b7b7c6a0b.webp";
        User user = new User(null, username, encodePassword, photo);
        userMapper.insert(user);

        map.put("error_message", "success");
        return map;
    }
}
