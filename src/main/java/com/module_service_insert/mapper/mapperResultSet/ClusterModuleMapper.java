package com.module_service_insert.mapper.mapperResultSet;

import com.module_service_insert.mapper.RowMapper;
import com.module_service_insert.model.ClusterModuleModel;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author Trọng Hướng
 */
public class ClusterModuleMapper implements RowMapper<ClusterModuleModel> {
    @Override
    public ClusterModuleModel mapRow(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        ClusterModuleModel clusterModuleModel = new ClusterModuleModel();
        for(int i = 1; i <= columnCount; i++){
            String columnName = metaData.getColumnLabel(i);
            if("id".equals(columnName)){
                clusterModuleModel.setId(rs.getLong(columnName));
                continue;
            }
            if("module_name".equals(columnName)){ // module_name
                clusterModuleModel.setModuleName(rs.getString(columnName));
                continue;
            }
            if("cpu_list".equals(columnName)){
                clusterModuleModel.setCpuList(rs.getString(columnName));
                continue;
            }
            if("run_path".equals(columnName)){
                clusterModuleModel.setRunPath(rs.getString(columnName));
                continue;
            }
            if("config_path".equals(columnName)){
                clusterModuleModel.setConfigPath(rs.getString(columnName));
                continue;
            }
            if("command".equals(columnName)){
                clusterModuleModel.setCommand(rs.getString(columnName));
            }
        }
        return clusterModuleModel;
    }
}
