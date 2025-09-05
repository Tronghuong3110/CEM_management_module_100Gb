package com.module_service_insert.service.impl;

import com.module_service_insert.dao.IClusterDao;
import com.module_service_insert.dao.IClusterModuleDao;
import com.module_service_insert.dao.IConfigClusterModuleDao;
import com.module_service_insert.dao.impl.ClusterDaoImpl;
import com.module_service_insert.dao.impl.ClusterModuleDaoImpl;
import com.module_service_insert.dao.impl.ConfigClusterModuleDaoImpl;
import com.module_service_insert.exception.DaoException;
import com.module_service_insert.model.ClusterModel;
import com.module_service_insert.model.ClusterModuleModel;
import com.module_service_insert.model.tableData.ModuleTableData;
import com.module_service_insert.service.IClusterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ClusterServiceImpl implements IClusterService {
    private final Logger logger = LoggerFactory.getLogger(ClusterServiceImpl.class);
    private final IClusterDao clusterDao;
    private final IClusterModuleDao clusterModuleDao;
    private final IConfigClusterModuleDao configClusterModuleDao;

    public ClusterServiceImpl() {
        clusterDao = new ClusterDaoImpl();
        clusterModuleDao = new ClusterModuleDaoImpl();
        configClusterModuleDao = new ConfigClusterModuleDaoImpl();
    }

    @Override
    public List<ClusterModel> findAll() throws DaoException {
        return clusterDao.findAll();
    }

    @Override
    public long save(ClusterModel clusterModel, List<ModuleTableData> moduleTableDatas) throws DaoException{
        Long clusterId = null;
        Connection conn = clusterDao.connection();
        try {
            clusterId = clusterDao.save(clusterModel, conn);
            List<ClusterModuleModel> clusterModuleModes = new ArrayList<>();
            for(ModuleTableData moduleTableDataModule : moduleTableDatas) {
                ClusterModuleModel clusterModuleModel = new ClusterModuleModel(
                        moduleTableDataModule.getClusterModuleId(),
                        clusterId,
                        moduleTableDataModule.getModuleId(),
                        moduleTableDataModule.getCpuList(),
                        moduleTableDataModule.getRunFolder(),
                        moduleTableDataModule.getConfigPath(),
                        moduleTableDataModule.getCommand(),
                        moduleTableDataModule.getModuleName(),
                        moduleTableDataModule.getLogPath()
                );
                if(moduleTableDataModule.getClusterModuleId() != -1 && moduleTableDataModule.isChange()) {
                    clusterModuleDao.update(clusterModuleModel, conn);
                    continue;
                }
                clusterModuleModes.add(clusterModuleModel);
            }
            if(!clusterModuleModes.isEmpty()) {
                clusterModuleDao.save(clusterModuleModes, conn);
            }
            conn.commit();
            return clusterId;
        }
        catch (DaoException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            logger.error("Thêm mới cụm và module bị lỗi, rollback cụm đã lưu cluster_id = {}, details: ", clusterId, e);
            throw e;
        } catch (SQLException e) {
            throw new DaoException("Lỗi khi thêm mới module vào cụm, vui lòng thử lại sau.");
        }
    }

    @Override
    public void update(ClusterModel newClusterModel, List<ModuleTableData> upSertdModulesOfCluster, List<Long> moduleIdsToDelete){
        Connection conn = clusterDao.connection();
        try {
            List<ClusterModuleModel> insertNewModuleOfCluster = new ArrayList<>();
            for(ModuleTableData moduleTableDataModule : upSertdModulesOfCluster) {
                ClusterModuleModel clusterModuleModel = new ClusterModuleModel(
                        moduleTableDataModule.getClusterModuleId(),
                        newClusterModel.getId(),
                        moduleTableDataModule.getModuleId(),
                        moduleTableDataModule.getCpuList(),
                        moduleTableDataModule.getRunFolder(),
                        moduleTableDataModule.getConfigPath(),
                        moduleTableDataModule.getCommand(),
                        moduleTableDataModule.getModuleName(),
                        moduleTableDataModule.getLogPath()
                );
                if(moduleTableDataModule.getClusterModuleId() != -1 && moduleTableDataModule.isChange()) {
                    clusterModuleDao.update(clusterModuleModel, conn);
                }
                else if(moduleTableDataModule.getClusterModuleId() == -1){
                    insertNewModuleOfCluster.add(clusterModuleModel);
                }
            }

            // Cập nhật thông tin cụm
            clusterDao.update(newClusterModel, conn);
            // Thêm mới module
            if(!insertNewModuleOfCluster.isEmpty()) {
                clusterModuleDao.save(insertNewModuleOfCluster, conn);
            }
            // xóa các module bị xóa
            if(moduleIdsToDelete != null && !moduleIdsToDelete.isEmpty()) {
                clusterModuleDao.deleteById(moduleIdsToDelete, conn);
            }
            conn.commit();
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi khi cập nhật cụm, details: ", e);
            try {
                conn.rollback();
            } catch (Exception ex) {
                logger.error("Xảy ra lỗi khi thực hiện rollback dữ liệu, details: ", e);
            }
            e.printStackTrace();
            throw new DaoException("Cập nhật thông tin cụm lỗi, vui lòng thử lại sau.");
        }
    }

    @Override
    public void delete(long id, List<Long> clusterModuleIds) throws DaoException{
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        Connection connection = clusterDao.connection();
        try {
            configClusterModuleDao.deleteByClusterModuleId(clusterModuleIds, connection);
            clusterModuleDao.deleteByCluster(ids, connection);
            clusterDao.delete(ids);
        }
        catch (DaoException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new DaoException("Xóa cụm lỗi, vui lòng thử lại sau.");
            }
            throw e;
        }
    }

    @Override
    public List<ClusterModuleModel> findAllByCluster(long clusterId) throws DaoException{
        return clusterModuleDao.findAllByCluster(clusterId);
    }
}
