package com.module_service_insert.dao;

import com.module_service_insert.mapper.RowMapper;

import java.sql.Connection;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface GenericDao <T> {
    List<T> query(String sql, RowMapper<T> mapper, Object... params);
    long insert(String sql, List<Object[]> models);
    T findById(String sql, RowMapper<T> mapper, long id);
    void delete(String sql, Object... params);
    long insertTransaction(String sql, List<Object[]> models, Connection conn);
    void deleteTransaction(String sql, Connection conn, Object... params);
}
