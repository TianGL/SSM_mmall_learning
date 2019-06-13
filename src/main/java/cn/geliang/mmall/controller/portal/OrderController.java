package cn.geliang.mmall.controller.portal;

import cn.geliang.mmall.common.Const;
import cn.geliang.mmall.common.ResponseCode;
import cn.geliang.mmall.common.ServerResponse;
import cn.geliang.mmall.pojo.User;
import cn.geliang.mmall.service.IOrderService;
import cn.geliang.mmall.util.CookieUtill;
import cn.geliang.mmall.util.JsonUtil;
import cn.geliang.mmall.util.RedisPoolUtil;
import cn.geliang.mmall.util.RedisSharedPoolUtil;
import cn.geliang.mmall.vo.OrderVo;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/order/")
@Slf4j
public class OrderController {


    @Autowired
    IOrderService iOrderService;

    /**
     * 创建订单
     *
     * @param httpServletRequest
     * @param shippingId
     * @return
     */
    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpServletRequest httpServletRequest, Integer shippingId) {
        String loginToken = CookieUtill.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String userJsonStr = RedisSharedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obje(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId(), shippingId);
    }

    /**
     * 取消订单
     *
     * @param httpServletRequest
     * @param orderNo
     * @return
     */
    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpServletRequest httpServletRequest, Long orderNo) {
        String loginToken = CookieUtill.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String userJsonStr = RedisSharedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obje(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancel(user.getId(), orderNo);
    }

    /**
     * 获取订单中商品明细
     *
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtill.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String userJsonStr = RedisSharedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obje(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

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
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderDetail(user.getId(), orderNo);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpServletRequest httpServletRequest, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        String loginToken = CookieUtill.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String userJsonStr = RedisSharedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obje(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(user.getId(), pageNum, pageSize);
    }


    /**
     * 支付
     *
     * @param httpServletRequest
     * @param orderNo
     * @param request
     * @return
     */
    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse<Map<String, String>> pay(HttpServletRequest httpServletRequest, Long orderNo, HttpServletRequest request) {
        String loginToken = CookieUtill.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String userJsonStr = RedisSharedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obje(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNo, user.getId(), path);
    }

    /**
     * 支付宝支付回调
     *
     * @param request
     * @return
     */
    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallBack(HttpServletRequest request) {

        Map<String, String> params = Maps.newHashMap();

        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; ++i) {
                valueStr = (i == (values.length - 1)) ? (valueStr + values[i]) : (valueStr + ",");
            }
            params.put(name, valueStr);
        }
        log.info("支付宝回调, sign:{}, trade_status: {}, 参数: {}", params.get("sign"), params.get("trade_status"), params.toString());

        // 验证回调的正确性，是否是支付宝发的，还要避免重复发送
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());

            if (!alipayRSACheckedV2) {
                return ServerResponse.createByErrorMessage("非法请求！！！");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝验证回调异常");
            e.printStackTrace();
        }

        //todo 验证各种数据


        ServerResponse serverResponse = iOrderService.aliCallback(params);
        if (serverResponse.isSuccess()) {
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    /**
     * 获取订单支付状态
     *
     * @param httpServletRequest
     * @param orderNo
     * @return
     */
    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPay(HttpServletRequest httpServletRequest, Long orderNo) {
        String loginToken = CookieUtill.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String userJsonStr = RedisSharedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obje(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(), orderNo);
        if (serverResponse.isSuccess()) {
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }
}
