package cn.geliang.mmall.util;

import cn.geliang.mmall.pojo.User;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * json通用工具类
 */
@Slf4j
public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 对象所有字段全部读入
        objectMapper.setSerializationInclusion(Include.ALWAYS);

        // 取消默认timestamps形式
        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);

        // 忽略空bean转换错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // 所有日期格式统一为标准格式
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STAND_FORMAT));

        // 忽略在json中存在，但是在java对象中不存在对应属性的情况。防止错误
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> String obj2String(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object ot String error", e);
        }
        return null;
    }

    /**
     * 返回格式化好的字符串
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String obj2StringPretty(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error", e);
        }
        return null;
    }

    public static <T> T string2Obje(String str, Class<T> clazz) {
        if (StringUtils.isEmpty(str) || clazz == null) {
            return null;
        }

        try {
            return clazz.equals(String.class) ? (T) str : objectMapper.readValue(str, clazz);
        } catch (Exception e) {
            log.warn("Parse String to object error", e);
            return null;
        }
    }

    public static <T> T string2Obje(String str, Class<?> collectionClass, Class<?>... elementClass) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClass);
        try {
            return objectMapper.readValue(str, javaType);
        } catch (Exception e) {
            log.warn("Parse String to object error", e);
            return null;
        }
    }

    public static <T> T string2Obje(String str, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(str) || typeReference == null) {
            return null;
        }

        try {
            return (T) (typeReference.getType().equals(String.class) ? str : objectMapper.readValue(str, typeReference));
        } catch (Exception e) {
            log.warn("Parse String to object error", e);
            return null;
        }
    }

    public static void main(String[] args) {
        User user1 = new User();
        user1.setId(1);
        user1.setUsername("张三");
        String user1Json = obj2String(user1);
        String user1JsonPretty = obj2StringPretty(user1);
        log.info("user1 {}", user1Json);
        log.info("user1Pretty {}", user1JsonPretty);

        User user2 = string2Obje(user1Json, User.class);

        User user3 = new User();
        user3.setId(2);
        user3.setUsername("李四");

        List<User> userList = Lists.newArrayList();
        userList.add(user1);
        userList.add(user3);
        String userListJson = obj2StringPretty(userList);
        log.info("=================");
        log.info("userList {}", userListJson);

        List<User> userListObj = string2Obje(userListJson, new TypeReference<List<User>>() {
        });

        log.info("=================");
        List<User> userListObj2 = string2Obje(userListJson, List.class, User.class);

        System.out.println("end...");

    }

}
