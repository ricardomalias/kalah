package com.game.kalah.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageUtil {

    private final MessageSource messageSource;

    public MessageUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String code) {
        System.out.println(code);
        return "abidul";
//        try {
//            Locale locale = LocaleContextHolder.getLocale();
//            System.out.println("locale");
//            System.out.println(locale.getCountry());
//            return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
//        } catch (Exception e) {
//            return code;
//        }
    }

    public String getMessage(String code, Object[] args) {
        try {
            return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return code;
        }
    }
}
