package com.qlgydx.mapper;

import com.qlgydx.pojo.Row;

/**
@author 岳贤翔
@date 2020/7/29 - 11:32
*/
public interface RowsMapper {
    int deleteByPrimaryKey(Integer id,String type);

    int insert(Row record,String type);

    int insertSelective(Row record,String type);

    Row selectByPrimaryKey(Integer id,String type);

    int updateByPrimaryKeySelective(Row record,String type);

    int updateByPrimaryKey(Row record,String type);
}