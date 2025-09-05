package com.module_service_insert.mapper.mapperResultSet;

import com.module_service_insert.mapper.RowMapper;
import com.module_service_insert.model.ClusterModel;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author Trọng Hướng
 */
public class ClusterMapper implements RowMapper<ClusterModel> {
    @Override
    public ClusterModel mapRow(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        ClusterModel clusterModel = new ClusterModel();
        for(int i=1;i<=columnCount;i++) {
            String columnName = metaData.getColumnName(i);
            if(columnName.equals("id")) {
                clusterModel.setId(rs.getLong(columnName));
            }
            if(columnName.equals("name")) {
                clusterModel.setName(rs.getString(columnName));
            }
            if(columnName.equals("status")) {
                clusterModel.setStatusStr(rs.getString(columnName));
            }
            if(columnName.equals("base_folder")) {
                clusterModel.setBaseFolder(rs.getString(columnName));
            }
        }
        return clusterModel;
    }
}
