package com.module_service_insert.mapper.mapperDataTable;

import com.module_service_insert.model.ModuleModel;
import com.module_service_insert.model.tableData.ModuleTableData;

/**
 * @author Trọng Hướng
 */
public class ModuleTableMapper {

    public static ModuleTableData toTableData(ModuleModel moduleModel){
        return new ModuleTableData(
                moduleModel.getName(),
                moduleModel.getCommand(),
                moduleModel.getDescription(),
                moduleModel.getCpuList(),
                moduleModel.getRunPath(),
                moduleModel.getConfigPath(),
                0, "", ""
        );
    }

    public static ModuleModel toModel(ModuleTableData moduleTableData){
        return new ModuleModel(
                System.currentTimeMillis(),
                moduleTableData.getModuleName(),
                moduleTableData.getDescription(),
                moduleTableData.getCommand()
        );
    }
}
