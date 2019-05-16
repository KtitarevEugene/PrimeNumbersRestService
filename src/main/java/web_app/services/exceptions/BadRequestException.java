package web_app.services.exceptions;

import org.jetbrains.annotations.NotNull;
import web_app.services.models.ErrorInfoModel;

public class BadRequestException extends Exception {

    private ErrorInfoModel errorInfo;

    public BadRequestException() { super(); }

    public BadRequestException(String what) { super(what); }

    public BadRequestException(Throwable throwable) { super(throwable); }

    public BadRequestException(String what, Throwable throwable) { super(what, throwable); }

    public BadRequestException(@NotNull ErrorInfoModel errorInfo) {
        this(errorInfo.getMessage());

        this.errorInfo = errorInfo;
    }

    public ErrorInfoModel getErrorInfo() {
        return errorInfo;
    }
}
