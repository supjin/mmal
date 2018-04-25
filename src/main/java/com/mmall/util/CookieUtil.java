package com.mmall.util;

import com.sun.deploy.net.HttpRequest;
import com.sun.deploy.net.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class CookieUtil {
    private final static String COOKIE_DOMAIN = ".happymmall.com";
    private final static String COOKIE_NAME = "mmall_login_token";


    public static void deleteLoginToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();
        for (Cookie ck : cks) {
            if (StringUtils.equals(ck.getName(), COOKIE_NAME)) {
                ck.setDomain(COOKIE_DOMAIN);
                ck.setPath("/");
                ck.setMaxAge(0);//设置为0--直接删除
                log.info("delete cookieName:{},cookieValue:{}", ck.getName(), ck.getValue());
                response.addCookie(ck);
                return;
            }
        }

    }

    public static String readLoginToken(HttpServletRequest request) {
        Cookie[] cks = request.getCookies();
        for (Cookie ck : cks) {
            log.info("read cookieName:{},cookieValue:{}", ck.getName(), ck.getValue());
            if (StringUtils.equals(ck.getName(), COOKIE_NAME)) {
                log.info("return cookieName:{},cookieValue:{}", ck.getName(), ck.getValue());
                return ck.getValue();
            }
        }
        return null;
    }
//a:A.happymmall.com   cookie:domail=A.
    //b:B.happygomall.com
    //c:A.happymmall.com/test/dd
    //d:A.happymmall.com/test/cc
    //e:A.happymmall.com/test



    public static void writeLoginToken(HttpServletResponse response, String token) {
        Cookie ck;
        ck = new Cookie(COOKIE_NAME, token);
      //  ck.setDomain(COOKIE_DOMAIN);
        ck.setHttpOnly(true);
        ck.setPath("/");//设置在根目录
        ck.setMaxAge(60 * 60 * 24 * 365);//一年
        log.info("write cookieName:{},cookieValue:{}", ck.getName(), ck.getValue());
        response.addCookie(ck);
    }


}
