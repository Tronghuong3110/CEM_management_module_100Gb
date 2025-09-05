package com.module_service_insert.dao.impl;

import com.module_service_insert.dao.IClusterModuleDao;
import com.module_service_insert.exception.DaoException;
import com.module_service_insert.mapper.mapperResultSet.ClusterModuleMapper;
import com.module_service_insert.model.ClusterModuleModel;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ClusterModuleDaoImpl extends AbstractDao<ClusterModuleModel> implements IClusterModuleDao {

    @Override
    public List<Long> save(List<ClusterModuleModel> clusterModuleModels) {
        String sql = "INSERT INTO cluster_module (id, cluster_id, " +
                    "module_id, cpu_list, run_path, config_path, log_path)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        List<Long> ids = new ArrayList<>();
        try {
            List<Object[]> parameters = new ArrayList<>();
            for(ClusterModuleModel clusterModuleModel : clusterModuleModels){
                long id = System.nanoTime();
                parameters.add(new Object[]{
                   id,
                   clusterModuleModel.getCluster_id(),
                   clusterModuleModel.getModule_id(),
                   clusterModuleModel.getCpuList(),
                   clusterModuleModel.getRunPath(),
                   clusterModuleModel.getConfigPath(),
                   clusterModuleModel.getLogPath()
                });
                ids.add(id);
            }
            insert(sql, parameters);
            return ids;
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi trong khi thêm mới cluster module, {}, details: ", clusterModuleModels.toString(), e);
            throw new DaoException("Lỗi khi thêm mới module vào cụm, vui lòng thử lại sau.");
        }
    }

    @Override
    public List<Long> save(List<ClusterModuleModel> clusterModuleModels, Connection conn) {
        String sql = "INSERT INTO cluster_module (id, cluster_id, " +
                "module_id, cpu_list, run_path, config_path)" +
                "VALUES (?, ?, ?, ?, ?, ?)";
        List<Long> ids = new ArrayList<>();
        try {
            List<Object[]> parameters = new ArrayList<>();
            for(ClusterModuleModel clusterModuleModel : clusterModuleModels){
                long id = System.nanoTime();
                parameters.add(new Object[]{
                        id,
                        clusterModuleModel.getCluster_id(),
                        clusterModuleModel.getModule_id(),
                        clusterModuleModel.getCpuList(),
                        clusterModuleModel.getRunPath(),
                        clusterModuleModel.getConfigPath()
                });
                ids.add(id);
            }
            insertTransaction(sql, parameters, conn);
            return ids;
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi trong khi thêm mới cluster module, {}, details: ", clusterModuleModels.toString(), e);
            throw new DaoException("Lỗi khi thêm mới module vào cụm, vui lòng thử lại sau.");
        }
    }

    @Override
    public List<ClusterModuleModel> findAllByCluster(long clusterId) {
        String sql = "select cm.*, m.name as module_name, m.command from module_insert.cluster_module cm \n" +
                "left join module_insert.module m on cm.module_id = m.id\n" +
                "where cm.cluster_id = ?";
        try {
            List<ClusterModuleModel> clusterModuleModels = query(sql, new ClusterModuleMapper(), clusterId);
            return clusterModuleModels;
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi khi lấy danh sách module của cụm: {}, details: ", clusterId, e);
        }
        return List.of();
    }

    @Override
    public void update(ClusterModuleModel clusterModuleModel) {
        StringBuilder sql = new StringBuilder("UPDATE cluster_module SET ");
        try {
            List<Object> params = new ArrayList<>();
            List<String> updateStr = new ArrayList<>();
            if(clusterModuleModel.getCpuList() != null && !clusterModuleModel.getCpuList().isBlank()) {
                params.add(clusterModuleModel.getCpuList());
                updateStr.add("cpu_list = ?");
            }
            if(clusterModuleModel.getRunPath() != null && !clusterModuleModel.getRunPath().isBlank()) {
                params.add(clusterModuleModel.getRunPath());
                updateStr.add("run_path = ?");
            }
            if(clusterModuleModel.getConfigPath() != null && !clusterModuleModel.getConfigPath().isBlank()) {
                params.add(clusterModuleModel.getConfigPath());
                updateStr.add("config_path = ?");
            }

            sql.append(String.join(", ", updateStr));
            sql.append(" WHERE id = ?");

            List<Object[]> parameters = new ArrayList<>();
            parameters.add(params.toArray());
            insert(sql.toString(), parameters);
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi khi cập nhật thông tin module trong cluster: {}, details: ", clusterModuleModel.toString(), e);
            throw new DaoException("Lỗi cập nhật thông tin module của cụm, vui lòng thử lại sau.");
        }
    }

    @Override
    public void update(ClusterModuleModel clusterModuleModel, Connection conn) {
        StringBuilder sql = new StringBuilder("UPDATE cluster_module SET ");
        try {
            List<Object> params = new ArrayList<>();
            List<String> updateStr = new ArrayList<>();
            if(clusterModuleModel.getCpuList() != null && !clusterModuleModel.getCpuList().isBlank()) {
                params.add(clusterModuleModel.getCpuList());
                updateStr.add("cpu_list = ?");
            }
            if(clusterModuleModel.getRunPath() != null && !clusterModuleModel.getRunPath().isBlank()) {
                params.add(clusterModuleModel.getRunPath());
                updateStr.add("run_path = ?");
            }
            if(clusterModuleModel.getConfigPath() != null && !clusterModuleModel.getConfigPath().isBlank()) {
                params.add(clusterModuleModel.getConfigPath());
                updateStr.add("config_path = ?");
            }

            sql.append(String.join(", ", updateStr));
            sql.append(" WHERE id = ?");

            params.add(clusterModuleModel.getId());
            List<Object[]> parameters = new ArrayList<>();
            parameters.add(params.toArray());
            insertTransaction(sql.toString(), parameters, conn);
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi khi cập nhật thông tin module trong cluster: {}, details: ", clusterModuleModel.toString(), e);
            throw new DaoException("Lỗi cập nhật thông tin module của cụm, vui lòng thử lại sau.");
        }
    }

    @Override
    public void deleteById(List<Long> ids) {
        String sql = "DELETE FROM cluster_module WHERE id in ";
        try {
            String idsStr = ids.toString().replaceAll("^\\[", "(").replaceAll("\\]$", ")");
            sql += idsStr;
            delete(sql);
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi khi xóa module của cluster, ids: {}, details: ", ids, e);
            throw new DaoException("Lỗi khi xóa module của cụm, vui lòng thử lại sau.");
        }
    }

    @Override
    public void deleteById(List<Long> ids, Connection conn) {
        String sql = "DELETE FROM cluster_module WHERE id in ";
        try {
            String idsStr = ids.toString().replaceAll("^\\[", "(").replaceAll("\\]$", ")");
            sql += idsStr;
            deleteTransaction(sql, conn);
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi khi xóa module của cluster, ids: {}, details: ", ids, e);
            throw new DaoException("Lỗi khi xóa module của cụm, vui lòng thử lại sau.");
        }
    }

    @Override
    public void deleteByCluster(List<Long> clusterIds, Connection conn) {
        String sql = "DELETE FROM module_insert.cluster_module WHERE cluster_id in ";
        try {
            String idsStr = clusterIds.toString().replaceAll("^\\[", "(").replaceAll("\\]$", ")");
            sql += idsStr;
            deleteTransaction(sql, conn);
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi khi xóa module của cluster, ids: {}, details: ", clusterIds, e);
            throw new DaoException("Lỗi khi xóa module của cụm, vui lòng thử lại sau.");
        }
    }
}
