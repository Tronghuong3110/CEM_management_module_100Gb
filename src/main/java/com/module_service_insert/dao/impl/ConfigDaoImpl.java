package com.module_service_insert.dao.impl;

import com.module_service_insert.dao.IConfigDao;
import com.module_service_insert.exception.DaoException;
import com.module_service_insert.mapper.mapperResultSet.ConfigMapper;
import com.module_service_insert.model.ConfigModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ConfigDaoImpl extends AbstractDao<ConfigModel> implements IConfigDao {

    private final Logger logger = LoggerFactory.getLogger(ConfigDaoImpl.class);

    @Override
    public List<ConfigModel> findAll() {
        String sql = "select * from module_insert.config";
        try {
            return query(sql, new ConfigMapper());
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi trong quá trình lấy ra danh sách config, details: ", e);
            throw new DaoException("Xảy ra lỗi khi lấy danh sách config, vui lòng thử lại sau.");
        }
    }

    @Override
    public long save(ConfigModel configModel) {
        String sql = "INSERT INTO module_insert.config (id, name) VALUES (?, ?)";
        try {
            List<Object[]> parameters = new ArrayList<>();
            long id = System.currentTimeMillis();
            parameters.add(new Object[]{id, configModel.getName()});
            insert(sql, parameters);
            return id;
        }
        catch (Exception e) {
            logger.error("Có lỗi xảy ra trong quá trình thêm mới config, details: ", e);
            throw new DaoException("Xảy ra lỗi khi thêm mới, vui lòng thử lại sau.");
        }
    }

    @Override
    public long save(ConfigModel configModel, Connection conn) {
        String sql = "INSERT INTO module_insert.config (id, name) VALUES (?, ?)";
        try {
            List<Object[]> parameters = new ArrayList<>();
            long id = System.currentTimeMillis();
            parameters.add(new Object[]{id, configModel.getName()});
            insertTransaction(sql, parameters, conn);
            return id;
        }
        catch (Exception e) {
            logger.error("Có lỗi xảy ra trong quá trình thêm mới config, details: ", e);
            throw new DaoException("Xảy ra lỗi khi thêm mới, vui lòng thử lại sau.");
        }
    }

    @Override
    public void update(ConfigModel configModel) {
        String sql = "UPDATE config SET name = ? WHERE id = ?";
        try {
            List<Object[]> parameters = new ArrayList<>();
            parameters.add(new Object[]{configModel.getName(), configModel.getId()});
            insert(sql, parameters);
        }
        catch (Exception e) {
            logger.error("Có lỗi xảy ra trong quá trình update config, detail: ", e);
            throw new DaoException("Xảy ra lỗi khi cập nhật thông tin, vui lòng thử lại sau.");
        }
    }

    @Override
    public void delete(List<Long> configIds, Connection conn) {
        String sql = "DELETE FROM config WHERE id in ";
        try {
            if (configIds != null && !configIds.isEmpty()) {
                return;
            }
            String configIdsStr = configIds.toString().replaceAll("^\\[", "(").replaceAll("\\]$", ")");
            sql += configIdsStr;
            deleteTransaction(sql, conn);
        }
        catch (Exception e) {
            logger.error("Có lỗi xảy ra trong quá trình xóa config, details: ", e);
            throw new DaoException("Xảy ra lỗi khi xóa config, vui lòng thử lại sau.");
        }
    }

    @Override
    public Connection connection() {
        try {
            return getConnection();
        }
        catch (Exception e) {
            throw new DaoException("Xảy ra lỗi hệ thống, vui lòng thử lại sau.");
        }
    }
}
