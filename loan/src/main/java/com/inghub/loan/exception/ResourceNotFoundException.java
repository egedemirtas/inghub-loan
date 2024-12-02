package com.inghub.loan.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, String uniqueId, MessageSource msgSource) {
        super(resourceName.concat(msgSource.getMessage("loan.exception.record.not.found", null,
                LocaleContextHolder.getLocale())).concat(uniqueId));
    }

}
