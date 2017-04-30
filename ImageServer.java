import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

import javax.swing.*;
import javax.swing.event.*;
import javax.imageio.ImageIO;

import java.util.*;
import java.net.*;

public class ImageServer extends JPanel{
    protected ImagePanel iPanel;
    protected JFileChooser FChooser = new JFileChooser();
    protected static JFrame FMain = new JFrame("Image Server");
    {
        iPanel = new ImagePanel();
        iPanel.setPreferredSize(new Dimension(600, 600));
        add(iPanel,BorderLayout.CENTER);

        JButton reloadButton = new JButton("Load another image");
        reloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getImage();
                revalidate();
                repaint();
            }
        });
        add(reloadButton,BorderLayout.SOUTH);
        getImage();
    }
    public ImageServer(){
        super(null, true);
    }
    public ImageServer(LayoutManager layout){
        super(layout, true);
    }
    private void getImage(){
        int returnVal = FChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION){
                iPanel.setImage(FChooser.getSelectedFile());
        } else {
            System.exit(0);
        }
    }
    private static void showGUI() {
        FMain.setDefaultCloseOperation(FMain.EXIT_ON_CLOSE);
        FMain.setBounds(0,0,600,600);
        FMain.getContentPane().add(new ImageServer(new BorderLayout()),BorderLayout.CENTER);
        FMain.pack();
        FMain.setVisible(true);
    }
    public static void infoBox(String infoMessage, String titleBar)
    {
       JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
    public void listenClient(){
        OpenPort8000 op8000 = new OpenPort8000();
        ClientListener cL = new ClientListener(op8000);
        cL.start();
    }

    public static void main(String[] args) throws Exception {
        int max_connection = Integer.parseInt(args[0]);
        ImageServer hehe = new ImageServer();
        hehe.showGUI();
        InetAddress IP=InetAddress.getLocalHost();
        hehe.infoBox(IP.getHostAddress(),"IP.getHostAddress()");
        hehe.listenClient();
    };


    public static class ImagePanel extends JPanel {
    	private BufferedImage imageBuffer;
    	ImagePanel(){
    	}
        public void setImage(File f){
            BufferedImage iBuffer_temp;
            try{
                    iBuffer_temp = ImageIO.read(f);
            }
            catch(Exception e){
                iBuffer_temp = null;
            }
            imageBuffer = iBuffer_temp;
        }
    	protected void paintComponent(Graphics g) {
    		super.paintComponent(g);
    		g.drawImage(imageBuffer, 0, 0, 600, 600, this);
    	}
    }

    public static class ClientListener extends Thread {
        private ServerSocket serverSocket = null;
        private Socket cLisSocket = null;
        private boolean socket_Accpeted = false;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private final int ServerPort = 8000;
        public ClientListener(Runnable r){
            super(r);
        }
        public ClientListener(){
            try{
                serverSocket = new ServerSocket(ServerPort);
            } catch (IOException e) {
                infoBox("Fail to initiate at port 8000","Fail!");
            }
            try{
                synchronized(serverSocket){
                cLisSocket = serverSocket.accept();
                this.run();
                }
            } catch (IOException e) {
                System.out.println("Accept failed: 8000");
            }
            if (cLisSocket.isConnected()) {socket_Accpeted = true;}
        }
        /**
         * The set of all names of clients in the chat room.  Maintained
         * so that we can check that new clients are not registering name
         * already in use.
         */
        private HashSet<String> names = new HashSet<String>();

        /**
         * The set of all the print writers for all the clients.  This
         * set is kept so we can easily broadcast messages.
         */
        private HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

        /**
         * Constructs a handler thread, squirreling away the socket.
         * All the interesting work is done in the run method.
         */
        // public ClientListener(Socket socket) {
        //     this.socket = socket;
        // }
    }

    public class OpenPort8000 implements Runnable{
        /**
         * Services this thread's client by repeatedly requesting a
         * screen name until a unique one has been submitted, then
         * acknowledges the name and registers the output stream for
         * the client in a global set, then repeatedly gets inputs and
         * broadcasts them.
         */
        public void run() {
        }
    }

}
