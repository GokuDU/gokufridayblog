package com.guo.common.lang;

import lombok.Data;
import java.io.Serializable;

@Data
public class Result implements Serializable {

    // 0: 成功  -1: 失败
    private int status;
    // 提示信息
    private String msg;
    // 返回结果
    private Object data;
    private String action;

    // 成功返回结果
    public static Result success() {
        return success("操作成功",null);
    }

    public static Result success(Object data) {
        return success("操作成功", data);
    }

    public static Result success(String msg,Object data) {
        Result result = new Result();
        result.status = 0;
        result.msg = msg;
        result.data = data;
        return result;
    }



    // 失败返回结果
    public static Result fail(String msg) {
        Result result = new Result();
        result.status = -1;
        result.msg = msg;
        result.data = null;
        return result;
    }

    public static Result fail() {
        return fail("操作失败");
    }

    public Result action(String action) {
        this.action =action;
        return this;
    }

}
