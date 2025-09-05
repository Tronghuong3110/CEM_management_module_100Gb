package com.module_service_insert.dao.impl;

import com.module_service_insert.constant.VariableCommon;
import com.module_service_insert.dao.GenericDao;
import com.module_service_insert.mapper.RowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class AbstractDao<T> implements GenericDao<T> {
    private final ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();
    protected final Logger logger =  LoggerFactory.getLogger(AbstractDao.class);

    protected Connection getConnection() {
        Connection conn = connectionThreadLocal.get();
        try {
            if(conn == null || conn.isClosed()) {
                Class.forName("org.mariadb.jdbc.Driver");
//                String url = "jdbc:mariadb://" + VariableCommon.DATABASE_IP + ":3306/" + VariableCommon.DATABASE_NAME;
                String url = "jdbc:mariadb://192.168.150.128:3306/module_insert";
//                conn = DriverManager.getConnection(url, VariableCommon.DATABASE_USERNAME, VariableCommon.DATABASE_PASSWORD);
                conn = DriverManager.getConnection(url, "huong", "1234");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }

    private void setParams(PreparedStatement stm, Object...params) throws SQLException {
        for(int i = 0; i < params.length; i++) {
            Object param =  params[i];
            int index = i+1;
            if(param instanceof String) {
                stm.setString(index, (String) param);
            }
            if(param instanceof Integer) {
                stm.setInt(index, (Integer) param);
            }
            if(param instanceof Long) {
                stm.setLong(index, (Long) param);
            }
            if(param instanceof Double) {
                stm.setDouble(index, (Double) param);
            }
            if(param instanceof Boolean) {
                stm.setBoolean(index, (Boolean) param);
            }
        }
    }

    @Override
    public List<T> query(String sql, RowMapper<T> mapper, Object... params) {
        PreparedStatement stm = null;
        ResultSet rs = null;
        List<T> result  = new ArrayList<>();
        try {
            Connection conn = getConnection();
            stm = conn.prepareStatement(sql);
            setParams(stm, params);
            rs = stm.executeQuery();

            while(rs.next()) {
                result.add(mapper.mapRow(rs));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally {
            try {
                if(rs != null) rs.close();
                if(stm != null) stm.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public long insert(String sql, List<Object[]> models) {
        PreparedStatement stm = null;
        try {
            Connection conn = getConnection();
            stm = conn.prepareStatement(sql);
            for(Object[] param : models) {
                setParams(stm, param);
                stm.addBatch();
            }
            stm.executeBatch();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally {
            try {
                if(stm != null) stm.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public long insertTransaction(String sql, List<Object[]> models, Connection conn) {
        PreparedStatement stm = null;
        try {
            conn.setAutoCommit(false);
            stm = conn.prepareStatement(sql);
            for(Object[] param : models) {
                setParams(stm, param);
                stm.addBatch();
            }
            stm.executeBatch();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally {
            try {
                if(stm != null) stm.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public T findById(String sql, RowMapper<T> mapper, long id) {
        PreparedStatement stm = null;
        ResultSet rs = null;
        T result = null;
        try {
            Connection conn = getConnection();
            stm = conn.prepareStatement(sql);
            stm.setLong(1, id);
            rs = stm.executeQuery();
            if(rs.next()) {
                result = mapper.mapRow(rs);
            }
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally {
            try {
                if(rs != null) rs.close();
                if(stm != null) stm.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void delete(String sql, Object... params) {
        PreparedStatement stm = null;
        Connection conn = null;
        try {
            conn = getConnection();
            stm = conn.prepareStatement(sql);
            setParams(stm, params);
            stm.execute();
        }
        catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException(e);
        }
        finally {
            try {
                if(stm != null) stm.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void deleteTransaction(String sql, Connection conn, Object... params) {
        PreparedStatement stm = null;
        try {
            conn.setAutoCommit(false);
            conn = getConnection();
            stm = conn.prepareStatement(sql);
            setParams(stm, params);
            stm.execute();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                if(stm != null) stm.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
