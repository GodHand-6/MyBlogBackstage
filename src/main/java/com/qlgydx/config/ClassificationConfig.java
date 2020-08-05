package com.qlgydx.config;

import jdk.nashorn.internal.ir.ReturnNode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author 岳贤翔
 * @date 2020/7/29 - 21:19
 */
@Configuration
public class ClassificationConfig {

    @Bean
    public HashMap<String,String> classificationMapping(){
        HashMap<String ,String> hashMap = new HashMap<>();
        hashMap.put("MyDate","我的日记");
        hashMap.put("Environment","环境搭建");
        hashMap.put("Record","踩坑记录");
        hashMap.put("Study","学习笔记");
        return hashMap;
    }
}
