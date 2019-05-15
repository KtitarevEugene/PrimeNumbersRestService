package web_app.repository.repository_types;

import web_app.repository.db.db_models.ResultModel;

import java.util.List;

public interface Repository {
    boolean insertResultModel(ResultModel model) throws Exception;
    int insertRequestedValue(String value) throws Exception;
    List<ResultModel> getResultByValue(String value);
    ResultModel getResultById(int id);
}
