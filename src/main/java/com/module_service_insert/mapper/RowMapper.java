package com.module_service_insert.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Trọng Hướng
 */
public interface RowMapper<T> {
    T mapRow(ResultSet rs) throws SQLException;
}
