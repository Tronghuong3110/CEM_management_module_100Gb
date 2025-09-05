package com.module_service_insert.dao;

import com.module_service_insert.model.ClusterModel;

import java.sql.Connection;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IClusterDao {
    long save(ClusterModel clusterModel);
    long save(ClusterModel clusterModel, Connection conn);
    List<ClusterModel> findAll();
    void update(ClusterModel clusterModel);
    void update(ClusterModel clusterModel, Connection conn);
    void delete(List<Long> ids);
    Connection connection();
}
