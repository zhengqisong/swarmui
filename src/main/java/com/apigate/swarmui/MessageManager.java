package com.apigate.swarmui;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component  
public class MessageManager {  
  
    private static MessageSource messageSource;   
  
    public static String getMsg(String key) {  
        Locale locale = LocaleContextHolder.getLocale();  
        return messageSource.getMessage(key, null, locale);  
    }  
  
    public static String getMsg(String key, String... arg) {  
        Locale locale = LocaleContextHolder.getLocale();  
        Object[] args = new Object[arg.length];  
        for (int i = 0; i < arg.length; i++) {  
            args[i] = arg[i];  
        }  
        return messageSource.getMessage(key, args, locale);  
    }  
  
    @Autowired(required = true)  
    public void setMessageSource(MessageSource messageSource) {  
        MessageManager.messageSource = messageSource;  
    }  
  
}  