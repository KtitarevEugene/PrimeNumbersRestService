package web_app.repository.db.db_connectors;

import org.jetbrains.annotations.NotNull;
import web_app.repository.db.db_models.ResultModel;

import java.sql.Connection;
import java.util.List;

public interface Connector extends AutoCloseable {
    void setConnection(@NotNull Connection connection);

    boolean insertResultModel(ResultModel model);
    int insertRequestedValue(String value);
    List<ResultModel> getResultByValue(String value);
    ResultModel getResultById(int id);
}
