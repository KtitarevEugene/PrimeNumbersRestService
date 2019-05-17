package web_app.services.exceptions;

import web_app.services.models.BaseResponseModel;

public class BadRequestException extends BaseException {

    public BadRequestException() { super(); }

    public BadRequestException(String what) { super(what); }

    public BadRequestException(Throwable throwable) { super(throwable); }

    public BadRequestException(String what, Throwable throwable) { super(what, throwable); }

    public BadRequestException(BaseResponseModel baseResponseModel) {
        super(baseResponseModel);
    }

    public BadRequestException(String what, BaseResponseModel baseResponseModel) {
        super(what, baseResponseModel);
    }

    public BadRequestException(Throwable throwable, BaseResponseModel baseResponseModel) {
        super(throwable, baseResponseModel);
    }

    public BadRequestException(String what, Throwable throwable, BaseResponseModel baseResponseModel) {
        super(what, throwable, baseResponseModel);
    }
}
