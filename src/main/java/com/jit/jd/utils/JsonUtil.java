package com.jit.jd.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    /*
    将对象转为json字符串
     */

    public static String object2JsonStr(Object obj){
        try{
            return objectMapper.writeValueAsString(obj);
        }catch (JsonProcessingException e){
            //打印异常消息
            e.printStackTrace();
        }
        return null;
    }

    /*
    将字符串转化为对象
     */
    public static <T> T  jsonStr2Object(String jsonStr, Class<T> clazz){
        try{
            return objectMapper.readValue(jsonStr.getBytes("UTF-8"), clazz);
        }catch (JsonParseException e){
            e.printStackTrace();
        }catch (JsonMappingException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
    /*
    将json数据转化为pojo对象list
     */
    public static <T> List<T> jsonToList(String jsonStr, Class<T> beantype){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, beantype);
        try{
            List<T> list = objectMapper.readValue(jsonStr, javaType);
            return list;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
