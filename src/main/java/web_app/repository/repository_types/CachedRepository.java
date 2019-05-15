package web_app.repository.repository_types;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_app.repository.cache.cache_connectors.CacheConnector;
import web_app.repository.cache.cache_managers.CacheManager;
import web_app.repository.db.db_connectors.Connector;
import web_app.repository.db.db_managers.ConnectorManager;
import web_app.repository.db.db_models.ResultModel;
import web_app.repository.exceptions.CacheConnectionException;
import web_app.repository.exceptions.NoDataInCacheException;
import web_app.repository.exceptions.NoDataInDBException;

import java.io.IOException;
import java.util.List;

public class CachedRepository implements Repository {

    private static final Logger logger = LoggerFactory.getLogger(CachedRepository.class);

    private static final String VALUE_KEY = "VALUE_PREFIX_KEY_%s";

    private ConnectorManager connectorManager;
    private CacheManager cacheManager;

    public CachedRepository(ConnectorManager connectorManager, CacheManager cacheManager) {
        this.connectorManager = connectorManager;
        this.cacheManager = cacheManager;
    }

    @Override
    public boolean insertResultModel(ResultModel model) throws Exception {
        try (Connector connector = connectorManager.establishConnection()) {
            return connector.insertResultModel(model);
        }
    }

    @Override
    public int insertRequestedValue (String value) throws Exception {
        try (Connector connector = connectorManager.establishConnection()) {
            return connector.insertRequestedValue(value);
        }
    }

    @Override
    public List<ResultModel> getResultByValue (String value) {
        List<ResultModel> resultModels = null;
        try {
            logger.info("Searching for result for value {} in cache...", value);
            return retrieveFromCache(value);
        } catch (NoDataInCacheException e) {
            logger.warn("Result in cache not found.");
            try {
                logger.info("Getting result from db...");
                resultModels = retrieveFromDatabase(value);

                if (!resultModels.isEmpty()) {
                    logger.info("Done. Saving result to cache.");
                    putModelsListToCache(value, resultModels);
                }
            } catch (NoDataInDBException ex) {
                logger.warn("Result for value {} not found", value);
            } catch (CacheConnectionException ex ) {
                logger.error("Error has happened during connection to cache.", ex);
            }
        } catch (CacheConnectionException e) {
            logger.warn("Error has happened during connection to cache.");
            try {
                logger.info("Getting data from db...");
                resultModels = retrieveFromDatabase(value);
            } catch (NoDataInDBException ex ) {
                logger.warn("Result for value {} not found", value);
            }
        }

        return resultModels;
    }

    @Override
    public ResultModel getResultById(int id) {
        ResultModel resultModel = null;
        try {
            logger.info("Getting for result by id = {} in cache...", id);
            return retrieveFromCache(id);
        } catch (NoDataInCacheException e) {
            logger.warn("Result in cache not found.");
            try {
                logger.info("Getting result from db...");
                resultModel = retrieveFromDatabase(id);

                logger.info("Done. Saving result to cache.");
                putModelToCache(id, resultModel);
            } catch (NoDataInDBException ex) {
                logger.warn("Result with id = {} not found", id);
            } catch (CacheConnectionException ex ) {
                logger.error("Error has happened during connection to cache.", ex);
            }
        } catch (CacheConnectionException e) {
            logger.warn("Error has happened during connection to cache.");
            try {
                logger.info("Getting data from db...");
                resultModel = retrieveFromDatabase(id);
            } catch (NoDataInDBException ex ) {
                logger.warn("Result with id = {} not found", id);
            }
        }

        return resultModel;
    }

    @NotNull
    private ResultModel retrieveFromDatabase(int id) throws NoDataInDBException {
        try (Connector connector = connectorManager.establishConnection()) {
            ResultModel model = connector.getResultById(id);
            if (model != null) {
                return model;
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

        throw new NoDataInDBException();
    }

    @NotNull
    private List<ResultModel> retrieveFromDatabase(String value) throws NoDataInDBException {
        try (Connector connector = connectorManager.establishConnection()) {
            List<ResultModel> resultModels = connector.getResultByValue(value);

            if (resultModels != null) {
                return resultModels;
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

        throw new NoDataInDBException();
    }

    @NotNull
    private List<ResultModel> retrieveFromCache(String value) throws CacheConnectionException, NoDataInCacheException {
        try (CacheConnector cacheConnector = cacheManager.getCacheConnector()) {
            List<ResultModel> resultModels = cacheConnector.getResultModelList(String.format(VALUE_KEY, value));

            if (resultModels != null) {
                return resultModels;
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
            if (e instanceof IOException) {
                throw new CacheConnectionException();
            }
        }

        throw new NoDataInCacheException();
    }

    @NotNull
    private ResultModel retrieveFromCache(int id) throws NoDataInCacheException, CacheConnectionException {
        try (CacheConnector cacheConnector = cacheManager.getCacheConnector()) {
            ResultModel model = cacheConnector.getResultModel(id);

            if (model != null) {
                return model;
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
            if (e instanceof IOException) {
                throw new CacheConnectionException();
            }
        }

        throw new NoDataInCacheException();
    }

    private void putModelToCache(int id, ResultModel model) throws CacheConnectionException {
        try (CacheConnector cacheConnector = cacheManager.getCacheConnector()) {
            cacheConnector.addResultModel(id, model);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            throw new CacheConnectionException();
        }
    }

    private void putModelsListToCache(String key, List<ResultModel> models) throws CacheConnectionException {
        try (CacheConnector cacheConnector = cacheManager.getCacheConnector()) {

            cacheConnector.addResultModelList(String.format(VALUE_KEY, key), models);

            for (ResultModel model : models) {
                cacheConnector.addResultModel(model.getId(), model);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
            throw new CacheConnectionException();
        }
    }
}
