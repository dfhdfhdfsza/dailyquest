package com.dailyquest.dailyquest.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String message;
    private  final  String code;

    public static <T> ApiResponse<T> data(T data){
        return new ApiResponse<>(true,data,null,null);
    }
    public static <T> ApiResponse<T> success(){
        return new ApiResponse<>(true,null,null,null);
    }
    public static <T> ApiResponse<T> message(String msg){
        return new ApiResponse<>(true, null, msg, null);
    }
    public static <T> ApiResponse<T> error(String code,String msg){
        return  new ApiResponse<>(false,null,msg,code);
    }
}
