package com.example.backend.service.impl.user.account;

import com.example.backend.pojo.User;
import com.example.backend.service.impl.utils.UserDetailsImpl;
import com.example.backend.service.user.account.InfoService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class InfoServiceImpl implements InfoService {
    //需要通过JwtAuthenticationTokenFilter的token校验后才能来到这里获取信息
    //在整个与外部使用交互的过程中Authentication的职责有两个，第一个是封装了验证请求的参数，第二个便是封装了用户的权限信息
    //身份信息交互的纽带：Authentication
    /*
      getAuthentication()返回的是Authentication对象，由于UsernamePasswordAuthenticationToken是返回的是Authentication
      的实现类，所以可以接收
    * */
    @Override
    public Map<String, String> getInfo() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();  //getUser是由lombok在UserDetailsImpl中用@Data注解自动实现的方法

        Map<String, String> map = new HashMap<>();
        map.put("error_message", "success");
        map.put("id", user.getId().toString());
        map.put("username", user.getUsername());
        map.put("photo", user.getPhoto());
        return map;
    }
}
