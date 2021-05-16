package com.springboot.anecdote.interceptor;

import com.springboot.anecdote.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户（User）拦截器，检验用户登录状态及权限
 *
 * @author Sampert
 * @version 1.0
 * @date 2020/9/21 20:15
 */
public class UserInterceptor implements HandlerInterceptor {

    /** self4j 日志记录实例 */
    private static final Logger logger = LoggerFactory.getLogger(UserInterceptor.class);

    /**
     * 拦截请求的前置处理
     * @param request HTTP请求
     * @param response HTTP响应
     * @param handler 处理器
     * @return boolean 请求放行返回 true，否则 false
     * @throws Exception 任何可能的异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = (User) request.getSession().getAttribute("ANEC_USER_SESSION");
        if (user != null) {
            String servletPath = request.getServletPath();
            if (servletPath.startsWith("/client")) {
                return true;
            } else if (user.getUserPower() == 1) {
                return true;
            } else {
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write("<h2 style='color: #f00;'>权限不足！</h2>");
                logger.warn("拦截越权请求：{}", request.getRequestURI());
                return false;
            }
        }
        response.sendRedirect(request.getContextPath() + "/user/login");
        logger.warn("拦截未登录请求：{}", request.getRequestURI());
        return false;
    }

    /**
     * 拦截请求正在执行中的处理
     * @param request HTTP请求
     * @param response HTTP响应
     * @param handler 处理器
     * @param modelAndView 模型视图
     * @return void
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        logger.info("处理中请求：{}", request.getRequestURI());
    }

    /**
     * 拦截请求的后置处理
     * @param request HTTP请求
     * @param response HTTP响应
     * @param handler 处理器
     * @param ex 异常情况
     * @return void
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        logger.info("完成请求：{}", request.getRequestURI());
    }
}
