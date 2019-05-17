package web_app.services.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "ResultResponse")
public class ResultResponseModel extends BaseResponseModel {

    @XmlElement(name = "data", nillable = true)
    private ValueResultModel data;

    public ResultResponseModel(int status, String reasonPhrase) {
        super(status, reasonPhrase);
    }

    public ResultResponseModel(int status, String reasonPhrase, ValueResultModel data) {
        super(status, reasonPhrase);
        this.data = data;
    }
}
