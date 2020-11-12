import java.io.IOException;

import java.io.*;
import java.net.*;

class Machine {
  public static void main(String[] args) {
    try {
      Boolean isHost;
      int nextServer;
      int ownServer;
      Socket clientSocket = null;
      Socket connectionSocket = null;
      ServerSocket serverSocket = null;
      BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in));

      System.out.println("Digite 1 para ser HOST e qualquer outro valor caso contrário");
      isHost = Integer.parseInt(fromUser.readLine()) == 1;

      if (isHost) {
        System.out.println("Digite a porta do próximo servidor:");
        nextServer = Integer.parseInt(fromUser.readLine());

        System.out.println("Digite a porta para esperar uma conexão:");
        ownServer = Integer.parseInt(fromUser.readLine());

        serverSocket = new ServerSocket(ownServer);
        clientSocket = new Socket("localhost", nextServer);

        System.out.println("Aguardando conexão...");
        connectionSocket = serverSocket.accept();
      } else {
        System.out.println("Digite a porta para esperar uma conexão:");
        ownServer = Integer.parseInt(fromUser.readLine());
        serverSocket = new ServerSocket(ownServer);

        System.out.println("Aguardando conexão...");
        connectionSocket = serverSocket.accept();

        System.out.println("Digite a porta do próximo servidor:");
        nextServer = Integer.parseInt(fromUser.readLine());
        clientSocket = new Socket("localhost", nextServer);
      }
      ObjectOutputStream toServer = new ObjectOutputStream(clientSocket.getOutputStream());
      ObjectInputStream fromClient = new ObjectInputStream(connectionSocket.getInputStream());

      while (true) {
        if (isHost) {
          System.out.println("O que você gostaria de fazer?\n1. Enviar uma mensagem\n2. Enviar o token\n3. Sair");
          int choice = Integer.parseInt(fromUser.readLine());

          int targetServer;
          MachinesProtocol data;

          switch (choice) {
            case 1:
              System.out.println("Qual a mensagens que você gostaria de enviar?");
              String message = fromUser.readLine();

              System.out.println("Para qual servidor você gostaria de enviar a mensagem?");
              targetServer = Integer.parseInt(fromUser.readLine());

              data = new MachinesProtocol(message, targetServer);
              toServer.writeObject(data);
              break;
            case 2:
              System.out.println("Para qual servidor você gostaria de enviar o token?");
              targetServer = Integer.parseInt(fromUser.readLine());

              data = new MachinesProtocol(targetServer);
              toServer.writeObject(data);
              isHost = false;
              break;
            case 3:
              toServer.writeObject(null);
              clientSocket.close();
              serverSocket.close();
              break;
          }

          if (choice == 3) {
            System.out.println("Fechando servidor...");
            break;
          }

          fromClient.readObject();
        } else {
          System.out.println("Aguardando dados...");
          MachinesProtocol data = (MachinesProtocol) fromClient.readObject();

          if (data == null) {
            System.out.println("Fechando servidor...");
            toServer.writeObject(data);
            clientSocket.close();
            serverSocket.close();
            break;
          } else if (data.getDestination() == ownServer) {
            if (data.getIsSendingToken()) {
              System.out.println("Token recebido com sucesso!");
              isHost = true;
            } else {
              System.out.println("Mensagens recebida com sucesso: " + data.getMessage());
            }
          } else {
            System.out.println("Mensagem não é para mim, repassando...");
          }

          toServer.writeObject(data);
        }
      }
    } catch (IOException | ClassNotFoundException error) {
      error.printStackTrace();
    }
  }
}