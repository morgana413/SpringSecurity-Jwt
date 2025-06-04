package org.example.myprojectbackend.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

public record RestBean<T>(int code, String msg, T data) {
    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(200,"请求成功",data);
    }

    public static <T> RestBean<T> success(){
        return success(null);
    }

    public static <T> RestBean<T> failure(String msg) {
        return new RestBean<>(403,msg,null);
    }



    public String asJsonString() {
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
}
