package web_app.services.exceptions;

import org.jetbrains.annotations.NotNull;
import web_app.services.models.ErrorInfoModel;

import java.io.Serializable;

public class NotFoundException extends Exception implements Serializable {

    private static final long serialVersionUID = 4849603993673454597L;

    private ErrorInfoModel errorInfo;

    public NotFoundException() { super(); }

    public NotFoundException(String what) { super(what); }

    public NotFoundException(Throwable cause) { super(cause); }

    public NotFoundException(String what, Throwable cause) { super(what, cause); }

    public NotFoundException(@NotNull ErrorInfoModel errorInfo) {
        this(errorInfo.getMessage());

        this.errorInfo = errorInfo;
    }

    public ErrorInfoModel getErrorInfo() {
        return errorInfo;
    }
}
