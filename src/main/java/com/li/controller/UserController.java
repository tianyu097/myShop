package com.li.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.li.pojo.User;
import com.li.service.SsoService;
import com.li.service.UserService;
import com.li.utils.CookieUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@Controller
@RequestMapping("/user")
public class UserController {
    @Reference
    private UserService userService;
    @Reference
    private SsoService ssoService;

    @RequestMapping("/validUsername")
    @ResponseBody
    public boolean validUsername(String username) {
        User user = userService.getByUsername(username);
        return user != null;
    }

    @RequestMapping("/validPassword")
    @ResponseBody
    public boolean validPassword(String username, String password) {
        User user = userService.getByUsername(username);
        return user != null && password.equals(user.getPassword());
    }

    @RequestMapping("/alreadyLogin")
    public String alreadyLogin() {
        return "alreadyLogin";
    }

//    @RequestMapping

    @RequestMapping("/tologin")
    public String tologin(HttpServletRequest request, HttpServletResponse response) {
        return "login";
    }

    @RequestMapping("/login")
    public String login(String username, String password, HttpServletRequest request, HttpServletResponse response) {
        String token = CookieUtil.getValue(request, "token");
        User user=ssoService.getUser(username,token);
        if (user!=null){
            System.out.println("该用户已经登录");
            return  "redirect:/";
        }
        Set keys = ssoService.keys("user:" + username + ":*");
        if (keys!=null && keys.size()>0){
            ssoService.delete(keys);
        }
        //用户名和密码正常

        user = userService.getByUsername(username);
        //存入cookie
        CookieUtil.SetValue("username", username, 10 * 24 * 3600, response);
        //存入redis
        ssoService.setUser(username,token,user,30);
        return "redirect:/";
    }
}
