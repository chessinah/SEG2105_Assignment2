// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  private String loginID;


  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    
    //openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }
  
  @Override
  protected void connectionEstablished() {
    try {
      sendToServer("#login " + loginID);
    } catch (IOException e) {
      clientUI.display("Error: Could not send login ID to server.");
    }
  }


  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
	  if (message.startsWith("#"))
	    {
	      handleCommand(message);
	    }
	    else 
	    {
    try
    {
      sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
    }
  }
  
  /** Handles all #commands */
  private void handleCommand(String cmd)
  {
    try 
    {
      if (cmd.equals("#quit"))
      {
        clientUI.display("Quitting client...");
        quit();
      }

      else if (cmd.equals("#logoff"))
      {
        if (isConnected())
        {
          closeConnection();
          clientUI.display("Logged off from server.");
        }
        else clientUI.display("Already logged off.");
      }

      else if (cmd.startsWith("#sethost"))
      {
        if (isConnected())
        {
          clientUI.display("ERROR! You must log off before setting the host.");
        }
        else 
        {
          String[] parts = cmd.split(" ");
          if (parts.length == 2)
          {
            setHost(parts[1]);
            clientUI.display("Host set to " + parts[1]);
          }
          else clientUI.display("Using : #sethost <host>");
        }
      }

      else if (cmd.startsWith("#setport"))
      {
        if (isConnected())
        {
          clientUI.display("ERROR! You must log off before setting the port.");
        }
        else 
        {
          String[] parts = cmd.split(" ");
          if (parts.length == 2)
          {
            setPort(Integer.parseInt(parts[1]));
            clientUI.display("Port set to " + parts[1]);
          }
          else clientUI.display("Usage: #setport <port>");
        }
      }

      else if (cmd.equals("#login"))
      {
        if (isConnected())
        {
          clientUI.display("ERROR! Already connected.");
        }
        else
        {
          openConnection();
          clientUI.display("Reconnected to server.");
        }
      }

      else if (cmd.equals("#gethost"))
      {
        clientUI.display("Current host: " + getHost());
      }

      else if (cmd.equals("#getport"))
      {
        clientUI.display("Current port: " + getPort());
      }

      
    }
    catch (Exception e)
    {
      clientUI.display("Command error: " + e.getMessage());
    }
  }
  
  
  
  /**
   * This method terminates the client.
   */
  public void quit() {
	  try {
	    
	    if (isConnected()) {
	      try {
	        closeConnection();
	      } catch (IOException e) {
	        
	      }
	    }
	  } catch (Exception e) {
	    
	  }
	  System.exit(0);
	}
  
  
  @Override
  protected void connectionClosed() {
    clientUI.display("The server has shut down.");
    System.exit(0);
  }
  
  
  @Override
  protected void connectionException(Exception exception) {
    clientUI.display("The server has shut down.");
    System.exit(0);
  }
  
  
}
//End of ChatClient class
