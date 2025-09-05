package com.module_service_insert.service.impl;

import com.module_service_insert.dao.IConfigClusterModuleDao;
import com.module_service_insert.dao.IConfigDao;
import com.module_service_insert.dao.impl.ConfigClusterModuleDaoImpl;
import com.module_service_insert.dao.impl.ConfigDaoImpl;
import com.module_service_insert.exception.DaoException;
import com.module_service_insert.model.ConfigClusterModuleModel;
import com.module_service_insert.model.ConfigModel;
import com.module_service_insert.model.tableData.ModuleTableData;
import com.module_service_insert.service.IConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ConfigServiceImpl implements IConfigService {
    private final IConfigDao configDao;
    private final IConfigClusterModuleDao configClusterModuleDao;
    private final Logger logger = LoggerFactory.getLogger(ConfigServiceImpl.class);

    public ConfigServiceImpl() {
        this.configDao = new ConfigDaoImpl();
        this.configClusterModuleDao = new ConfigClusterModuleDaoImpl();
    }

    @Override
    public List<ConfigClusterModuleModel> findAllConfigModule(long configId) throws DaoException {
        return configClusterModuleDao.findAllByConfig(configId);
    }

    @Override
    public List<ConfigModel> findAll() throws DaoException {
        return configDao.findAll();
    }

    @Override
    public long save(ConfigModel configModel, List<ModuleTableData> modulesOfCluster) throws DaoException {
        Connection conn = configDao.connection();
        try {
            // thêm mới config
            long configId = configDao.save(configModel, conn);
            // Thêm mới configClusterModule
            List<ConfigClusterModuleModel> configClusterModuleModels = new ArrayList<>();
            for(ModuleTableData moduleTableData : modulesOfCluster){
                configClusterModuleModels.add(new ConfigClusterModuleModel(
                    configId,
                    moduleTableData.getClusterModuleId(),
                    moduleTableData.getClusterName(),
                    "inactive",
                    moduleTableData.getModuleName()
                ));
            }
            configClusterModuleDao.save(configClusterModuleModels, conn);
            conn.commit();
            return configId;
        }
        catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                logger.error("Xảy ra lỗi khi rollback dữ liệu, details: ", e);
            }
            throw new DaoException("Lỗi khi thêm mới cấu hình chạy, vui lòng thử lại sau.");
        }
    }

    @Override
    public void update(ConfigModel configModel) throws DaoException {
        configDao.update(configModel);
    }

    @Override
    public void delete(List<Long> configIds) {
        Connection conn = configDao.connection();
        try {
            configClusterModuleDao.deleteByConfigId(configIds, conn);
            configDao.delete(configIds, conn);
        }
        catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                logger.error("Xảy ra lỗi khi rollback dữ liệu xóa config, details: ", e);
            }
            throw new DaoException("Lỗi khi xóa config, vui lòng thử lại sau.");
        }
    }
}
