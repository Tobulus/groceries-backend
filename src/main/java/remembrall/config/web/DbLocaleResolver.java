package remembrall.config.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import remembrall.model.User;
import remembrall.model.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public class DbLocaleResolver {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void updateLang(HttpServletRequest request, String username) {
        String localeCode = request.getParameter("locale");

        if (localeCode == null) {
            localeCode = request.getHeader("locale");
        }

        if (localeCode != null && !localeCode.isEmpty()) {
            Locale locale = Locale.forLanguageTag(localeCode);
            userRepository.updateLangFor(username, locale.getLanguage());
        }
    }

    public Locale getLocal(Long userId) {
        return userRepository.findById(userId).map(User::getLang).map(Locale::forLanguageTag).orElse(Locale.ENGLISH);
    }
}
