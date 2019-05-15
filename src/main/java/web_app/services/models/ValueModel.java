package web_app.services.models;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "Value")
public class ValueModel {
    private int id;
    private int value;

    public ValueModel(int id, int value) {
        this.id = id;
        this.value = value;
    }
}
