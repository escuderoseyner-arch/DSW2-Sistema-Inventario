package com.inventario.msalertas.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ManejadorCliente implements Runnable {

    private Socket clienteSocket;

    public ManejadorCliente(Socket clienteSocket) {
        this.clienteSocket = clienteSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clienteSocket.getOutputStream(), true);

            out.println("Conectado al servidor de alertas de inventario");
            out.println("Escribe 'CONSULTAR' para verificar stock bajo o 'SALIR' para desconectar");

            String mensaje;
            while ((mensaje = in.readLine()) != null) {
                if ("SALIR".equalsIgnoreCase(mensaje.trim())) {
                    out.println("Desconectado. Hasta luego.");
                    break;
                } else if ("CONSULTAR".equalsIgnoreCase(mensaje.trim())) {
                    // TODO: Consultar ms-productos para productos con stock bajo
                    out.println("ALERTA: Consulta de stock bajo - funcionalidad pendiente de integrar con ms-productos");
                } else {
                    out.println("Comando no reconocido. Use CONSULTAR o SALIR.");
                }
            }

            clienteSocket.close();
        } catch (Exception e) {
            System.out.println("Error manejando cliente: " + e.getMessage());
        }
    }
}
