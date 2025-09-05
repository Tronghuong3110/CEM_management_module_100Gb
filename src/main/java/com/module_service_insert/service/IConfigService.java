package com.module_service_insert.service;

import com.module_service_insert.model.ConfigClusterModuleModel;
import com.module_service_insert.model.ConfigModel;
import com.module_service_insert.model.tableData.ConfigClusterModuleTableData;
import com.module_service_insert.model.tableData.ModuleTableData;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IConfigService {
    List<ConfigClusterModuleModel> findAllConfigModule(long configId);
    List<ConfigModel> findAll();
    long save(ConfigModel configModel, List<ModuleTableData> modulesOfCluster);
    void update(ConfigModel configModel);
    void delete(List<Long> configIds);
}
