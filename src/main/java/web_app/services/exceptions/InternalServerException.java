package web_app.services.exceptions;

import web_app.services.models.BaseResponseModel;

public class InternalServerException extends BaseException {

    public InternalServerException() { super(); }

    public InternalServerException(String what) { super(what); }

    public InternalServerException(Throwable throwable) { super(throwable); }

    public InternalServerException(String what, Throwable throwable) { super(what, throwable); }

    public InternalServerException(BaseResponseModel baseResponseModel) {
        super(baseResponseModel);
    }

    public InternalServerException(String what, BaseResponseModel baseResponseModel) {
        super(what, baseResponseModel);
    }

    public InternalServerException(Throwable throwable, BaseResponseModel baseResponseModel) {
        super(throwable, baseResponseModel);
    }

    public InternalServerException(String what, Throwable throwable, BaseResponseModel baseResponseModel) {
        super(what, throwable, baseResponseModel);
    }
}
