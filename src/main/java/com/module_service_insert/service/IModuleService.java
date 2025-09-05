package com.module_service_insert.service;

import com.module_service_insert.model.ModuleModel;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IModuleService {
    long save(ModuleModel moduleModel);
    void update(ModuleModel moduleModel);
    void delete(long id);
    List<ModuleModel> findAll();
}
