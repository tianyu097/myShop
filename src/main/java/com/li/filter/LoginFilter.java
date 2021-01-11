package com.li.filter;

import com.alibaba.dubbo.config.annotation.Reference;
import com.li.pojo.User;
import com.li.service.SsoService;
import com.li.utils.CookieUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@WebFilter(value = {"/cart/*","/order/*"})
public class LoginFilter implements Filter {
    @Reference
    private SsoService ssoService;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        HttpServletResponse response= (HttpServletResponse) servletResponse;
        String username = CookieUtil.getValue(request, "username");
        String token = CookieUtil.getValue(request, "token");
        User user =ssoService.getUser(username,token);
        System.out.println(user);
        if (user!=null){
            filterChain.doFilter(request,response);
            return;
        }
        else{
            Set keys =ssoService.keys("user:" + username + ":*");
            if (keys!=null && keys.size()>0){//该用户在别的浏览器已经登录过
                //提示:该用户在别的浏览器已经登录过
//                response.sendRedirect(request.getContextPath() + "/user/alreadyLogin");
                //操作redis,将原来登录从redis缓存中清除
               // Long count = ssoService.delete(keys);
                //提示:该用户在别的浏览器已经登录过
                response.sendRedirect(request.getContextPath() + "/user/alreadyLogin");
//                response.sendRedirect(request.getContextPath() + "/user/tologin");
                return ;
            }else {
                response.sendRedirect(request.getContextPath() + "/user/tologin");
            }
        }

    }

    @Override
    public void destroy() {

    }
}
