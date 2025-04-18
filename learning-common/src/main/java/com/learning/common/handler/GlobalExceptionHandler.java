package com.learning.common.handler;

import com.learning.common.entity.Result;
import com.learning.common.entity.ResultStatus;
import com.learning.common.exception.BusinessException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author 张家伟
 * @date 2025/04/04
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理器
     */
    @ExceptionHandler(BusinessException.class)
    public ResultStatus businessExceptionHandler(BusinessException e) {
        return e.getStatus();
    }

    /**
     * 非法参数异常处理器
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<String> illegalArgumentExceptionHandler(Exception e) {
        return Result.of(ResultStatus.ARGUMENT_NOT_VALID, e.getMessage());
    }

    /**
     * 参数无效异常处理器
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<List<String>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        return Result.of(ResultStatus.ARGUMENT_NOT_VALID, e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList()));
    }

}
