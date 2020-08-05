package com.qlgydx;

import com.alibaba.fastjson.JSON;
import com.qlgydx.mapper.RowsMapper;
import com.qlgydx.pojo.Document;
import com.qlgydx.pojo.Row;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.sql.Date;

@SpringBootTest
class MyBlogBackstageApplicationTests {

    @Autowired
    RestHighLevelClient restHighLevelClient;



    @Test
    void contextLoads() throws IOException {
        GetRequest getRequest = new GetRequest("kuang_index","Iex1dHMBtm77R5_Xa2U3");
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(getResponse.getSourceAsString());
    }

    @Test
    void createIndex() throws IOException {
        CreateIndexRequest index = new CreateIndexRequest("blog_index");
        index.mapping("{\n" +
                "  \"properties\": {\n" +
                "    \"doc_id\": {\n" +
                "      \"type\": \"integer\"\n" +
                "    },\n" +
                "    \"doc_name\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\"\n" +
                "    },\n" +
                "    \"doc_type\": {\n" +
                "      \"type\": \"text\"\n" +
                "    },\n" +
                "    \"doc_date\": {\n" +
                "      \"type\": \"date\"\n" +
                "    }\n" +
                "  }\n" +
                "}", XContentType.JSON);
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(index, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }


    @Autowired
    RowsMapper rowsMapper;

    @Test
    void insertDocToDBAndInsertIndexToES() throws IOException {
        scanFilesWithRecursion("E:\\！\\java\\项目\\个人博客\\MyBlogBackstage\\src\\main\\resources\\blog",rowsMapper,restHighLevelClient);
    }

    public static void scanFilesWithRecursion(String folderPath,RowsMapper rowsMapper,RestHighLevelClient restHighLevelClient) throws IOException {
        File directory = new File(folderPath);
        if(directory.isDirectory()){
            File [] filelist = directory.listFiles();
            for(int i = 0; i < filelist.length; i ++){
                /**如果当前是文件夹，进入递归扫描文件夹**/
                if(filelist[i].isDirectory()){
                    /**递归扫描下面的文件夹**/
                    scanFilesWithRecursion(filelist[i].getAbsolutePath(),rowsMapper,restHighLevelClient);
                }
                /**非文件夹**/
                else{
                    Row row = new Row();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filelist[i].getPath()));
                    StringBuilder sb = new StringBuilder();
                    String s = null;
                    byte[] bytes = new byte[1024];
                    while(bufferedInputStream.read(bytes) > 0){
                        sb.append(new String(bytes));
                    }

                    row.setDoc(sb.toString());
                    row.setDate(new Date(System.currentTimeMillis()));
                    rowsMapper.insert(row, filelist[i].getParentFile().getName() + "documents");

                    IndexRequest indexRequest = new IndexRequest("blog_index");
                    Document document = new Document();
                    document.setDoc_id(row.getId());
                    document.setDoc_name(filelist[i].getName().split("\\.")[0]);
                    document.setDoc_type(filelist[i].getParentFile().getName());
                    document.setDoc_date(row.getDate());
                    indexRequest.timeout(TimeValue.timeValueSeconds(1));

                    indexRequest.source(JSON.toJSONString(document),XContentType.JSON);

                    IndexResponse index = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
                    System.out.println(index.status());
                }
            }
        }
    }


}
