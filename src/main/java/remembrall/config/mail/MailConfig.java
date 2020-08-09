package remembrall.config.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Arrays;
import java.util.Properties;

/**
 * Configures mail support. Uses 'mail.host' and 'mail.port' from properties file for
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

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        boolean runningIde = Arrays.asList(env.getActiveProfiles()).contains("ide");

        String user = env.getProperty("mail.user");
        String host = env.getProperty("mail.host");
        int port = 587;

        if (env.getProperty("mail.port") != null && !env.getProperty("mail.port").isEmpty()) {
            port = env.getProperty("mail.port", Integer.class);
        }

        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(user);
        mailSender.setPassword(env.getProperty("mail.password"));

        if (runningIde) {
            logger.info("Skipping to set auth and TLS parameters as runnning in IDE.");
        } else {
            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
        }

        logger.info("Mail host: {}", host);
        logger.info("Mail port: {}", port);
        logger.info("Mail user: {}", user);

        return mailSender;
    }
}
