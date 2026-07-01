package folio_lp3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // 🚀 Importación crítica

@SpringBootApplication
@EnableScheduling // 🔥 Habilita el soporte para tareas automatizadas (@Scheduled)
public class FolioLp3Application {

    public static void main(String[] args) {
        SpringApplication.run(FolioLp3Application.class, args);
    }

}