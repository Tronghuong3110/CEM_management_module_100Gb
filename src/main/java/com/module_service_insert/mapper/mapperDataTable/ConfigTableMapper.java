package com.module_service_insert.mapper.mapperDataTable;

import com.module_service_insert.model.ConfigClusterModuleModel;
import com.module_service_insert.model.tableData.ConfigClusterModuleTableData;

/**
 * @author Trọng Hướng
 */
public class ConfigTableMapper {

    public static ConfigClusterModuleTableData toModelTableData(ConfigClusterModuleModel configClusterModuleModel) {
        return new ConfigClusterModuleTableData(
                configClusterModuleModel.getModuleModel().getName(),
                configClusterModuleModel.getModuleModel().getCommand(),
                configClusterModuleModel.getModuleModel().getCpuList(),
                configClusterModuleModel.getLogPath(),
                configClusterModuleModel.getStatus(),
                configClusterModuleModel.getLog(),
                configClusterModuleModel.getClusterName(),
                configClusterModuleModel.getpId()
        );
    }

}
