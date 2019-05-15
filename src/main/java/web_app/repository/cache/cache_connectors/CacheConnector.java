package web_app.repository.cache.cache_connectors;

import web_app.repository.db.db_models.ResultModel;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CacheConnector extends AutoCloseable {
    boolean addResultModel(int id, ResultModel model) throws InterruptedException, ExecutionException;
    boolean setResultModel(int id, ResultModel model) throws InterruptedException, ExecutionException;
    ResultModel getResultModel(int id);

    boolean addResultModelList(String key, List<ResultModel> models) throws InterruptedException, ExecutionException;
    List<ResultModel> getResultModelList(String key);
}
