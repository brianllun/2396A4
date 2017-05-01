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
    private int max_connection;
    private static int peercount = 0;
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
    private void showGUI() {
        FMain.setDefaultCloseOperation(FMain.EXIT_ON_CLOSE);
        FMain.setBounds(0,0,600,600);
        FMain.getContentPane().add(this,BorderLayout.CENTER);
        FMain.pack();
        FMain.setVisible(true);
    }
    public static void infoBox(String infoMessage, String titleBar)
    {
       JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
    public void max_connection_set (int i){
        max_connection = i;
    }
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
    public static void main(String[] args) throws Exception {
        ImageServer hehe = new ImageServer(new BorderLayout());
        hehe.max_connection_set(Integer.parseInt(args[0]));
        hehe.go();
    };
    public void go(){
        showGUI();
        listenClient();
    }
    public static void listenClient(){
        HashMap<String,Socket> ClientList = new HashMap<String,Socket>();
        ServerSocket serverSocket = null;
        Socket cHandleSocket = null;
        final int ServerPort = 8000;

        try{
            serverSocket = new ServerSocket(ServerPort);
        } catch (IOException e) {
            infoBox("Fail to initiate at port 8000","Fail!");
        }
        while(true){
            try{
                    cHandleSocket = serverSocket.accept();
                    infoBox("on9","on9");
                    ClientHandler cl = new ClientHandler(cHandleSocket);
                    peercount++;
                    cl.start();
            } catch (IOException e) {
                infoBox("Accept failed: 8000","Fail!");
            }
        }
    }
    public static class ClientHandler extends Thread{

        private boolean socket_Accpeted = false;
        private Socket socket;
        private BufferedReader in;
        private OutputStream out;
        public ClientHandler(Socket s){
            this.socket = s;
        }
        public void run(){
            OutputStream out = socket.getOutputStream();
            JFileChooser jf = FChooser;
            ByteArrayOutputStream baout = new ByteArrayOutputStream();
            while(true){
                infoBox("Client"+peercount,"");
                if (!jf.equal(FChooser)){
                    infoBox("Changed!","");
                    BufferedImage bfImage = ImageIO.read(jf.getSelectedFile());
                    ImageIO.write(bfImage, jf.substring(jf.lastIndexOf("."),jf.length()), baout);
                    byte[] size = ByteBuffer.allocate(4).putInt(baout.size()).array();
                    out.write(size);
                    out.write(baout.toByteArray());
                    out.flush();
                }
                try{
                    this.sleep(5000);
                } catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
    public class OpenPort8000 implements Runnable{
        public void run() {
            infoBox("dllm","dllm");
        }
    }
}
