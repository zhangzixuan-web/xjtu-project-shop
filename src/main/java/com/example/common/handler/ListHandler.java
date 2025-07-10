package com.example.common.handler;


import cn.hutool.json.JSONUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * MyBatis TypeHandler for List collections.
 * <p>
 * This handler converts a List of objects to a JSON string for storing in the database,
 * and converts a JSON string from the database back into a List of objects.
 * It uses Hutool's JSONUtil for JSON serialization and deserialization.
 * </p>
 */
public class ListHandler extends BaseTypeHandler<List> {

    /**
     * Sets the parameter on the PreparedStatement.
     * Converts the List object into a JSON string before setting it.
     *
     * @param preparedStatement the PreparedStatement
     * @param i                 the parameter index
     * @param list              the List object to set
     * @param jdbcType          the JDBC type
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, List list, JdbcType jdbcType) throws SQLException {
        preparedStatement.setObject(i, JSONUtil.toJsonStr(list));
    }

    /**
     * Gets the result from the ResultSet by column name.
     * Converts the JSON string from the database into a List.
     *
     * @param resultSet the ResultSet
     * @param s         the column name
     * @return a List object
     * @throws SQLException if a database access error occurs
     */
    @Override
    public List getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return JSONUtil.parseArray(resultSet.getString(s));
    }

    /**
     * Gets the result from the ResultSet by column index.
     * Converts the JSON string from the database into a List.
     *
     * @param resultSet the ResultSet
     * @param i         the column index
     * @return a List object
     * @throws SQLException if a database access error occurs
     */
    @Override
    public List getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return JSONUtil.parseArray(resultSet.getString(i));
    }

    /**
     * Gets the result from the CallableStatement by column index.
     * Converts the JSON string from the database into a List.
     *
     * @param callableStatement the CallableStatement
     * @param i                 the column index
     * @return a List object
     * @throws SQLException if a database access error occurs
     */
    @Override
    public List getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return JSONUtil.parseArray(callableStatement.getString(i));
    }


}


