package com.inghub.loan.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class LimitNotSufficientException extends RuntimeException {
    public LimitNotSufficientException(String resourceName, String uniqueId, MessageSource msgSource) {
        super(resourceName +
                msgSource.getMessage("loan.exception.limit.not.sufficient", null, LocaleContextHolder.getLocale())
                        .concat(uniqueId));
    }
}
