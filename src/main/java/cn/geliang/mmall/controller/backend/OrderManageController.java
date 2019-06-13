package cn.geliang.mmall.controller.backend;

import cn.geliang.mmall.common.Const;
import cn.geliang.mmall.common.ResponseCode;
import cn.geliang.mmall.common.ServerResponse;
import cn.geliang.mmall.pojo.User;
import cn.geliang.mmall.service.IOrderService;
import cn.geliang.mmall.service.IUserServcie;
import cn.geliang.mmall.util.CookieUtill;
import cn.geliang.mmall.util.JsonUtil;
import cn.geliang.mmall.util.RedisPoolUtil;
import cn.geliang.mmall.util.RedisSharedPoolUtil;
import cn.geliang.mmall.vo.OrderVo;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/order/")
public class OrderManageController {

    @Autowired
    private IUserServcie iUserServcie;

    @Autowired
    private IOrderService iOrderService;

    /**
     * 后台获取订单列表
     * @param httpServletRequest
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpServletRequest httpServletRequest,
                                              @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        String loginToken = CookieUtill.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String userJsonStr = RedisSharedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obje(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }
        // 校验是否是管理员
        if (iUserServcie.checkAdminRole(user).isSuccess()) {
            // 是管理员
            return iOrderService.manageList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }

    }

    /**
     * 后台获取订单详情
     * @param httpServletRequest
     * @param orderNo
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> detail(HttpServletRequest httpServletRequest, Long orderNo) {

        String loginToken = CookieUtill.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String userJsonStr = RedisSharedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obje(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }
        // 校验是否是管理员
        if (iUserServcie.checkAdminRole(user).isSuccess()) {
            // 是管理员
            return iOrderService.manageSearch(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }

    }

    /**
     * 后台搜索订单
     * @param httpServletRequest
     * @param orderNo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(HttpServletRequest httpServletRequest, Long orderNo,
                                          @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        String loginToken = CookieUtill.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String userJsonStr = RedisSharedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obje(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }
        // 校验是否是管理员
        if (iUserServcie.checkAdminRole(user).isSuccess()) {
            // 是管理员
            return iOrderService.manageSearch(orderNo, pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }

    }

    /**
     * 订单发货
     * @param httpServletRequest
     * @param orderNo
     * @return
     */
    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse<String> orderSendGoods(HttpServletRequest httpServletRequest, Long orderNo) {

        String loginToken = CookieUtill.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String userJsonStr = RedisSharedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obje(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录, 请登录");
        }
        // 校验是否是管理员
        if (iUserServcie.checkAdminRole(user).isSuccess()) {
            // 是管理员
            return iOrderService.manageSendGoods(orderNo);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }

    }

}
