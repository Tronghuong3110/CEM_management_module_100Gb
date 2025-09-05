package com.module_service_insert.dao.impl;

import com.module_service_insert.dao.IModuleDao;
import com.module_service_insert.exception.DaoException;
import com.module_service_insert.mapper.mapperResultSet.ModuleMapper;
import com.module_service_insert.model.ModuleModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ModuleDaoImpl extends AbstractDao<ModuleModel> implements IModuleDao {
    private final Logger logger = LoggerFactory.getLogger(ModuleDaoImpl.class);

    @Override
    public List<ModuleModel> findAll() {
        String sql = "SELECT * FROM module";
        try {
            return query(sql, new ModuleMapper());
        } catch (Exception e) {
            logger.error("Xảy ra lỗi khi lấy danh sách module, details: ", e);
            throw new DaoException("Lỗi khi lấy danh sách module, vui lòng thử lại sau.");
        }
    }

    @Override
    public long save(ModuleModel moduleModel) {
        String sql = "INSERT INTO module (id, name, description, command) " +
                    "VALUES (?, ?, ?, ?)";
        try {
            List<Object[]> parameters = new ArrayList<>();
            long id = System.currentTimeMillis();
            parameters.add(new Object[] {
                id, moduleModel.getName(), moduleModel.getDescription(), moduleModel.getCommand()
            });
            insert(sql, parameters);
            return id;
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi khi thêm mới module, details: ", e);
            throw new DaoException("Lỗi thêm mới module, vui lòng thử lại sau.");
        }
    }

    @Override
    public void update(ModuleModel moduleModel) {
        StringBuilder sql = new StringBuilder("UPDATE module SET ");
        try {
            List<Object[]> parameters = new ArrayList<>();
            List<String> updateStr = new ArrayList<>();
            List<Object> params = new ArrayList<>();
            if(moduleModel.getName() != null || !moduleModel.getName().isBlank()) {
                params.add(moduleModel.getName());
                updateStr.add("name = ?");
            }
            if(moduleModel.getDescription() != null || !moduleModel.getDescription().isBlank()) {
                params.add(moduleModel.getDescription());
                updateStr.add("description = ?");
            }
            if(moduleModel.getCommand() != null || !moduleModel.getCommand().isBlank()) {
                params.add(moduleModel.getCommand());
                updateStr.add("command = ?");
            }

            sql.append(String.join(", ", updateStr));
            sql.append(" WHERE id = ?");
            params.add(moduleModel.getId());

            parameters.add(params.toArray());
            insert(sql.toString(), parameters);
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi khi cập nhật thông tin module, name: {}, id: {}, details: ", moduleModel.getName(), moduleModel.getId(), e);
            throw new DaoException("Lỗi khi cập nhật thông tin, vui lòng thử lại sau.");
        }
    }

    @Override
    public void delete(List<Long> ids) {
        String sql = "DELETE FROM module WHERE id IN ";
        try {
            if(ids == null || ids.isEmpty()) {
                return;
            }
            String idsStr = ids.toString().replaceAll("^\\[", "(").replaceAll("\\]$", ")");
            sql += idsStr;
            delete(sql);
        }
        catch (Exception e) {
            logger.error("Xảy ra lỗi trong khi xóa module, ids: {}, details: ", ids.toString(), e);
            throw new DaoException("Lỗi khi xóa module, vui lòng thử lại sau.");
        }
    }
}
