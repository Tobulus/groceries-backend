package remembrall.config.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * Can be used to translate language keys to the current active locale.
 */
public class I18n {

    @Autowired
    private MessageSource messageSource;

    public String getMessage(String key, Object... params) {
        return messageSource.getMessage(key, params, LocaleContextHolder.getLocale());
    }

    public String getMessage(String key, Locale locale, Object... params) {
        return messageSource.getMessage(key, params, locale);
    }
}
