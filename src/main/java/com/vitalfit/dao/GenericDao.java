package com.vitalfit.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Generic Data Access Object contract for CRUD operations.
 *
 * @author ADITYA KUMAR SINGH (25SCSE1180275) - Team NOVA
 */
public interface GenericDao<T, ID> {
    Optional<T> findById(ID id) throws SQLException;
    List<T> findAll() throws SQLException;
    boolean save(T entity) throws SQLException;
    boolean update(T entity) throws SQLException;
    boolean deleteById(ID id) throws SQLException;
}
