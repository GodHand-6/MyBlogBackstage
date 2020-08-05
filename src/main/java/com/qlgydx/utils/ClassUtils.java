package com.qlgydx.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 岳贤翔
 * @date 2020/7/30 - 22:40
 */
public class ClassUtils {
    public static Map<String,String> classes;
    public static String folderPath = "E:\\！\\java\\项目\\个人博客\\MyBlogBackstage\\src\\main\\resources\\blog";

    static{
        classes = new HashMap<>();
        classes.put("Study","学习笔记");
        classes.put("MyDate","我的日记");
        classes.put("Record","踩坑记录");
        classes.put("Environment","环境搭建");
    }
}
