package org.example.common.converter;

import org.apache.commons.collections4.CollectionUtils;
import org.example.common.BaseDb;
import org.example.common.dao.SearchTableDao;
import org.example.common.model.SearchTable;

import java.sql.SQLException;
import java.util.List;

/**
 * <pre>
 *     系统在读取到MySqlColumnInfo模型的时候，自动把mysql类型转换为elasticsearch的类型
 *     当前类用来使用这个模型构建mapping映射
 *
 * </pre>
 */
public class ColumnMappingUtils {


    public static void buildMapping(Integer schemaId, BaseDb defaultDb) throws SQLException {
        //根据方案id拿到所有的表
        SearchTableDao searchTableDao = new SearchTableDao("",defaultDb);
        //这是一个已经加工好的树形结构
        List<SearchTable> searchTableList = searchTableDao.queryBySchemaId(schemaId);
        //根据表取到对应的列
        if(CollectionUtils.isEmpty(searchTableList)){
            return;
        }
        //递归遍历这棵树，并构建mapping


    }


}
