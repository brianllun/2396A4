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
    ImageServer(LayoutManager layout){
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

    public static void main(String[] args) throws Exception {
        int max_connection = Integer.parseInt(args[0]);
        showGUI();
        listenSocket();
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
    private static class Peer{
        private String userN;
        private Date lastLogin = new Date();
        private int blkNum;
    }
    private static void listenSocket(){
        ServerSocket iServer = null;
        Socket clientSocket = null;
        boolean socket_accpeted = false;
        try{
            iServer = new ServerSocket(8000);
        } catch (IOException e) {
            System.out.println("Port listening fail!");
        }

        try{
            clientSocket = iServer.accept();
        } catch (IOException e) {
            System.out.println("Accept failed: 8000");
        }
        if (clientSocket.isConnected()) {socket_accpeted = true;}

        // try{
        //     in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        //     out = new PrintWriter(client.getOutputStream(), true);
        // } catch (IOException e) {
        //     System.out.println("Accept failed: 8000");
        //     System.exit(-1);
        // }
        // while(true){
        //     try{
        //         line = in.readLine();
        //         //Send data back to client
        //         out.println(line);
        //     } catch (IOException e) {
        //         System.out.println("Read failed");
        //         System.exit(-1);
        //     }
        // }
    }
}
