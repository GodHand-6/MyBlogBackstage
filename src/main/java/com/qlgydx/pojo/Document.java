package com.qlgydx.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * @author 岳贤翔
 * @date 2020/7/29 - 12:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Document {
    private Integer doc_id;
    private String doc_name;
    private String doc_type;
    private Date doc_date;
}
