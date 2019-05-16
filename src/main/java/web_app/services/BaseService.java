package web_app.services;

import com.mysql.jdbc.Driver;
import org.ini4j.Ini;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_app.common.Constants;
import web_app.common.Utils;
import web_app.messaging.JMSConnection;
import web_app.messaging.exceptions.JMSConfigurationException;
import web_app.repository.DataRepository;
import web_app.repository.cache.cache_connectors.MemcachedConnector;
import web_app.repository.cache.cache_managers.CacheManager;
import web_app.repository.cache.cache_managers.MemcachedManager;
import web_app.repository.db.db_connectors.MySQLConnector;
import web_app.repository.db.db_managers.ConnectorManager;
import web_app.repository.db.db_managers.NonPooledConnectorManager;
import web_app.repository.db.db_managers.PooledConnectorManager;
import web_app.repository.repository_types.CachedRepository;
import web_app.repository.repository_types.NonCachedRepository;
import web_app.repository.repository_types.Repository;

import javax.jms.JMSException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class BaseService {

    private final Logger logger = LoggerFactory.getLogger(PrimeNumbersService.class);

    protected DataRepository dataRepository;
    protected JMSConnection connection;
    protected Properties messagingProperties;

    protected void init() {
        try {
            Ini configFile = loadConfigFile();

            initDataRepository(configFile);
            initJmsConnection(configFile);

        } catch (IOException ex) {
            logger.error("Config file {} not found.",
                    System.getenv(Constants.ENV_VAR_NAME), ex);
        } catch (SQLException ex) {
            logger.error("SQLException has been thrown: ", ex);
        } catch (JMSException ex) {
            logger.error("JMSException has been thrown: ", ex);
        } catch (JMSConfigurationException ex) {
            logger.error("JMSConfigurationException has been thrown: ", ex);
        }
    }

    @NotNull
    private Ini loadConfigFile() throws IOException {
        String filename = System.getenv(Constants.ENV_VAR_NAME);
        Reader configFileReader = new FileReader(filename);

        Ini configFile = new Ini();
        configFile.load(configFileReader);

        return configFile;
    }

    private void initDataRepository (@NotNull Ini configFile) throws SQLException {
        Properties jdbcProps = getPropertiesBySectionName(configFile, Constants.JDBC_CFG);
        Properties cacheProps = getPropertiesBySectionName(configFile, Constants.CACHE_CFG);

        Repository repositoryType = getRepositoryType(cacheProps, jdbcProps);

        dataRepository = new DataRepository();
        dataRepository.setRepositoryType(repositoryType);
    }

    private void initJmsConnection(Ini configFile) throws JMSConfigurationException, JMSException {
        messagingProperties = getPropertiesBySectionName(configFile, Constants.ACTIVE_MQ_CFG);

        connection = new JMSConnection(messagingProperties);
    }

    @NotNull
    private Properties getPropertiesBySectionName(@NotNull Ini configFile, String sectionName) {
        Properties properties = new Properties();

        Utils.addAllConfigParamsFromSection(configFile, sectionName, properties);

        return properties;
    }

    @NotNull
    private Repository getRepositoryType(@NotNull Properties cacheProps, @NotNull Properties jdbcProps) throws SQLException {

        String useCache = cacheProps.getProperty(Constants.CACHE_USE_CACHE);

        if (useCache != null && useCache.equalsIgnoreCase(Constants.USE_CACHE_VALUE)) {
            CacheManager memcachedManager = new MemcachedManager.Builder()
                    .setHost(cacheProps.getProperty(Constants.CACHE_HOST))
                    .setPort(Integer.parseInt(cacheProps.getProperty(Constants.CACHE_PORT)))
                    .setOperationTimeoutMillis(Integer.parseInt(cacheProps.getProperty(Constants.CACHE_TIMEOUT)))
                    .setExpirationTimeMillis(Integer.parseInt(cacheProps.getProperty(Constants.CACHE_EXPIRATION_TIME)))
                    .setLogLevel(getLogLevelValue(cacheProps.getProperty(Constants.CACHE_LOG_LEVEL)))
                    .build();

            return new CachedRepository(
                    getConnectorManager(jdbcProps),
                    memcachedManager);
        }

        return new NonCachedRepository(
                getConnectorManager(jdbcProps));

    }

    private MemcachedConnector.LogLevel getLogLevelValue(String logLevel) {

        MemcachedConnector.LogLevel level = MemcachedConnector.LogLevel.SEVERE;

        try {
            level = MemcachedConnector.LogLevel.valueOf(logLevel.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            logger.error("'log_level' property is wrong or not specified, used default value.");
        }

        return level;
    }

    @NotNull
    private ConnectorManager getConnectorManager(@NotNull Properties properties) throws SQLException {
        DriverManager.registerDriver(new Driver());

        String usePool = properties.getProperty(Constants.JDBC_USE_CONNECTION_POOL);

        if (usePool.equalsIgnoreCase(Constants.USE_POOL_VALUE)) {

            PooledConnectorManager.Builder builder = new PooledConnectorManager.Builder(new MySQLConnector())
                    .setUrl(properties.getProperty(Constants.JDBC_URL))
                    .setUsername(properties.getProperty(Constants.JDBC_USER))
                    .setPassword(properties.getProperty(Constants.JDBC_PASSWORD))
                    .setConnectionTestQuery(properties.getProperty(Constants.JDBC_CONNECTION_TEXT_QUERY))
                    .setPoolName(properties.getProperty(Constants.JDBC_POOL_NAME));
            try {
                builder.setLeakDetectionThreshold(Integer.parseInt(properties.getProperty(Constants.JDBC_LEAK_DETECTION_THRESHOLD)));
            } catch (NumberFormatException ex) {
                logger.error("can't set {} parameter, used default value", Constants.JDBC_LEAK_DETECTION_THRESHOLD);
            }

            try {
                builder.setMaximumPoolSize(Integer.parseInt(properties.getProperty(Constants.JDBC_MAXIMUM_POOL_SIZE)));
            } catch (NumberFormatException ex) {
                logger.error("can't set {} parameter, used default value", Constants.JDBC_MAXIMUM_POOL_SIZE);
            }

            try {
                builder.setMinimumIdle(Integer.parseInt(properties.getProperty(Constants.JDBC_MINIMUM_IDLE)));
            } catch (NumberFormatException ex) {
                logger.error("can't set {} parameter, used default value", Constants.JDBC_LEAK_DETECTION_THRESHOLD);
            }

            for (Object obj : properties.keySet()) {
                if (obj instanceof String) {
                    String key = (String) obj;
                    if (key.startsWith(Constants.JDBC_PARAM_PREFIX)) {
                        builder.addSourceProperty(key.replace(Constants.JDBC_PARAM_PREFIX, ""), properties.getProperty(key));
                    }
                }
            }

            return builder.build();
        }

        NonPooledConnectorManager.Builder builder = new NonPooledConnectorManager.Builder(new MySQLConnector())
                .setUrl(properties.getProperty(Constants.JDBC_URL))
                .setUsername(properties.getProperty(Constants.JDBC_USER))
                .setPassword(properties.getProperty(Constants.JDBC_PASSWORD));

        for (Object obj : properties.keySet()) {
            if (obj instanceof String) {
                String key = (String) obj;
                if (key.startsWith(Constants.JDBC_PARAM_PREFIX)) {
                    builder.setCustomParameter(key.replace(Constants.JDBC_PARAM_PREFIX, ""), properties.getProperty(key));
                }
            }
        }

        return builder.build();
    }

}
