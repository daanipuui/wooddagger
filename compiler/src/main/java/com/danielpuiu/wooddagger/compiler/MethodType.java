package com.danielpuiu.wooddagger.compiler;

/**
 * Created: 20/04/2017
 * Author:  dpuiu
 */
enum MethodType {
    GET("get"),
    SET("set");

    String type;

    MethodType(String type) {
        this.type = type;
    }
}
