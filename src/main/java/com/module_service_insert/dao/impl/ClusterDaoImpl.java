package com.module_service_insert.dao.impl;

import com.module_service_insert.dao.IClusterDao;
import com.module_service_insert.exception.DaoException;
import com.module_service_insert.mapper.mapperResultSet.ClusterMapper;
import com.module_service_insert.model.ClusterModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ClusterDaoImpl extends AbstractDao<ClusterModel> implements IClusterDao {
    private final Logger logger = LoggerFactory.getLogger(ClusterDaoImpl.class);

    @Override
    public long save(ClusterModel clusterModel) {
        String sql = "INSERT INTO module_insert.cluster (id, name, description, status)" +
                    "values (?, ?, ?, ?)";
        try {
            List<Object[]> parameters = new ArrayList<>();
            long id = System.currentTimeMillis();
            parameters.add(new Object[] {
                id,  clusterModel.getName(),
                clusterModel.getDescription(),
                clusterModel.getStatus()
            });
            insert(sql, parameters);
            return id;
        }
        catch (Exception e) {
            logger.error("Xảy ra lồi khi thêm cluster, {}, details: ", clusterModel, e);
            throw new DaoException("Lỗi thêm mới cluster, vui lòng thử lại sau.");
        }
    }

    @Override
    public long save(ClusterModel clusterModel, Connection conn) {
        String sql = "INSERT INTO module_insert.cluster (id, name, description, status)" +
                "values (?, ?, ?, ?)";
        try {
            List<Object[]> parameters = new ArrayList<>();
            long id = System.currentTimeMillis();
            parameters.add(new Object[] {
                    id,
                    clusterModel.getName(),
                    clusterModel.getDescription(),
                    clusterModel.getStatus()
            });
            insertTransaction(sql, parameters, conn);
            return id;
        }
        catch (Exception e) {
            logger.error("Xảy ra lồi khi thêm cluster, {}, details: ", clusterModel, e);
            throw new DaoException("Lỗi thêm mới cluster, vui lòng thử lại sau.");
        }
    }

    @Override
    public List<ClusterModel> findAll() {
        String sql = "select * from module_insert.cluster c";
        try {
            List<ClusterModel> clusterModels = query(sql, new ClusterMapper());
            return clusterModels;
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi khi lấy danh sách cluster, details: ", e);
            throw new DaoException("Lỗi khi lấy danh sách cụm, vui lòng thử lại sau.");
        }
    }

    @Override
    public void update(ClusterModel clusterModel) {
        StringBuilder sql = new StringBuilder("UPDATE cluster SET ");
        try {
            List<Object> params = new ArrayList<>();
            List<String> updateStr = new ArrayList<>();
            if(clusterModel.getName() != null && !clusterModel.getName().isBlank()) {
                params.add(clusterModel.getName());
                updateStr.add("name = ?");
            }
            if(clusterModel.getDescription() != null && !clusterModel.getDescription().isBlank()) {
                params.add(clusterModel.getDescription());
                updateStr.add("description = ?");
            }
            if(clusterModel.getBaseFolder() != null && !clusterModel.getBaseFolder().isBlank()) {
                params.add(clusterModel.getBaseFolder());
                updateStr.add("base_folder = ?");
            }
            if(clusterModel.getStatusStr() != null && !clusterModel.getStatusStr().isBlank()) {
                params.add(clusterModel.getStatusStr());
                updateStr.add("status = ?");
            }

            sql.append(String.join(", ", updateStr));
            sql.append(" WHERE id = ?");
            params.add(clusterModel.getId());

            List<Object[]> parameters = new ArrayList<>();
            parameters.add(params.toArray());
            insert(sql.toString(), parameters);
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi trong khi cập nhật thông tin cluster, {}, details: ", clusterModel, e);
            throw new DaoException("Lỗi khi cập nhật thông tin, vui lòng thử lại sau.");
        }
    }
    @Override
    public void update(ClusterModel clusterModel, Connection conn) {
        StringBuilder sql = new StringBuilder("UPDATE cluster SET ");
        try {
            List<Object> params = new ArrayList<>();
            List<String> updateStr = new ArrayList<>();
            if(clusterModel.getName() != null && !clusterModel.getName().isBlank()) {
                params.add(clusterModel.getName());
                updateStr.add("name = ?");
            }
            if(clusterModel.getDescription() != null && !clusterModel.getDescription().isBlank()) {
                params.add(clusterModel.getDescription());
                updateStr.add("description = ?");
            }
            if(clusterModel.getBaseFolder() != null && !clusterModel.getBaseFolder().isBlank()) {
                params.add(clusterModel.getBaseFolder());
                updateStr.add("base_folder = ?");
            }
            if(clusterModel.getStatusStr() != null && !clusterModel.getStatusStr().isBlank()) {
                params.add(clusterModel.getStatusStr());
                updateStr.add("status = ?");
            }

            sql.append(String.join(", ", updateStr));
            sql.append(" WHERE id = ?");
            params.add(clusterModel.getId());

            List<Object[]> parameters = new ArrayList<>();
            parameters.add(params.toArray());
            insertTransaction(sql.toString(), parameters, conn);
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi trong khi cập nhật thông tin cluster, {}, details: ", clusterModel, e);
            throw new DaoException("Lỗi khi cập nhật thông tin, vui lòng thử lại sau.");
        }
    }

    @Override
    public void delete(List<Long> ids) {
        String sql = "DELETE FROM cluster WHERE id in ";
        try {
            String idsStr = ids.toString().replaceAll("^\\[", "(").replaceAll("\\]$", ")");
            sql += idsStr;
            delete(sql);
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi khi xóa cluster, ids: {}, details: ", ids, e);
            throw new DaoException("Lỗi khi xóa cụm, vui lòng thử lại sau.");
        }
    }

    @Override
    public Connection connection() {
        return getConnection();
    }
}
