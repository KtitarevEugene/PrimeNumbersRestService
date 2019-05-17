package web_app.services.exceptions;

import web_app.services.models.BaseResponseModel;

public class NotFoundException extends BaseException {

    public NotFoundException() { super(); }

    public NotFoundException(String what) { super(what); }

    public NotFoundException(Throwable cause) { super(cause); }

    public NotFoundException(String what, Throwable cause) { super(what, cause); }

    public NotFoundException(BaseResponseModel baseResponseModel) {
        super(baseResponseModel);
    }

    public NotFoundException(String what, BaseResponseModel baseResponseModel) {
        super(what, baseResponseModel);
    }

    public NotFoundException(Throwable throwable, BaseResponseModel baseResponseModel) {
        super(throwable, baseResponseModel);
    }

    public NotFoundException(String what, Throwable throwable, BaseResponseModel baseResponseModel) {
        super(what, throwable, baseResponseModel);
    }
}
