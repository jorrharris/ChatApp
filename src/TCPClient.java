
// Java implementation for multithreaded chat client 
// Save file as Client.java 
  
import java.io.*; 
import java.net.*; 
import java.util.Scanner; 
  
public class TCPClient  
{  
  
    public static void main(String args[]) throws UnknownHostException, IOException  
    { 
        Scanner scn = new Scanner(System.in); 
        System.out.println("Enter Port: (Server is set to 1234)");
        String portString = scn.nextLine();
        int portInt = Integer.parseInt(portString);
        
        System.out.println("Enter Name: ");
        String name = scn.nextLine();
          
        // getting localhost ip 
        InetAddress ip = InetAddress.getByName("localhost"); 
          
        // establish the connection 
        Socket s = new Socket(ip, portInt); 
        System.out.println("Connected to Server!");
          
        // obtaining input and out streams 
        DataInputStream input = new DataInputStream(s.getInputStream()); 
        DataOutputStream output = new DataOutputStream(s.getOutputStream()); 
        output.writeUTF(name);
          
        // readMessage thread 
        Thread readMessage = new Thread(new Runnable()  
        { 
            @Override
            public void run() { 
  
                while (true) { 
                    try { 
                        // read the message sent to this client 
                        String msg = input.readUTF(); 
                        System.out.println(msg); 
                    } catch (IOException e) { 
  
                        e.printStackTrace(); 
                    } 
                } 
            } 
        }); 
        
        // sendMessage thread 
        Thread sendMessage = new Thread(new Runnable()  
        { 
            @Override
            public void run() { 
                while (true) { 
  
                    // read the message to deliver. 
                    String msg = scn.nextLine();                   
                    try { 
                        // write on the output stream 
                        output.writeUTF(msg);
                        if (msg.toUpperCase().equals("QUIT")) {
                        	System.out.println("Leaving Chat");
                        	System.exit(0);
                        }
                    } catch (IOException e) { 
                        e.printStackTrace(); 
                    } 
                } 
            } 
        }); 
  
        sendMessage.start(); 
        readMessage.start(); 
  
    } 
} 