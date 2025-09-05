package com.module_service_insert.mapper.mapperResultSet;

import com.module_service_insert.mapper.RowMapper;
import com.module_service_insert.model.ConfigModel;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author Trọng Hướng
 */
public class ConfigMapper implements RowMapper<ConfigModel> {
    @Override
    public ConfigModel mapRow(ResultSet rs) throws SQLException {
        ConfigModel configModel = new ConfigModel();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        for(int i=1;i<=columnCount;i++) {
            String column = metaData.getColumnName(i);
            if("name".equals(column)) {
                configModel.setName(rs.getString(column));
            }
            if("id".equals(column)) {
                configModel.setId(rs.getLong(column));
            }
        }
        return configModel;
    }
}
