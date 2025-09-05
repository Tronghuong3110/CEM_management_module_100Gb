package com.module_service_insert.mapper.mapperResultSet;

import com.module_service_insert.mapper.RowMapper;
import com.module_service_insert.model.ConfigClusterModuleModel;
import com.module_service_insert.model.ModuleModel;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author Trọng Hướng
 */
public class ConfigClusterModuleMapper implements RowMapper<ConfigClusterModuleModel> {
    @Override
    public ConfigClusterModuleModel mapRow(ResultSet rs) throws SQLException {
        ConfigClusterModuleModel configClusterModuleModel = new ConfigClusterModuleModel();
        ModuleModel moduleModel = new ModuleModel();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        for(int i=1;i<=columnCount;i++) {
            String column =  metaData.getColumnName(i);
            if(column.equals("id")) {
                configClusterModuleModel.setId(rs.getLong(column));
            }
            if(column.equals("cluster_name")) {
                configClusterModuleModel.setClusterName(rs.getString(column));
            }
            if(column.equals("module_name")) {
                moduleModel.setName(rs.getString(column));
            }
            if(column.equals("cpu_list")) {
                moduleModel.setCpuList(rs.getString(column));
            }
            if(column.equals("command")) {
                moduleModel.setCommand(rs.getString(column));
            }
            if(column.equals("log_path")) {
                configClusterModuleModel.setLogPath(rs.getString(column));
            }
            if(column.equals("status")) {
                configClusterModuleModel.setStatus(rs.getString(column));
            }
            if(column.equals("log")) {
                configClusterModuleModel.setLog(rs.getString(column));
            }
        }
        configClusterModuleModel.setModuleModel(moduleModel);
        return configClusterModuleModel;
    }
}
