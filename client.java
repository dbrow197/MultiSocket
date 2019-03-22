package ProjectOne;

// A Java program for a Client
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class client extends JFrame {

    // initialize socket and input output streams
    private Socket socket = null;
    private ServerSocket server = null;
    private ObjectInputStream in = null;
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream out = null;
    private String line = "Test";
    private String serverIP;


    public client(String host) {
        super("Client Program for messenger ");
        serverIP = host;
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        sendMessage(event.getActionCommand());
                        userText.setText("");
                    }
                }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(800, 600);
        setVisible(true);
    }

    public void beginTransmission() {
        try {
            server = new ServerSocket(5000, 4);
            Message("\n" + "Server started");

            while (true) {
                try {
                    standBy();
                    setUp();
                    chat();
                } catch (EOFException eofException) {
                    Message("\n" + "Ending by Client! ");
                } finally {
                    closeServer();
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    private void standBy() throws IOException {
        Message("\n" + "Attempting connection ...");
        socket = new Socket(InetAddress.getByName(serverIP), 5000);
        Message("\n" + "Connected!.. Now talking to: " +
                socket.getInetAddress().getHostName());
    }


    private void setUpType(final boolean check) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        userText.setEditable(check);
                    }
                }
        );
    }

    private void setUp() throws IOException {

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

    private void chat() throws IOException {
        System.out.println("you are now ready to chat!");
        setUpType(true);
        sendMessage(line);

        do {
            try {
                line = (String) in.readObject();

                Message("\n" + line);
            } catch (ClassNotFoundException classNotFoundException) {
                Message("\n" + "Cannot get retrieve message from user.");
            }

        } while (!line.equals("Over"));


    }

    private void closeServer() {
        Message("\n" + "Closing connection");
        setUpType(false);
        // close connection
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    private void Message(final String line) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        chatWindow.append(line);

                    }
                }
        );
    }


    private void sendMessage(String line){
        try{
            out.writeObject("\n Client - " + line);
            out.flush();
            Message("\n Client - " + line);
        }catch(IOException ioException){
            chatWindow.append("\n Cannot send Message");
        }
    }
}

    /* constructor to put ip address and port
    public client(String address, int port)
    {
        // establish a connection
        try
        {
            socket = new Socket(address, port);
            System.out.println("Connected");

            // takes input from terminal
            input = new DataInputStream(System.in);

            // sends output to the socket
            out = new DataOutputStream(socket.getOutputStream());
        }
        catch(UnknownHostException u)
        {
            System.out.println(u);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }

        // string to read message from input
        String line = "";

        // keep reading until "Over" is input
        while (!line.equals("Over"))
        {
            try
            {
                line = input.readUTF();
                out.writeUTF(line);
            }
            catch(IOException i)
            {
                System.out.println(i);
            }
        }

        // close the connection
        try
        {
            input.close();
            out.close();
            socket.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }

    public static void main(String args[])
    {
        client client = new client("192.168.1.20", 5000);
    }
}
*/