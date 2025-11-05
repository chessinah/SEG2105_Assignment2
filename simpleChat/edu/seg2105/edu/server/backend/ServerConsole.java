package edu.seg2105.edu.server.backend;

import java.util.Scanner;
import edu.seg2105.client.common.ChatIF;

public class ServerConsole implements ChatIF 
{
  final public static int DEFAULT_PORT = 5555;

  EchoServer server;
  Scanner input;

  public ServerConsole(EchoServer server) 
  {
    this.server = server;
    input = new Scanner(System.in);
    new Thread(() -> accept()).start();
  }

  public void accept()
  {
    try {
      String message;
      while (true) {
        message = input.nextLine();
        server.handleMessageFromServerUI(message);
      }
    } 
    catch (Exception ex) {
      System.out.println("Unexpected error reading server console input!");
    }
  }

  @Override
  public void display(String message) 
  {
    System.out.println("> " + message);
  }
}
