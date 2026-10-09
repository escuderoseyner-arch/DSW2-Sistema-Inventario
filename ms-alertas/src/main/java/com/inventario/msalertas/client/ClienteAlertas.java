package com.inventario.msalertas.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClienteAlertas {
    public static void main(String[] args) {
        String host = "localhost";
        int puerto = 9090;

        try (Socket socket = new Socket(host, puerto);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            // Leer mensajes de bienvenida
            System.out.println(in.readLine());
            System.out.println(in.readLine());

            String entrada;
            while (true) {
                System.out.print("> ");
                entrada = scanner.nextLine();
                out.println(entrada);
                String respuesta = in.readLine();
                System.out.println("Servidor: " + respuesta);
                if ("SALIR".equalsIgnoreCase(entrada.trim())) break;
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
