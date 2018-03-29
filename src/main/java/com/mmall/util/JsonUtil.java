package com.mmall.util;

import com.mmall.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //对象所有字段全部列入
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);
        //忽略空bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        //取消默认转换timestamps形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        ///所有日期统一格式
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));
        //忽略在json字符传中存在，但在java对象中不存在对应属性的情况，防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> String obj2String(T obj) {
        if (obj == null)
            return null;
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error", e);
            return null;
        }
    }

    public static <T> String obj2StringPretty(T obj) {
        if (obj == null)
            return null;
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error", e);
            return null;
        }
    }

    public static <T> T string2Obj(String str, Class<T> clazz) {

        if (StringUtils.isEmpty(str) || clazz == null) return null;
        try {
            return clazz.equals(String.class) ? (T) str :
                    objectMapper.readValue(str, clazz);
        } catch (Exception e) {
            log.warn("Parse String to object error", e);
            return null;
        }


    }


    public static <T> T string2Obj(String str, TypeReference<T> typeReference) {

        if (StringUtils.isEmpty(str) || typeReference == null) return null;
        try {
            return typeReference.getType().equals(String.class) ? (T) str :
                    (T) objectMapper.readValue(str, typeReference);
        } catch (Exception e) {
            log.warn("Parse String to object error", e);
            return null;
        }
    }

    public static <T> T string2Obj(String str, Class<?> collectionClass, Class<?>... elementClasses) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);


        try {
            return objectMapper.readValue(str, javaType);
        } catch (Exception e) {
            log.warn("Parse String to object error", e);
            return null;
        }
    }


    public static void main(String[] args) {
        User u1 = new User();
        u1.setId(1);
        u1.setEmail("google@qq.com");
        String str = JsonUtil.obj2String(u1);
        String strprety = JsonUtil.obj2StringPretty(u1);

        log.info("user1Json:{}", str);
        log.info("userprety:{}", strprety);
        User user = JsonUtil.string2Obj(str, User.class);
        System.out.println("end");

        User u2 = new User();
        u2.setId(2);
        u2.setEmail("google@qq.com");
        u1.setId(1);
        u1.setEmail("google@qq.com");
        List<User> list = new ArrayList<>();
        list.add(u1);
        list.add(u2);
        String strlist = JsonUtil.obj2StringPretty(list);
        System.out.println(JsonUtil.obj2StringPretty(list));

        List<User> listjj = JsonUtil.string2Obj(strlist, List.class);
        List<User> list2 = JsonUtil.string2Obj(strlist, new TypeReference<List<User>>() {
        });
        List<User> list3 = JsonUtil.string2Obj(strlist, List.class, User.class);
        System.out.println("");

    }


}

