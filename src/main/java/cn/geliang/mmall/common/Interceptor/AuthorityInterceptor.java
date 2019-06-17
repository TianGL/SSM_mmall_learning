package cn.geliang.mmall.common.Interceptor;

import cn.geliang.mmall.common.Const;
import cn.geliang.mmall.common.ServerResponse;
import cn.geliang.mmall.pojo.User;
import cn.geliang.mmall.util.CookieUtill;
import cn.geliang.mmall.util.JsonUtil;
import cn.geliang.mmall.util.RedisSharedPoolUtil;
import com.google.common.collect.Maps;
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
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandle");
        // Controller中的方法名
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 解析handlerMethod
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();

        // 解析参数，具体的key及value
        StringBuffer requestParameterBuffer = new StringBuffer();
        Map paramMap = request.getParameterMap();
        Iterator it = paramMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String mapKey = (String) entry.getKey();

            String mapValue = StringUtils.EMPTY;

            // requestMap的value是String数组
            Object object = entry.getValue();
            if (object instanceof String[]) {
                String[] strs = (String[]) object;
                mapValue = Arrays.toString(strs);
            }
            requestParameterBuffer.append(mapKey).append("=").append(mapValue);
        }

        if (StringUtils.equals(className, "UserManageController") && StringUtils.equals(methodName, "login")) {
            log.info("权限拦截器拦截到请求，className:{}, methodName:{}", className, methodName);
            // 拦截到登录请求，不打印日志
            return true;
        }

        User user = null;
        String loginToken = CookieUtill.readLoginToken(request);
        if (StringUtils.isNotEmpty(loginToken)) {
            String userJsonStr = RedisSharedPoolUtil.get(loginToken);
            user = JsonUtil.string2Obje(userJsonStr, User.class);
        }

        if (user == null || user.getRole().intValue() != Const.Role.ROLE_ADMIN) {
            // 返回false，即不会调用controller里的方法
            response.reset(); // 添加reset,否则报错
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");

            PrintWriter out = response.getWriter();
            if (user == null) {
                if (StringUtils.equals(className, "ProductManageController") && StringUtils.equals(methodName, "richtextImgUpload")) {
                    // 富文本上传拦截, 返回统一的map格式
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "请登录管理员");
                    out.print(JsonUtil.obj2String(resultMap));
                } else {
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，用户未登录")));
                }

            } else {
                if (StringUtils.equals(className, "ProductManageController") && StringUtils.equals(methodName, "richtextImgUpload")) {
                    // 富文本上传拦截, 返回统一的map格式
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "无权限操作");
                    out.print(JsonUtil.obj2String(resultMap));
                } else {
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，不是管理员")));
                }
            }
            out.flush();
            out.close();
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("afterCompletion");
    }
}
