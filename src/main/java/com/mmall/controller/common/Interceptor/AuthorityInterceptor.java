package com.mmall.controller.common.Interceptor;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        HandlerMethod handlerMethod = (HandlerMethod) o;

        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();
        StringBuffer requestStringBuffer = new StringBuffer();
        Map<String, String[]> paramMap = httpServletRequest.getParameterMap();
        Iterator<Map.Entry<String, String[]>> it = paramMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String[]> entry = it.next();
            String mapKey = entry.getKey();
            String mapValueString = StringUtils.EMPTY;
            String[] mapValue = entry.getValue();

            mapValueString = Arrays.toString(mapValue);
            requestStringBuffer.append(mapKey).append("=").append(mapValueString);
        }

        if (StringUtils.equals(className, "UserManageController") && StringUtils.equals(methodName, "login")) {
            log.info("拦截到请求,className:{},methodName:{}", className, methodName);
            //拦截到登陆请求，不打印参数日志---因为里面包含账号密码--
//            直接返回true
            return true;
        }
        log.info("拦截器拦截到请求,className:{},methodName:{},param:{}",className,methodName,paramMap);


        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        User user = null;

        if (StringUtils.isNotEmpty(loginToken)) {
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr, User.class);
        }
        if (user == null || user.getRole().intValue() != Const.Role.ROLE_ADMIN) {
            httpServletResponse.reset();
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            PrintWriter out = httpServletResponse.getWriter();
            if (user == null) {
                out.append(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，用户未登录")));
            } else {
                out.append(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，非管理员登陆")));
            }
            out.flush();
            out.close();
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
