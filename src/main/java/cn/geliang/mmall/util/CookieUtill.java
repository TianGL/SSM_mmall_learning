package cn.geliang.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class CookieUtill {
    private final  static String COOKIE_DOMAIN = ".geliang.com";
    private final static String COOKIE_NAME = "mmall_login_token";

    /**
     * 尝试获取login cookie
     * @param request
     * @return
     */
    public static String readLoginToken(HttpServletRequest request) {
        Cookie[] cks = request.getCookies();
        if (cks != null) {
            for (Cookie ck : cks) {
                log.info("write cookieName:{}, cookieValue:{}", ck.getName(), ck.getValue());
                if (StringUtils.equals(ck.getName(), COOKIE_NAME)) {
                    log.info("return cookieName:{}, cookieValue:{}", ck.getName(), ck.getValue());
                    return ck.getValue();
                }
            }
        }
        return null;
    }

    // X:domain=".geliang.com"
    // a: a.geliang.com                 cookie:domain=a.geliang.com; path="/"
    // b: b.geliang.com                 cookie:domain=b.geliang.com; path="/"
    // c: a.geliang.com/test/cc         cookie:domain=a.geliang.com; path="/test/cc"
    // d: a.geliang.com/test/dd         cookie:domain=a.geliang.com; path="/test/dd"
    // e: a.geliang.com/test            cookie:domain=a.geliang.com; path="/test"

    /**
     * 将登陆的token存入cookie
     * @param response
     * @param token
     */
    public static void writeLoginToken(HttpServletResponse response, String token) {
        Cookie ck = new Cookie(COOKIE_NAME, token);
        ck.setDomain(COOKIE_DOMAIN);
        ck.setPath("/"); // 存到根目录
        ck.setHttpOnly(true); // 不许通过脚本访问cookie, 防止被攻击
        // 单位是秒
        // 如果这个maxage不设置的话，cookie就不会写入硬盘，而是写在内存。只在当前页面有效
        ck.setMaxAge(60 * 60 * 24 * 356); // -1代表永久
        log.info("write cookieName:{}, cookieValue:{}", ck.getName(), ck.getValue());
        response.addCookie(ck);
    }

    public static void delLoginToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cks = request.getCookies();
        if (cks != null) {
            for (Cookie ck : cks) {
                if (StringUtils.equals(ck.getName(), COOKIE_NAME)) {
                    ck.setDomain(COOKIE_DOMAIN);
                    ck.setPath("/");
                    ck.setMaxAge(0); // 设置成0，代表删除此cookie
                    log.info("del cookieName:{}, cookieValue:{}", ck.getName(), ck.getValue());
                    response.addCookie(ck);
                    return;
                }
            }
        }
    }
}
