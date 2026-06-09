package cl.duoc.ejemplo.ms.administracion.archivos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.duoc.ejemplo.ms.administracion.archivos.service.GuiaDespachoService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class DemoDataConfig {

    @Bean
    CommandLineRunner cargarGuiasDemo(
            GuiaDespachoService guiaDespachoService,
            @Value("${app.demo.crear-guias:true}") boolean crearGuiasDemo) {
        return args -> {
            if (!crearGuiasDemo) {
                log.info("Carga automática de guías demo desactivada");
                return;
            }

            try {
                guiaDespachoService.crearGuiasDemo();
                log.info("Guías demo creadas automáticamente en EFS y S3");
            } catch (Exception e) {
                log.warn("No se pudieron crear las guías demo automáticamente: {}", e.getMessage());
            }
        };
    }
}
