package cn.geliang.mmall.controller.backend;

import cn.geliang.mmall.common.Const;
import cn.geliang.mmall.common.ServerResponse;
import cn.geliang.mmall.pojo.User;
import cn.geliang.mmall.service.IUserServcie;
import cn.geliang.mmall.util.CookieUtill;
import cn.geliang.mmall.util.JsonUtil;
import cn.geliang.mmall.util.RedisSharedPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/manage/user/")
public class UserManageController {

    @Autowired
    private IUserServcie iUserServcie;

    @RequestMapping(value = "login.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(HttpServletResponse httpServletResponse, String username, String password, HttpSession session) {
        ServerResponse<User> response = iUserServcie.login(username, password);
        if(response.isSuccess()) {
            User user = response.getData();
            if(user.getRole() == Const.Role.ROLE_ADMIN) { // 确定登陆的用户是不是管理员
                CookieUtill.writeLoginToken(httpServletResponse, session.getId());
                RedisSharedPoolUtil.setex(session.getId(), Const.RedisCacheExtime.REDIS_SESSION_EXTIME, JsonUtil.obj2String(response.getData()));
                return response;
            } else {
                return ServerResponse.createByErrorMessage("不是管理员，无法登陆");
            }
        }
        return response;
    }

}
