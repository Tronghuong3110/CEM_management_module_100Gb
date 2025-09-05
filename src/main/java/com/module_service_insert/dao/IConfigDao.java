package com.module_service_insert.dao;

import com.module_service_insert.model.ConfigModel;

import java.sql.Connection;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IConfigDao {
    List<ConfigModel> findAll();
    long save(ConfigModel configModel);
    long save(ConfigModel configModel, Connection conn);
    void update(ConfigModel configModel);
    void delete(List<Long> configIds, Connection conn);
    Connection connection();
}
