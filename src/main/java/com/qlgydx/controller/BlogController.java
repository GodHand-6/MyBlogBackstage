package com.qlgydx.controller;

import com.qlgydx.pojo.Document;
import com.qlgydx.pojo.Row;
import com.qlgydx.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.print.Doc;
import java.util.List;
import java.util.Map;

/**
 * @author 岳贤翔
 * @date 2020/7/29 - 21:08
 */
@RestController
@CrossOrigin//允许跨域访问
public class BlogController {

    @Autowired
    BlogService mainService;

    //主页
    @GetMapping("/main/{start}")
    public Map<String,Object> getMainLeft(@PathVariable("start")Integer start){
        if(start == null){
            //初始为第一页
            start = 0;
        }
        return mainService.mainColumn(start);
    }

    @GetMapping("/class")
    public Map<String,String> getMainRightForClass(){
        return mainService.getClasses();
    }

    @GetMapping("/class/{db}/{start}")
    public Map<String,Object> getDocByClass(@PathVariable("db")String  db, @PathVariable("start")Integer start){
        return mainService.getDocByClass(db,start);
    }

    @GetMapping("/recommend")
    public List<Document> getRecommendDocs(){
        return mainService.recommendColumn();
    }

    @GetMapping("/reduce")
    public  List<Map<String,Object>>  reduceByYear(){
        return mainService.reduceByDateYear();
    }

    @GetMapping("/doc/{db}/{id}")
    public Row getDoc(@PathVariable("db")String db, @PathVariable("id")Integer id){
        return mainService.getDoc(id,db);
    }

    @GetMapping("/search/{data}/{start}")
    public  Map<String,Object> search(@PathVariable("data")String data,@PathVariable("start") Integer start){
        return mainService.search(data,start);
    }
}
