import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime; 

// Server class 
public class TCPServer  
{ 
  
    // Vector to store active clients 
    static Vector<ClientHandler> ar = new Vector<>(); 
  
    public static void main(String[] args) throws IOException  
    { 
        // server is listening on port 1234 
    	ServerSocket ss = new ServerSocket(1234); 
          
        Socket s; 
          
        // running infinite loop for getting 
        // client request 
        while (true)  
        { 
            // Accept the incoming request 
            s = ss.accept();  
              
            // obtain input and output streams 
            DataInputStream dis = new DataInputStream(s.getInputStream()); 
            DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
            
            String name = dis.readUTF();
            System.out.println("New client request received : " + name);
              
            System.out.println("Creating a new handler for this client..."); 
  
            // Create a new handler object for handling this request. 
            ClientHandler mtch = new ClientHandler(s,name, dis, dos); 
  
            // Create a new Thread with this object. 
            Thread t = new Thread(mtch); 
              
            System.out.println("Adding this client to active client list"); 
  
            // add this client to active clients list 
            ar.add(mtch); 
  
            // start the thread. 
            t.start(); 
        } 
    } 
} 
  
// ClientHandler class 
class ClientHandler implements Runnable  
{ 
    Scanner scn = new Scanner(System.in); 
    private String name; 
    final DataInputStream dis; 
    final DataOutputStream dos; 
    Socket s; 
    boolean loggedOn;
      
    // constructor 
    public ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos) { 
        this.dis = dis; 
        this.dos = dos; 
        this.name = name; 
        this.s = s; 
        this.loggedOn = true;
    } 
  
    @Override
    public void run() { 
  
        String received; 
        while (loggedOn == true)  
        { 
            try
            { 
                // receive the string 
                received = dis.readUTF(); 
                
                //put a time stamp on the message
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");  
                LocalDateTime now = LocalDateTime.now();
                String time = dtf.format(now);
                  
                System.out.println(time + " " + this.name + " " + received); 
                  
                if(received.toUpperCase().equals("QUIT")){ 
                	this.loggedOn = false;
                    for (ClientHandler mc : TCPServer.ar)  
                    { 
                        // if the recipient is found, write on its 
                        // output stream 
                        if (!mc.name.equals(this.name) && mc.loggedOn == true)  
                        { 
                            mc.dos.writeUTF(time + " " + this.name + " left the chat."); 
                        } 
                    } 
                    break; 
                } 
  
                // search for the recipient in the connected devices list. 
                // ar is the vector storing client of active users 
                for (ClientHandler mc : TCPServer.ar)  
                { 
                    // if the recipient is found, write on its 
                    // output stream 
                    if (!mc.name.equals(this.name) && mc.loggedOn==true)  
                    { 
                        mc.dos.writeUTF(time+" "+this.name+" : "+received); 
                    } 
                } 
            } catch (IOException e) { 
                  
                e.printStackTrace(); 
            } 
              
        } 
        try
        { 
            // closing resources 
            this.dis.close(); 
            this.dos.close();
            this.s.close();
              
        }catch(IOException e){ 
            e.printStackTrace(); 
        } 
    } 
} 