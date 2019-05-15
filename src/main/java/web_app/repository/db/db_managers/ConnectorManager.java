package web_app.repository.db.db_managers;

import web_app.repository.db.db_connectors.Connector;

import java.sql.SQLException;

public interface ConnectorManager {
    Connector establishConnection() throws SQLException;
}
