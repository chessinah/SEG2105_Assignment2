package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.util.Scanner;

import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  
  public void handleMessageFromClient(Object msg, ConnectionToClient client) {
	    String message = msg.toString().trim();

	    
	    if (client.getInfo("loginID") == null && message.startsWith("#login")) {
	        System.out.println("A new client has connected to the server.");
	        System.out.println("Message received: " + message + " from null.");

	        String[] parts = message.split(" ", 2);
	        if (parts.length < 2 || parts[1].trim().isEmpty()) {
	            try {
	                client.sendToClient("ERROR: Login ID missing. Usage: #login <loginID>");
	                client.close();
	            } catch (Exception e) {
	                System.out.println("Error closing client after invalid login: " + e);
	            }
	            return;
	        }

	        String loginID = parts[1].trim();
	        client.setInfo("loginID", loginID);

	        System.out.println(loginID + " has logged on.");

	        try {
	            client.sendToClient(loginID + " has logged on.");
	        } catch (Exception e) {
	            System.out.println("Error sending confirmation to client: " + e);
	        }

	        return;
	    }

	    
	    String loginID = (String) client.getInfo("loginID");
	    if (loginID == null) {
	        try {
	            client.sendToClient("ERROR: You must login first using #login <loginID>");
	            client.close();
	        } catch (Exception e) {
	            System.out.println("Error closing unlogged client: " + e);
	        }
	        return;
	    }

	    String newMessage = message;
	    System.out.println("Message received: " + newMessage + " from " + loginID);
	    this.sendToAllClients(loginID + ": " + newMessage);
	}


  
  public void handleMessageFromServerUI(String message)
  {
      try 
      {
          if (message.startsWith("#")) {
              handleCommand(message);
          } 
          else {
            
              String fullMsg = "SERVER MSG> " + message;

              System.out.println(fullMsg);

              sendToAllClients(fullMsg);
          }
      } 
      catch (Exception e) {
          System.out.println("Error handling server input: " + e);
      }
  }

  
  private void handleCommand(String cmd)
  {
    try {
      if (cmd.equals("#quit")) {
        stopListening();
        close();
        System.out.println("Server shutting down.");
        System.exit(0);
      }

      else if (cmd.equals("#stop")) {
        stopListening();
        // DON'T print "Server has stopped listening for connections." here —
        // serverStopped() will already print that once.
      }

      else if (cmd.equals("#close")) {
        // stop listening; serverStopped() will print the "stopped listening" message
        stopListening();

        // Close will disconnect all clients. Let clientDisconnected() print each client's line.
        close();

        // Print a single summary line for the action
        System.out.println("Server closed. Disconnected all clients.");
      }

      else if (cmd.startsWith("#setport")) {
        if (isListening() || getNumberOfClients() > 0) {
          System.out.println("Error! Server must be closed to set port.");
        } else {
          String[] parts = cmd.split(" ");
          setPort(Integer.parseInt(parts[1]));
          System.out.println("Port set to " + getPort());
        }
      }

      else if (cmd.equals("#start")) {
        if (isListening()) {
          System.out.println("Server is already running.");
        } else {
          listen();
          System.out.println("Server started listening again.");
        }
      }

      else if (cmd.equals("#getport")) {
        System.out.println("Current port: " + getPort());
      }

    }
    catch(Exception e) {
      System.out.println("Command error: " + e.getMessage());
    }
  }


  
  @Override
  protected void clientConnected(ConnectionToClient client) 
  {
    System.out.println("Client connected: " + client);
  }
  
  @Override
  protected void clientDisconnected(ConnectionToClient client) 
  {
    Object id = client.getInfo("loginID");
    if (id != null) {
      System.out.println(id + " has disconnected.");
    } else {
      System.out.println("A client has disconnected.");
    }
  }


    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server stopped listening for new clients.");
  }
  
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = DEFAULT_PORT; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
    	
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
      return;
    }
    Scanner consoleScanner = new Scanner(System.in);
    boolean running = true;

    while (running) {
        String input = consoleScanner.nextLine().trim();
        if (input.equalsIgnoreCase("#quit")) {
            // Graceful shutdown
            try {
                if (sv.isListening()) {
                    sv.stopListening(); // stop accepting new connections
                }
            } catch (Exception e) {
                System.out.println("Error stopping listening: " + e.getMessage());
            }
            try {
                sv.close(); // disconnect existing clients and close server
            } catch (Exception e) {
                System.out.println("Error closing server: " + e.getMessage());
            }
            System.out.println("Server shutting down gracefully.");
            running = false; // break loop
        } else {
            // Let the server UI handler process commands (it already handles #start, #setport, etc.)
            sv.handleMessageFromServerUI(input);
        }
    }

    consoleScanner.close();
    System.exit(0);

}
}
//End of EchoServer class
