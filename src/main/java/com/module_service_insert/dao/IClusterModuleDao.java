package com.module_service_insert.dao;

import com.module_service_insert.model.ClusterModuleModel;

import java.sql.Connection;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IClusterModuleDao {
    List<Long> save(List<ClusterModuleModel> clusterModuleModels);
    List<Long> save(List<ClusterModuleModel> clusterModuleModels, Connection conn);
    List<ClusterModuleModel> findAllByCluster(long clusterId);
    void update(ClusterModuleModel clusterModuleModel);
    void update(ClusterModuleModel clusterModuleModel, Connection conn);
    void deleteById(List<Long> ids);
    void deleteById(List<Long> ids, Connection conn);
    void deleteByCluster(List<Long> clusterIds, Connection conn);
}
