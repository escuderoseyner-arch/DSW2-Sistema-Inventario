package com.inventario.msalertas.server;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorAlertas implements Runnable {

    private int puerto;

    public ServidorAlertas(int puerto) {
        this.puerto = puerto;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor de alertas escuchando en puerto " + puerto);
            while (true) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clienteSocket.getRemoteSocketAddress());
                // Cada cliente se maneja en un hilo separado
                Thread hilo = new Thread(new ManejadorCliente(clienteSocket));
                hilo.start();
            }
        } catch (Exception e) {
            System.out.println("Error en servidor de alertas: " + e.getMessage());
        }
    }
}
