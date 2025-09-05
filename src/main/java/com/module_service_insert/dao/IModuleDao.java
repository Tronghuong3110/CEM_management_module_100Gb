package com.module_service_insert.dao;

import com.module_service_insert.model.ModuleModel;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IModuleDao {
    List<ModuleModel> findAll();
    long save(ModuleModel moduleModel);
    void update(ModuleModel moduleModel);
    void delete(List<Long> ids);
}
