package com.springboot.anecdote.interceptor;

import com.springboot.anecdote.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Class UserInterceptor 
 * Description //TODO 用户拦截器，检验用户登录状态及权限
 * Date 2020/9/21 20:15
 * @author Sampert
 * @version 1.0
 */
public class UserInterceptor implements HandlerInterceptor {

    private static final Logger logger= LoggerFactory.getLogger(UserInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        User user=(User) request.getSession().getAttribute("ANEC_USER_SESSION");
        if (user!=null){
            String servletPath=request.getServletPath();
            if (servletPath.startsWith("/client")){
                return true;
            }else if (user.getUserPower()==1){
                return true;
            }else {
                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write("<h2 style='color: #f00;'>权限不足！</h2>");
                logger.warn("拦截越权请求："+request.getRequestURI());
                return false;
            }
        }
        response.sendRedirect(request.getContextPath()+"/user/login");
        logger.warn("拦截未登录请求："+request.getRequestURI());
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) {
         logger.debug("处理中请求："+request.getRequestURI());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
         logger.debug("完成请求："+request.getRequestURI());
    }
}
