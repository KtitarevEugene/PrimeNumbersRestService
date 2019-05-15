package web_app.repository.db.db_connectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web_app.repository.db.db_models.ResultModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MySQLConnector implements Connector {

    private static final Logger logger = LoggerFactory.getLogger(MySQLConnector.class);

    private Connection connection;

    @Override
    public void setConnection(@NotNull Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean insertResultModel(@NotNull ResultModel model) {
        try (PreparedStatement statement =
                     connection.prepareStatement("INSERT INTO `results`.`prime_numbers` " +
                             "(`value`, `results`) VALUES (?, ?);")) {

            statement.setString(1, model.getValue());
            statement.setString(2, new Gson().toJson(model.getPrimeNumbers(), new TypeToken<List<Integer>>(){}.getType()));

            return statement.execute();

        } catch (SQLException e) {
            logger.error("SQL exception has been thrown.", e);
        }

        return false;
    }

    @Override
    public int insertRequestedValue (String value) {
        String query = "insert into `results`.`prime_numbers` (`value`) values (?); select last_insert_id() as id;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, value);
            statement.execute();

            if (statement.getMoreResults()) {
                try (ResultSet resultSet = statement.getResultSet()) {
                    if (resultSet.first()) {
                        return resultSet.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            logger.error("SQL exception has been thrown.", e);
        }

        return -1;
    }

    @Override
    public List<ResultModel> getResultByValue (String value) {
        try (PreparedStatement statement = getQueryStatement("SELECT * FROM `prime_numbers` WHERE `value` = ? ;")) {
            statement.setString(1, value);

            try (ResultSet resultSet = statement.executeQuery()) {
                return fetchModelsList(resultSet);
            }

        } catch (SQLException e) {
            logger.error("SQL exception has been thrown.", e);
        }

        return null;
    }

    @Override
    public ResultModel getResultById(int id) {
        try (PreparedStatement statement = getQueryStatement("SELECT * FROM `prime_numbers` WHERE `id` = ? ;")) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                List<ResultModel> models = fetchModelsList(resultSet);
                return !models.isEmpty() ? models.get(0) : null;
            }

        } catch (SQLException e) {
            logger.error("SQL exception has been thrown.", e);
        }

        return null;
    }

    private PreparedStatement getQueryStatement(String query) throws SQLException {
        return connection.prepareCall(query);
    }

    private List<ResultModel> fetchModelsList (ResultSet resultSet) throws SQLException {
        List<ResultModel> models = new ArrayList<>();
        while (resultSet.next()) {
            ResultModel model = fetchModel(resultSet);
            models.add(model);
        }

        return models;
    }

    private ResultModel fetchModel (ResultSet resultSet) throws SQLException {
        ResultModel model = new ResultModel();

        model.setId(resultSet.getInt(1));
        model.setValue(resultSet.getString(2));
        model.setPrimeNumbers(new Gson().fromJson(resultSet.getString(3), new TypeToken<List<Integer>>(){}.getType()));
        model.setCreateTime(new Date(resultSet.getTimestamp(4).getTime()));

        return model;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
