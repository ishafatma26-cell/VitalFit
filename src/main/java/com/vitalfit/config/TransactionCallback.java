package com.vitalfit.config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Functional callback interface for transactional JDBC operations.
 *
 * @author ADITYA KUMAR SINGH (25SCSE1180275) - Team NOVA
 */
@FunctionalInterface
public interface TransactionCallback<T> {
    T doInTransaction(Connection connection) throws SQLException;
}
