package com.qlgydx.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
@author 岳贤翔
@date 2020/7/29 - 11:32
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Row {

    private Integer id;
    private String doc;
    private Date date;
    private String doc_name;
}