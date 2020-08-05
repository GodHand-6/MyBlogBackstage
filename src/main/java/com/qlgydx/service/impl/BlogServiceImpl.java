package com.qlgydx.service.impl;

import com.qlgydx.mapper.RowsMapper;
import com.qlgydx.pojo.Document;
import com.qlgydx.pojo.Row;
import com.qlgydx.service.BlogService;
import com.qlgydx.utils.ClassUtils;
import com.qlgydx.utils.PageUtil;
import org.apache.lucene.queryparser.surround.query.SrndPrefixQuery;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.metrics.ParsedTopHits;
import org.elasticsearch.search.aggregations.metrics.TopHitsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 岳贤翔
 * @date 2020/7/29 - 21:28
 */
@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    RowsMapper rowsMapper;

    @Resource(name = "classificationMapping")
    HashMap<String,String> classificationMapping;

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public Map<String,Object> mainColumn(Integer start) {

        SearchRequest searchRequest = new SearchRequest("blog_index");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.from(start);
        searchSourceBuilder.size(PageUtil.pageSize);
        searchSourceBuilder.timeout(TimeValue.timeValueSeconds(3));
        searchSourceBuilder.sort("doc_date");

        searchRequest.source(searchSourceBuilder);
        
        List<Document> list = new ArrayList<>();
        Map<String,Object> result = new HashMap<>();
        try{
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            for(SearchHit hit : searchResponse.getHits().getHits()){
                Map<String, Object> map = hit.getSourceAsMap();
                list.add(new Document(
                        Integer.valueOf(map.get("doc_id").toString()),
                        map.get("doc_name").toString(),
                        map.get("doc_type").toString(),
                        new Date(Long.valueOf(map.get("doc_date").toString()))));
            }
            result.put("list",list);
            result.put("counts",searchResponse.getHits().getTotalHits().value);
        }catch (Exception ex){
            ex.printStackTrace();
            System.out.println("出错了！！！！！");
        }
        
        return result;
    }

    @Override
    public Map<String,String>  getClasses() {
        return ClassUtils.classes;
    }

    @Override
    public Map<String,Object> getDocByClass(String db, Integer start) {
        SearchRequest searchRequest = new SearchRequest("blog_index");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.timeout(TimeValue.timeValueSeconds(3));
        searchSourceBuilder.from(start);
        searchSourceBuilder.size(PageUtil.pageSize);
        searchSourceBuilder.sort("doc_date");

        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("doc_type", db);

        searchSourceBuilder.query(matchQueryBuilder);

        searchRequest.source(searchSourceBuilder);

        List<Document> list = new ArrayList<>();
        Map<String,Object> result = new HashMap<>();
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            for(SearchHit hit : searchResponse.getHits().getHits()){
                Map<String, Object> map = hit.getSourceAsMap();
                list.add(new Document(
                        Integer.valueOf(map.get("doc_id").toString()),
                        map.get("doc_name").toString(),
                        map.get("doc_type").toString(),
                        new Date(Long.valueOf(map.get("doc_date").toString()))));
            }
            result.put("list",list);
            result.put("counts",searchResponse.getHits().getTotalHits().value);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("出错了！！！！！");
        }

        return result;
    }

    @Override
    public void tagColumn() {

    }

    @Override
    public List<Document> recommendColumn() {
        List<Document> list = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest("blog_index");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.size(4);
        searchSourceBuilder.timeout(TimeValue.timeValueSeconds(3));
        searchSourceBuilder.sort("doc_date");

        searchRequest.source(searchSourceBuilder);

        try{
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            for(SearchHit hit : searchResponse.getHits().getHits()){
                Map<String, Object> map = hit.getSourceAsMap();
                list.add(new Document(
                        Integer.valueOf(map.get("doc_id").toString()),
                        map.get("doc_name").toString(),
                        map.get("doc_type").toString(),
                        new Date(Long.valueOf(map.get("doc_date").toString()))));
            }
        }catch (Exception ex){
            ex.printStackTrace();
            System.out.println("出错了！！！！！");
        }
        return list;
    }

    @Override
    public  List<Map<String,Object>>  reduceByDateYear() {
        SearchRequest searchRequest = new SearchRequest("blog_index");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.timeout(TimeValue.timeValueSeconds(2));
        searchSourceBuilder.sort("doc_date");

        DateHistogramAggregationBuilder dateHistogramAggregationBuilder = AggregationBuilders.dateHistogram("group_by_date");
        dateHistogramAggregationBuilder.field("doc_date");
        dateHistogramAggregationBuilder.calendarInterval(DateHistogramInterval.YEAR);

        TopHitsAggregationBuilder topHitsAggregationBuilder = AggregationBuilders.topHits("group_get");
        topHitsAggregationBuilder.size(20);
        dateHistogramAggregationBuilder.subAggregation(topHitsAggregationBuilder);

        searchSourceBuilder.size(0);
        searchSourceBuilder.aggregation(dateHistogramAggregationBuilder);

        searchRequest.source(searchSourceBuilder);


        List<Map<String,Object>> result = new ArrayList<>();

        Map<String,Object> hashMap = null;
        List<Document> list = null;
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);

            ParsedDateHistogram parsedDateHistogram = response.getAggregations().get("group_by_date");
            List<? extends Histogram.Bucket> buckets = parsedDateHistogram.getBuckets();
            for (int i = 0; i < buckets.size(); i++) {
                list = new ArrayList<>();
                hashMap = new HashMap<>();
                ParsedTopHits hits = buckets.get(i).getAggregations().get("group_get");
               
                for(SearchHit hit : hits.getHits().getHits()){
                    Map<String, Object> map = hit.getSourceAsMap();
                    list.add(new Document(
                            Integer.valueOf(map.get("doc_id").toString()),
                            map.get("doc_name").toString(),
                            map.get("doc_type").toString(),
                            new Date(Long.valueOf(map.get("doc_date").toString()))));
                }

                hashMap.put("list",list);
                hashMap.put("counts",hits.getHits().getTotalHits().value);
                hashMap.put("year",buckets.get(i).getKeyAsString().split("-")[0]);

                result.add(hashMap);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("出错了！！！！！");
        }

        return result;
    }

    @Override
    public Row getDoc(Integer id, String type) {
        return rowsMapper.selectByPrimaryKey(id,type + PageUtil.suffix);
    }

    @Override
    public  Map<String,Object> search(String data,Integer start){
        SearchRequest searchRequest = new SearchRequest("blog_index");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.timeout(TimeValue.timeValueSeconds(1));
        searchSourceBuilder.from(start);
        searchSourceBuilder.size(PageUtil.pageSize);

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("doc_name");
        highlightBuilder.requireFieldMatch(false);//多个高亮显示！！！
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");

        searchSourceBuilder.highlighter(highlightBuilder);

        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("doc_name", data);

        searchSourceBuilder.query(matchQueryBuilder);

        searchRequest.source(searchSourceBuilder);

        Map<String,Object> result = new HashMap<>();
        List<Document> list = new ArrayList<>();

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //解析高亮的字段,将原来的字段换为我们高亮的字段即可
            for(SearchHit hit : searchResponse.getHits().getHits()){
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField doc_name = highlightFields.get("doc_name");
                Map<String, Object> map = hit.getSourceAsMap();
                if(doc_name != null){
                    Text[] fragments = doc_name.getFragments();
                    String newName = "";
                    for(Text text : fragments){
                        if(text != null){
                            newName += text;
                        }
                    }
                    map.put("doc_name",newName);
                }
                list.add(new Document(
                        Integer.valueOf(map.get("doc_id").toString()),
                        map.get("doc_name").toString(),
                        map.get("doc_type").toString(),
                        new Date(Long.valueOf(map.get("doc_date").toString()))));
            }

            result.put("list",list);
            result.put("counts",searchResponse.getHits().getTotalHits().value);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("!!!!!!");
        }
        return result;
    }
}

