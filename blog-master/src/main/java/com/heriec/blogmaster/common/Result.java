package com.heriec.blogmaster.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Result implements Serializable {

    int code;
    String msg;
    Object data;
    // 错误只有200和400
    public static Result success(String msg, Object data) {
        return new Result(200, msg, data);
    }

    public static Result success(Object data) {
        return new Result(200, "操作成功", data);
    }

    // 失败
    public static Result fail(int code, String msg, Object data) {
        return new Result(code, msg, data);
    }

    public static Result fail(String msg, Object data) {
        return new Result(400, msg, data);
    }

    public static Result fail(String message) {
        return new Result(400, message, null);
    }


}
