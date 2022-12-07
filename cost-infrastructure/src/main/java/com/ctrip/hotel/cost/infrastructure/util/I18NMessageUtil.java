package com.ctrip.hotel.cost.infrastructure.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class I18NMessageUtil {

  private static MessageSource messageSource;

  public I18NMessageUtil(MessageSource messageSource) {
    I18NMessageUtil.messageSource = messageSource;
  }

  public static final String getMessage(String key) {
    try {
      return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    } catch (Exception e) {
      return "";
    }
  }
}
