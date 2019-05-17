package web_app.services.exceptions;

import web_app.services.models.BaseResponseModel;

public class BaseException extends Exception {

    private BaseResponseModel baseResponseModel;

    public BaseException() { super(); }

    public BaseException(String what) { super(what); }

    public BaseException(Throwable throwable) { super(throwable); }

    public BaseException(String what, Throwable throwable) { super(what, throwable); }

    public BaseException(BaseResponseModel baseResponseModel) {
        super();
        this.baseResponseModel = baseResponseModel;
    }

    public BaseException(String what, BaseResponseModel baseResponseModel) {
        super(what);
        this.baseResponseModel = baseResponseModel;
    }

    public BaseException(Throwable throwable, BaseResponseModel baseResponseModel) {
        super(throwable);
        this.baseResponseModel = baseResponseModel;
    }

    public BaseException(String what, Throwable throwable, BaseResponseModel baseResponseModel) {
        super(what, throwable);
        this.baseResponseModel = baseResponseModel;
    }

    public BaseResponseModel getBaseResponseModel() {
        return baseResponseModel;
    }

    public void setBaseResponseModel(BaseResponseModel baseResponseModel) {
        this.baseResponseModel = baseResponseModel;
    }
}
