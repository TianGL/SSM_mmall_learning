package cn.geliang.mmall.service;

import cn.geliang.mmall.common.ServerResponse;
import cn.geliang.mmall.pojo.User;

public interface IUserServcie {

    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse<String> selectQuestion(String username);

    ServerResponse<String> checkAnwer(String username, String question, String answer);

    ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);

    ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User> getInformation(Integer userId);

    ServerResponse<User> test(String passwordOld, String passwordNew, User user);

    ServerResponse checkAdminRole(User user);
}
