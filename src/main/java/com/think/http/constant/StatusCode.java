package com.think.http.constant;

/**
 * Status code object.
 *
 * @author veione
 */
public enum StatusCode {
    SUCCESS(0, "success"),
    SERVER_INTER_ERROR(1000, "Server internal error"),
    API_NOT_FOUND(1001, "Api can't be found"),
    REQUEST_METHOD_NOT_ALLOWED(1002, "Request method not allowed"),
    REQUEST_PARAMETER_ERROR(1003, "Missing parameters"),
    REQUEST_NO_AUTH(1004, "Request forbidden"),
    REQUEST_PERMISSION_DENIED(1005, "Permission denied");

    public int code;
    public String msg;

    StatusCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
