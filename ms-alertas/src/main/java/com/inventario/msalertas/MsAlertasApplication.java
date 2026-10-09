package com.inventario.msalertas;

import com.inventario.msalertas.server.ServidorAlertas;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MsAlertasApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsAlertasApplication.class, args);
    }

    @Bean
    CommandLineRunner iniciarServidorSocket(@Value("${socket.server.port}") int puerto) {
        return args -> {
            ServidorAlertas servidor = new ServidorAlertas(puerto);
            Thread hiloServidor = new Thread(servidor);
            hiloServidor.setDaemon(true);
            hiloServidor.start();
            System.out.println("Servidor de alertas TCP iniciado en puerto " + puerto);
        };
    }
}
