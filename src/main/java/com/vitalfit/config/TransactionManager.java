package com.vitalfit.config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Clean Database Transaction Management Helper.
 * Executes operations inside an explicit JDBC transaction:
 * 1. connection.setAutoCommit(false);
 * 2. Execute statements;
 * 3. connection.commit();
 * 4. In catch (SQLException ex): rollback();
 * 5. In finally: setAutoCommit(true) and connection.close().
 *
 * @author ADITYA KUMAR SINGH (25SCSE1180275) - Team NOVA
 */
public class TransactionManager {

    public static <T> T executeInTransaction(TransactionCallback<T> callback) throws SQLException {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            T result = callback.doInTransaction(connection);

            connection.commit();
            return result;
        } catch (SQLException ex) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rbEx) {
                    System.err.println("Transaction rollback failed: " + rbEx.getMessage());
                }
            }
            throw ex;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException acEx) {
                    System.err.println("Failed to reset autoCommit to true: " + acEx.getMessage());
                }
                try {
                    connection.close();
                } catch (SQLException clEx) {
                    System.err.println("Failed to close connection: " + clEx.getMessage());
                }
            }
        }
    }
}
