package com.module_service_insert.service;

import com.module_service_insert.model.ClusterModel;
import com.module_service_insert.model.ClusterModuleModel;
import com.module_service_insert.model.tableData.ModuleTableData;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IClusterService {
    List<ClusterModel> findAll();
    long save(ClusterModel clusterModel, List<ModuleTableData> moduleTableDatas);
    void update(ClusterModel newClusterModel, List<ModuleTableData> updatedModulesOfCluster, List<Long> moduleIdsToDelete);
    void delete(long id, List<Long> clusterModuleIds);
    List<ClusterModuleModel> findAllByCluster(long clusterId);
}
