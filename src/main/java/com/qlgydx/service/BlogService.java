package com.qlgydx.service;

import com.qlgydx.pojo.Document;
import com.qlgydx.pojo.Row;

import java.util.List;
import java.util.Map;

/**
 * @author 岳贤翔
 * @date 2020/7/29 - 21:27
 */
public interface BlogService {

    public Map<String, Object> mainColumn(Integer start);

    public Map<String, String> getClasses();

    public void tagColumn();

    public List<Document> recommendColumn();

    public Map<String, Object> getDocByClass(String db, Integer start);

    public List<Map<String, Object>> reduceByDateYear();

    public Row getDoc(Integer id, String type);

    public  Map<String,Object> search(String data,Integer start);

}
