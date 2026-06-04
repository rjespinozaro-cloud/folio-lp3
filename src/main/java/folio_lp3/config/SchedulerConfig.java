package folio_lp3.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuración para habilitar el Scheduler
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
    // Las tareas programadas en @Scheduled se ejecutarán automáticamente
}
