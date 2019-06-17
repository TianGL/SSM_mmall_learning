package cn.geliang.mmall.controller.backend;

import cn.geliang.mmall.common.Const;
import cn.geliang.mmall.common.ResponseCode;
import cn.geliang.mmall.common.ServerResponse;
import cn.geliang.mmall.pojo.Category;
import cn.geliang.mmall.pojo.User;
import cn.geliang.mmall.service.ICategoryService;
import cn.geliang.mmall.service.IUserServcie;
import cn.geliang.mmall.util.CookieUtill;
import cn.geliang.mmall.util.JsonUtil;
import cn.geliang.mmall.util.RedisPoolUtil;
import cn.geliang.mmall.util.RedisSharedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping(value = "/manage/category/")
public class CatagoryManageController {

    @Autowired
    private IUserServcie iUserServcie;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpServletRequest httpServletRequest, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
//        String loginToken = CookieUtill.readLoginToken(httpServletRequest);
//        if (StringUtils.isEmpty(loginToken)) {
//            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
//        }
//        String userJsonStr = RedisSharedPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obje(userJsonStr, User.class);
//        if(user == null) {
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
//        }
//        // 校验是否是管理员
//        if(iUserServcie.checkAdminRole(user).isSuccess()) {
//            // 是管理员
//            return iCategoryService.addCategory(categoryName, parentId);
//        } else {
//            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
//        }

        // 通过拦截器判断是否登陆以及拥有权限
        return iCategoryService.addCategory(categoryName, parentId);
    }

    @RequestMapping(value = "set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpServletRequest httpServletRequest, Integer categoryId, String categoryName) {
//        String loginToken = CookieUtill.readLoginToken(httpServletRequest);
//        if (StringUtils.isEmpty(loginToken)) {
//            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
//        }
//        String userJsonStr = RedisSharedPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obje(userJsonStr, User.class);
//        if(user == null) {
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
//        }
//        // 校验是否是管理员
//        if(iUserServcie.checkAdminRole(user).isSuccess()) {
//            // 是管理员
//            return iCategoryService.updateCategoryName(categoryId, categoryName);
//        } else {
//            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
//        }

        // 通过拦截器判断是否登陆以及拥有权限
        return iCategoryService.updateCategoryName(categoryId, categoryName);
    }

    @RequestMapping(value = "get_category.do")
    @ResponseBody
    public ServerResponse<List<Category>> getChildrenParallelCategory(HttpServletRequest httpServletRequest, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
//        String loginToken = CookieUtill.readLoginToken(httpServletRequest);
//        if (StringUtils.isEmpty(loginToken)) {
//            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
//        }
//        String userJsonStr = RedisSharedPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obje(userJsonStr, User.class);
//        if(user == null) {
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
//        }
//        // 校验是否是管理员
//        if(iUserServcie.checkAdminRole(user).isSuccess()) {
//            // 是管理员
//            return iCategoryService.getChildrenParallelCategory(categoryId);
//        } else {
//            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
//        }

        // 通过拦截器判断是否登陆以及拥有权限
        return iCategoryService.getChildrenParallelCategory(categoryId);
    }

    @RequestMapping(value = "get_deep_category.do")
    @ResponseBody
    public ServerResponse<List<Integer>> getCategoryAndDeepChildrenCategory(HttpServletRequest httpServletRequest, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
//        String loginToken = CookieUtill.readLoginToken(httpServletRequest);
//        if (StringUtils.isEmpty(loginToken)) {
//            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
//        }
//        String userJsonStr = RedisSharedPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obje(userJsonStr, User.class);
//        if(user == null) {
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
//        }
//        // 校验是否是管理员
//        if(iUserServcie.checkAdminRole(user).isSuccess()) {
//            //查询当前节点的id和递归子节点的id
//            return iCategoryService.selectChildrenAndCategory(categoryId);
//        } else {
//            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
//        }

        // 通过拦截器判断是否登陆以及拥有权限
        return iCategoryService.selectChildrenAndCategory(categoryId);
    }

}
