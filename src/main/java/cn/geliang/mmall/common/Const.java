package cn.geliang.mmall.common;

import com.alipay.api.domain.PassInstanceDetail;
import com.alipay.api.domain.PublicAuditStatus;
import com.google.common.collect.Sets;
import com.sun.corba.se.impl.resolver.ORBDefaultInitRefResolverImpl;
import org.omg.CORBA.INTERNAL;

import java.util.Set;

public class Const {
    public static final String CURRENT_USER = "currentUser";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public static final String TOKEN_PREFIX = "token_";

    public interface RedisCacheExtime {
        int REDIS_SESSION_EXTIME = 60 * 30; // 30 分钟
    }

    public interface ProductListOrderBy {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }

    public interface Cart{
        int CHECKED = 1; //购物车选中状态
        int UN_CHECKED = 2; // 购物车未选中状态

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";

    }

    public interface Role{
        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }

    public interface AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    public enum ProductStatusEnum {
        ON_SALE("在线", 1);

        private String value;
        private int code;

        ProductStatusEnum(String value, int code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum OrderStatusEnum{
        CANCELED("已取消", 0),
        NO_PAY("未支付", 10),
        PAID("已付款", 20),
        SHIPPED("已发货", 40),
        ORDER_SUCCESS("订单完成", 50),
        ORDER_CLOSE("订单关闭", 60)
        ;
        private String value;
        private int code;

        OrderStatusEnum(String value, int code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static OrderStatusEnum codeOf(int code) {
            for (OrderStatusEnum orderStatusEnum : values()) {
                if(orderStatusEnum.getCode() == code) {
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("没有找到" + OrderStatusEnum.class.getName() + "对应的枚举");
        }
    }

    public enum PayPlatformEnum{
        ALIPAY(1,"支付宝")
        ;
        private int code;
        private String value;

        PayPlatformEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum PaymentTypeEnum{
        ONLINE_PAY(1, "在线支付")
        ;

        private int code;
        private String value;

        PaymentTypeEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static PaymentTypeEnum codeOf(int code) {
            for (PaymentTypeEnum paymentTypeEnum : values()) {
                if(paymentTypeEnum.getCode() == code) {
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("没有找到" + PaymentTypeEnum.class.getName() + "对应的枚举");
        }
    }
}
