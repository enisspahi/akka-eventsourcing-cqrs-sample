package de.openvalue.resilience.infrastructure;

import akka.japi.function.Function;
import akka.projection.jdbc.JdbcSession;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PlainJdbcSession implements JdbcSession {

    private final Connection connection;

    public PlainJdbcSession() {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "username", "password");
            connection.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public <Result> Result withConnection(Function<Connection, Result> func) throws Exception {
        return func.apply(connection);
    }

    @Override
    public void commit() throws SQLException {
        connection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }
}
