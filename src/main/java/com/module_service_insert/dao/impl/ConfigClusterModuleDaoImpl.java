package com.module_service_insert.dao.impl;

import com.module_service_insert.dao.IConfigClusterModuleDao;
import com.module_service_insert.exception.DaoException;
import com.module_service_insert.mapper.mapperResultSet.ConfigClusterModuleMapper;
import com.module_service_insert.model.ConfigClusterModuleModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ConfigClusterModuleDaoImpl extends AbstractDao<ConfigClusterModuleModel> implements IConfigClusterModuleDao {

    private final Logger logger = LoggerFactory.getLogger(ConfigClusterModuleDaoImpl.class);

    @Override
    public List<Long> save(List<ConfigClusterModuleModel> configClusterModuleModels) {
        String sql = "INSERT INTO config_cluster_module " +
                    "(id, log_path, cluster_name, module_name) " +
                    "VALUES (?, ?, ?, ?)";
        List<Long> ids = new ArrayList<>();
        try {
            List<Object[]> parameters = new ArrayList<>();
            for(ConfigClusterModuleModel configClusterModuleModel : configClusterModuleModels){
                long id = System.nanoTime();
                parameters.add(new Object[] {
                    id, configClusterModuleModel.getLogPath(),
                    configClusterModuleModel.getClusterName(),
                    configClusterModuleModel.getModuleModel().getName()
                });
                ids.add(id);
            }
            insert(sql, parameters);
            return ids;
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi khi thêm mới config module, details: ", e);
            throw new DaoException("Lỗi khi thêm mới config module, vui lòng thử lại sau");
        }
    }

    @Override
    public List<Long> save(List<ConfigClusterModuleModel> configClusterModuleModels, Connection conn) {
        String sql = "INSERT INTO config_cluster_module " +
                "(id, cluster_name, module_name, " +
                "cluster_module_id, config_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        List<Long> ids = new ArrayList<>();
        try {
            List<Object[]> parameters = new ArrayList<>();
            for(ConfigClusterModuleModel configClusterModuleModel : configClusterModuleModels){
                long id = System.nanoTime();
                parameters.add(new Object[] {
                    id,
                    configClusterModuleModel.getClusterName(),
                    configClusterModuleModel.getModuleName(),
                    configClusterModuleModel.getClusterModuleId(),
                    configClusterModuleModel.getConfigId(),
                });
                ids.add(id);
            }
            insertTransaction(sql, parameters, conn);
            return ids;
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi khi thêm mới config module, details: ", e);
            throw new DaoException("Lỗi khi thêm mới config module, vui lòng thử lại sau");
        }
    }

    @Override
    public List<ConfigClusterModuleModel> findAllByConfig(long configId) {
        String sql = "SELECT * FROM config_cluster_module WHERE config_id = ? ";
        try {
            return query(sql, new ConfigClusterModuleMapper(), configId);
        }
        catch (Exception e) {
            logger.error("Có lỗi khi lấy danh sách config module, details: ", e);
            throw new DaoException("Lỗi khi lấy danh sách, vui lòng thử lại sau.");
        }
    }

    @Override
    public void update(ConfigClusterModuleModel configClusterModuleModel) {
        StringBuilder sql = new StringBuilder("UPDATE config_cluster_module SET ");
        List<Object> params = new ArrayList<>();
        List<String> updatesStr = new ArrayList<>();
        try {
            if(configClusterModuleModel.getpId() != null) {
                updatesStr.add("PID = ?");
                params.add(configClusterModuleModel.getpId());
            }
            if(configClusterModuleModel.getStatus() != null || !configClusterModuleModel.getStatus().isBlank()) {
                updatesStr.add("status = ?");
                params.add(configClusterModuleModel.getStatus());
            }

            sql.append(String.join(", ", updatesStr));
            sql.append(" WHERE id = ?");
            params.add(configClusterModuleModel.getId());

            List<Object[]> parameters = new ArrayList<>();
            parameters.add(params.toArray());
            insert(sql.toString(), parameters);
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi trong quá trình update config module, params: {}, details: ", params, e);
            throw new DaoException("Lỗi khi cập nhật thông tin, vui lòng thử lại sau.");
        }
    }

    @Override
    public void delete(List<Long> ids) {
        String sql = "DELETE FROM config_cluster_module WHERE id in ";
        try {
            if(ids == null || ids.isEmpty()) {
                return;
            }
            String idsStr = ids.toString().replaceAll("^\\[", "(").replaceAll("\\]$", ")");
            sql += idsStr;
            delete(sql);
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi khi xóa module khỏi config, id: {}, details: ", ids, e);
            throw new DaoException("Lỗi xóa module config, vui lòng thử lại sau.");
        }
    }

    @Override
    public void deleteByConfigId(List<Long> configIds, Connection conn) {
        String sql = "DELETE FROM config_cluster_module WHERE config_id in ";
        try {
            if(configIds == null || configIds.isEmpty()) {
                return;
            }
            String configIdsStr = configIds.toString().replaceAll("^\\[", "(").replaceAll("\\]$", ")");
            sql += configIdsStr;
            deleteTransaction(sql, conn);
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi khi xóa module config, config id: {}, details: ", configIds, e);
            throw new DaoException("Lỗi xóa module config, vui lòng thử lại sau.");
        }
    }

    @Override
    public void deleteByClusterModuleId(List<Long> clusterModuleIds, Connection conn) {
        String sql = "DELETE FROM module_insert.config_cluster_module WHERE cluster_module_id in ";
        try {
            if(clusterModuleIds == null || clusterModuleIds.isEmpty()) {
                return;
            }
            String configIdsStr = clusterModuleIds.toString().replaceAll("^\\[", "(").replaceAll("\\]$", ")");
            sql += configIdsStr;
            deleteTransaction(sql, conn);
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi khi xóa module config, config id: {}, details: ", clusterModuleIds, e);
            throw new DaoException("Lỗi xóa module config, vui lòng thử lại sau.");
        }
    }
}
