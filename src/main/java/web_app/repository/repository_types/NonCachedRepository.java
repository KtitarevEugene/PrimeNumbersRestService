package web_app.repository.repository_types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_app.repository.db.db_connectors.Connector;
import web_app.repository.db.db_managers.ConnectorManager;
import web_app.repository.db.db_models.ResultModel;
import web_app.repository.exceptions.NoDataInDBException;

import java.util.List;

public class NonCachedRepository implements Repository {

    private static final Logger logger = LoggerFactory.getLogger(NonCachedRepository.class);

    private ConnectorManager connectorManager;

    public NonCachedRepository(ConnectorManager connectorManager) {
        this.connectorManager = connectorManager;
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

    @Nullable
    @Override
    public List<ResultModel> getResultByValue (String value) {
        List<ResultModel> resultModels = null;
        try {
            logger.info("Getting result from db...");
            resultModels = retrieveFromDatabase(value);
        } catch (NoDataInDBException ex) {
            logger.warn("Result for value {} not found", value);
        }

        return resultModels;
    }

    @Nullable
    @Override
    public ResultModel getResultById(int id) {
        ResultModel resultModel = null;
        try {
            logger.info("Getting result with id = {} from db...", id);
            resultModel = retrieveFromDatabase(id);
        } catch (NoDataInDBException ex) {
            logger.warn("Result with id = {} not found", id);
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
}
