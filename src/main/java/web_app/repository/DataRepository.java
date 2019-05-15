package web_app.repository;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import web_app.repository.db.db_models.ResultModel;
import web_app.repository.repository_types.Repository;

import java.util.List;

public class DataRepository implements Repository {

    private Repository repositoryType;

    public DataRepository() {
        this(null);
    }

    public DataRepository(Repository repositoryType) {
        this.repositoryType = repositoryType;
    }

    public void setRepositoryType(@NotNull Repository repositoryType) {
        this.repositoryType = repositoryType;
    }

    @Override
    public boolean insertResultModel(@NotNull ResultModel model) throws Exception {
        return repositoryType.insertResultModel(model);
    }

    @Override
    public int insertRequestedValue (String value) throws Exception {
        return repositoryType.insertRequestedValue(value);
    }

    @Nullable
    @Override
    public List<ResultModel> getResultByValue (String value) {
        return repositoryType.getResultByValue(value);
    }

    @Nullable
    @Override
    public ResultModel getResultById(int id) {
        return repositoryType.getResultById(id);
    }
}
