package com.campus.trade.exception;

import com.campus.trade.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.ConnectException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String DB_CONN_HINT =
            "无法连接 MySQL：请先启动本机数据库服务（如终端执行 brew services start mysql 或 mysql.server start），"
                    + "并核对 backend/src/main/resources/application.yml 中的 url、用户名与密码。";

    private static boolean isDbConnectionFailure(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof CannotGetJdbcConnectionException || t instanceof ConnectException) {
                return true;
            }
            String m = t.getMessage();
            if (m != null && (m.contains("Communications link failure") || m.contains("Connection refused"))) {
                return true;
            }
        }
        return false;
    }

    @ExceptionHandler(BusinessException.class)
    public Result handleBusiness(BusinessException e) {
        return Result.error(e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result handleValidate(Exception e) {
        FieldError fieldError = null;
        if (e instanceof MethodArgumentNotValidException) {
            fieldError = ((MethodArgumentNotValidException) e).getBindingResult().getFieldError();
        } else if (e instanceof BindException) {
            fieldError = ((BindException) e).getBindingResult().getFieldError();
        }
        String msg = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        return Result.error(msg);
    }

    @ExceptionHandler(CannotGetJdbcConnectionException.class)
    public Result handleJdbcConn(CannotGetJdbcConnectionException e) {
        log.error("JDBC connection failed", e);
        return Result.error(DB_CONN_HINT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result handleIllegalArgument(IllegalArgumentException e) {
        log.warn("IllegalArgumentException: {}", e.getMessage());
        if (e.getMessage() != null && e.getMessage().contains("rawPassword")) {
            return Result.error("登录请求未携带有效密码，请检查客户端是否提交 password 字段");
        }
        return Result.error(e.getMessage() != null ? e.getMessage() : "参数无效");
    }

    @ExceptionHandler(MyBatisSystemException.class)
    public Result handleMyBatis(MyBatisSystemException e) {
        if (isDbConnectionFailure(e)) {
            log.error("MyBatis: database unreachable", e);
            return Result.error(DB_CONN_HINT);
        }
        log.error("MyBatis system exception", e);
        return Result.error("数据库访问异常，请稍后再试");
    }

    @ExceptionHandler(Exception.class)
    public Result handleOther(Exception e) {
        if (isDbConnectionFailure(e)) {
            log.error("Database unreachable", e);
            return Result.error(DB_CONN_HINT);
        }
        log.error("Unhandled exception", e);
        return Result.error("系统繁忙，请稍后再试");
    }
}
