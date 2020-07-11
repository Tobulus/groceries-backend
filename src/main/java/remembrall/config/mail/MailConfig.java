package remembrall.config.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Configures mail support. Uses 'spring.mail.host' and 'spring.mail.port' from properties file for
 * host and port information.
 * <p></p>
 * Expect to read the credentials for mailing from a file '/data/mail/mail.conf'.
 */
@Configuration
@PropertySource(value = "file:/data/mail/mail.conf", ignoreResourceNotFound = true)
public class MailConfig {

    Logger logger = LoggerFactory.getLogger(MailConfig.class);

    @Autowired
    private Environment env;

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(env.getProperty("mail.host"));
        mailSender.setPort(env.getProperty("mail.port", Integer.class));
        mailSender.setUsername(env.getProperty("mail.user"));
        mailSender.setPassword(env.getProperty("mail.password"));

        logger.info("Mail host: {}", host);
        logger.info("Mail port: {}", port);
        logger.info("Mail user: {}", env.getProperty("mail.user"));

        return mailSender;
    }
}
