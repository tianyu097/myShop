package com.li.filter;

import com.li.utils.CookieUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@WebFilter("/*")
public class ZongFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        HttpServletResponse response= (HttpServletResponse) servletResponse;
        String token1 = CookieUtil.getValue(request, "token");
        if (token1==null) {
            String uuid = UUID.randomUUID().toString().toUpperCase();
            Cookie token = new Cookie("token", uuid);
            token.setMaxAge(10 * 24 * 3600);
            token.setPath("/");
            response.addCookie(token);
        }
        filterChain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
