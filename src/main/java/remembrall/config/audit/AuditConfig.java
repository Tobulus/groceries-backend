package remembrall.config.audit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configures auditing of database entities.
 */
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@Configuration
public class AuditConfig {
    @Bean
    public AuditorAware<Long> auditorProvider() {
        return new Auditor();
    }
}
