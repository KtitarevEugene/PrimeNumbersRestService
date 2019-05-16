package web_app.services.exceptions;

import org.jetbrains.annotations.NotNull;
import web_app.services.models.ErrorInfoModel;

public class InternalServerException extends Exception {

    private ErrorInfoModel errorInfo;

    public InternalServerException() { super(); }

    public InternalServerException(String what) { super(what); }

    public InternalServerException(Throwable throwable) { super(throwable); }

    public InternalServerException(String what, Throwable throwable) { super(what, throwable); }

    public InternalServerException(@NotNull ErrorInfoModel errorInfo) {
        this(errorInfo.getMessage());

        this.errorInfo = errorInfo;
    }

    public ErrorInfoModel getErrorInfo() {
        return errorInfo;
    }
}
