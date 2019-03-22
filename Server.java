package ProjectOne;

// A Java program for a Server
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Server extends JFrame
{
    //initialize socket and input stream
    private Socket		 socket = null;
    private ServerSocket server = null;
    private ObjectInputStream in	 = null;
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream out = null;

    //Constructor and GUI
    public Server(){
        super("Instant Messenger");
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        sendMessage(event.getActionCommand());
                        userText.setText("");

                    }
                }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(800,600);
        setVisible(true);

    }

    public void beginTransmission()
    {
        try{
            server = new ServerSocket(5000, 4);
            Message("\n" + "Server started");

            while(true){
                try{
                    standBy();
                    setUp();
                    chat();
                }catch(EOFException eofException){
                    Message("\n" + "Ending by Server side! ");
                }finally{
                    closeServer();
                }
            }
        }catch(IOException ioException){
            ioException.printStackTrace();
        }

    }

    //pause for connection and print info to user to see who connects.
    private void standBy() throws IOException{
        Message("\n" + "Waiting for a client ...");
        socket = server.accept();
        Message("\n" + "Client accepted.. Talking to: " +
                socket.getInetAddress().getHostName());
    }

    //Creates pathway to connect to client
    private void setUp() throws IOException{

        // takes input from the client socket
        out = new ObjectOutputStream(
                new BufferedOutputStream(socket.getOutputStream()));
        out.flush();
        in = new ObjectInputStream(
                new BufferedInputStream(socket.getInputStream()));
        setUpType(true);
        Message("\n" + "I/O Streams established..");
        //String line = "";
    }

    private void chat() throws IOException{
        String line = "\n you are now ready to chat!";
        setUpType(true);
        sendMessage(line);


        do{
            try{
                line = (String) in.readObject();

                Message("\n" + line);
            }catch(ClassNotFoundException classNotFoundException){
                Message("\n" + "Cannot get retrieve message from user.");
            }
        }while(!line.equals("Client - Over"));


    }


    private void sendMessage(String line){
        try{
            out.writeObject("Server - " + line);
            out.flush();
            Message("\n Server - " + line);

        }catch(IOException ioException){
            chatWindow.append("\n Cannot send Message");
        }
    }


    private void setUpType(final boolean check){
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run(){
                        userText.setEditable(check);
                    }
                }
        );
    }
    //close socket
    private void closeServer(){
        Message("\n" + "Closing connection");
        setUpType(false);
        // close connection
        try {
            in.close();
            out.close();
            socket.close();
        }catch(IOException i)
        {
            System.out.println(i);
        }
    }

    //updates chat window
    private void Message(final String line){
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run(){
                        chatWindow.append(line);
                    }
                }
        );
    }


/*
    // constructor with port
    public Server(int port,String line)
    {


        // starts server and waits for a connection
        try
        {






            // reads message from client until "Over" is sent
            while (!line.equals("Over"))
            {
                try
                {
                    line = in.readUTF();
                    System.out.println(line);
                    sendMessage(line);
                    System.out.println(line);
*/

    public static void main(String args[])
    {
        Server server = new Server();
        server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        server.beginTransmission();
    }
}
