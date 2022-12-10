package com.ctrip.hotel.cost.infrastructure.util;

import hotel.settlement.common.LogHelper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class I18NMessageUtil {

  private static MessageSource messageSource;

  public I18NMessageUtil(MessageSource messageSource) {
    I18NMessageUtil.messageSource = messageSource;
  }

  public static final String getMessage(String key) {
    try {
      return messageSource.getMessage(key, null, key, Locale.CHINA);
    } catch (Exception e) {
      LogHelper.logError("I18NMessageUtil Error", e);
      return "I18NMessageUtil Error";
    }
  }
}
