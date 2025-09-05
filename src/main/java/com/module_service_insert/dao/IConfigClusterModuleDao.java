package com.module_service_insert.dao;

import com.module_service_insert.model.ConfigClusterModuleModel;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IConfigClusterModuleDao {
    List<Long> save(List<ConfigClusterModuleModel> configClusterModuleModels);
    List<Long> save(List<ConfigClusterModuleModel> configClusterModuleModels, Connection conn);
    List<ConfigClusterModuleModel> findAllByConfig(long configId);
    void update(ConfigClusterModuleModel configClusterModuleModels);
    void delete(List<Long> ids);
    void deleteByConfigId(List<Long> configIds, Connection conn);
    void deleteByClusterModuleId(List<Long> clusterModuleIds, Connection conn);
}
