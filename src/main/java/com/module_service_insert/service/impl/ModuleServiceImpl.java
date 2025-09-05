package com.module_service_insert.service.impl;

import com.module_service_insert.dao.impl.ModuleDaoImpl;
import com.module_service_insert.exception.DaoException;
import com.module_service_insert.model.ModuleModel;
import com.module_service_insert.service.IModuleService;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ModuleServiceImpl implements IModuleService {
    private final ModuleDaoImpl moduleDao;

    public ModuleServiceImpl() {
        moduleDao = new ModuleDaoImpl();
    }

    @Override
    public long save(ModuleModel moduleModel) throws DaoException {
        return moduleDao.save(moduleModel);
    }

    @Override
    public void update(ModuleModel moduleModel) throws DaoException {
        moduleDao.update(moduleModel);
    }

    @Override
    public void delete(long id) throws DaoException {
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        moduleDao.delete(ids);
    }

    @Override
    public List<ModuleModel> findAll() throws DaoException {
        return moduleDao.findAll();
    }
}
