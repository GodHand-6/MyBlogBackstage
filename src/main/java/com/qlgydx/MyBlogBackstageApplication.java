package com.qlgydx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
@SpringBootApplication
/**
 * 在自己学习搭建SpringBoot的项目中并没有使用到数据库，但在启动的时候报没有找到数据源，网上查了一下发现原因是SpringBoot项目启动时会自动加载datasourceConfig配置
 *
 * 经过尝试总结下面是解决方案：
 *
 * 1.在yml文件配置数据库信息
 * 2.排除引入带有数据库的包
 * 3.使用注解启动类的@EnableAutoConfiguration或@SpringBootApplication中添加exclude = {DataSourceAutoConfiguration.class}，排除此类的autoconfig。
 */
@MapperScan("com.qlgydx.mapper")
public class MyBlogBackstageApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyBlogBackstageApplication.class, args);
    }

}
