package web_app.repository.db.db_models;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class ResultModel implements Serializable {

    private static final long serialVersionUID = 123L;

    private int id;
    private String value;
    private List<Integer> primeNumbers;
    private Date createTime;
}
