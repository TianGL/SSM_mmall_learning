package cn.geliang.mmall.controller.portal;

import cn.geliang.mmall.common.Const;
import cn.geliang.mmall.common.RedisPool;
import cn.geliang.mmall.common.ResponseCode;
import cn.geliang.mmall.common.ServerResponse;
import cn.geliang.mmall.pojo.User;
import cn.geliang.mmall.service.IUserServcie;
import cn.geliang.mmall.util.CookieUtill;
import cn.geliang.mmall.util.JsonUtil;
import cn.geliang.mmall.util.RedisPoolUtil;
import cn.geliang.mmall.util.RedisSharedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserServcie iUserServcie;

    /**
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session,
                                      HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {
        ServerResponse<User> response = iUserServcie.login(username, password);
        if(response.isSuccess()) {
//            session.setAttribute(Const.CURRENT_USER, response.getData());
            CookieUtill.writeLoginToken(httpServletResponse, session.getId());
            RedisSharedPoolUtil.setex(session.getId(), Const.RedisCacheExtime.REDIS_SESSION_EXTIME, JsonUtil.obj2String(response.getData()));
        }
        return response;
    }

    /**
     * 用户登出
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
//        session.removeAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtill.readLoginToken(httpServletRequest);
        CookieUtill.delLoginToken(httpServletRequest, httpServletResponse);
        RedisSharedPoolUtil.del(loginToken);
        return ServerResponse.createBySuccess();
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iUserServcie.register(user);
    }

    /**
     * 检查参数是否存在，存在失败，不存在成功
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,  String type) {
        return iUserServcie.checkValid(str,  type);
    }

    /**
     * 获取用户信息
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest httpServletRequest) {

        String loginToken = CookieUtill.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String userJsonStr = RedisSharedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obje(userJsonStr, User.class);

        if(user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
    }

    /**
     * 获取忘记密码提示问题
     * @param username
     * @return
     */
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username) {
        return iUserServcie.selectQuestion(username);
    }

    /**
     * 检查忘记密码的答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserServcie.checkAnwer(username, question, answer);
    }

    /**
     * 重置密码（根据提示问题）
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        return iUserServcie.forgetResetPassword(username, passwordNew, forgetToken);
    }

    /**
     * 重置密码（登录状态）
     * @param httpServletRequest
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpServletRequest httpServletRequest, String passwordOld, String passwordNew) {
        String loginToken = CookieUtill.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String userJsonStr = RedisSharedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obje(userJsonStr, User.class);
        if(user != null) {
            return iUserServcie.resetPassword(passwordOld, passwordNew, user);
        }
        return ServerResponse.createByErrorMessage("用户未登录");
    }

    /**
     * 修改用户信息
     * @param httpServletRequest
     * @param user
     * @return
     */
    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpServletRequest httpServletRequest, User user) {
        String loginToken = CookieUtill.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String userJsonStr = RedisSharedPoolUtil.get(loginToken);
        User currentUser = JsonUtil.string2Obje(userJsonStr, User.class);
        if(currentUser == null) {
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        // id和username均为当前用户的
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserServcie.updateInformation(user);
        if(response.isSuccess()){
            response.getData().setUsername(currentUser.getUsername());
//            session.setAttribute(Const.CURRENT_USER, response.getData());
            RedisSharedPoolUtil.setex(loginToken, Const.RedisCacheExtime.REDIS_SESSION_EXTIME, JsonUtil.obj2String(response.getData()));
        }
        return response; // 失败即为错误信息
    }

    /**
     * 获取当前用户信息
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtill.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String userJsonStr = RedisSharedPoolUtil.get(loginToken);
        User currentUser = JsonUtil.string2Obje(userJsonStr, User.class);
        if(currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录，需要强制登陆status=10");
        }
        return iUserServcie.getInformation(currentUser.getId());
    }

    @RequestMapping(value = "test.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> test(HttpServletRequest httpServletRequest, String passwordOld, String passwordNew) {
        String loginToken = CookieUtill.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String userJsonStr = RedisSharedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obje(userJsonStr, User.class);
        if(user != null) {
            return iUserServcie.test(passwordOld, passwordNew, user);
        }
        return ServerResponse.createByErrorMessage("用户未登录");
    }


}
