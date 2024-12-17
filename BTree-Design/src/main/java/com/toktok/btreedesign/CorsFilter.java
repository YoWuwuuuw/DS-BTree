package com.toktok.btreedesign;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
@Order(1)
@Component
public class CorsFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化方法，可以留空
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        // 设置允许访问的来源地址，这里设置为允许任何来源进行跨域请求
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        // 设置允许的请求方法，如 GET、POST 等
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        
        // 设置允许的请求头信息
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        
        // 继续执行过滤器链
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        // 销毁方法，可以留空
    }
}

