package com.module_service_insert.mapper.mapperResultSet;

import com.module_service_insert.mapper.RowMapper;
import com.module_service_insert.model.ModuleModel;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author Trọng Hướng
 */
public class ModuleMapper implements RowMapper<ModuleModel> {

    @Override
    public ModuleModel mapRow(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        ModuleModel moduleModel = new ModuleModel();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            if(columnName.equals("id")) {
                moduleModel.setId(rs.getLong(columnName));
            }
            if(columnName.equals("cpu_list")) {
                moduleModel.setCpuList(rs.getString(columnName));
            }
            if(columnName.equals("run_path")) {
                moduleModel.setRunPath(rs.getString(columnName));
            }
            if(columnName.equals("config_path")) {
                moduleModel.setConfigPath(rs.getString(columnName));
            }
            if(columnName.equals("command")) {
                moduleModel.setCommand(rs.getString(columnName));
            }
            if("name".equals(columnName)) {
                moduleModel.setName(rs.getString(columnName));
            }
            if("description".equals(columnName)) {
                moduleModel.setDescription(rs.getString(columnName));
            }
        }
        return moduleModel;
    }
}
