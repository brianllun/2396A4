

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.IOException;
import java.net.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.*;
import java.util.*;
import java.util.HashSet;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.*;
public class ImageServer extends JPanel{

    protected ImagePanel iPanel;
    protected JFileChooser FChooser = new JFileChooser();
    protected JFrame FMain = new JFrame("Image Server");
    protected File IFile;
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
            IFile = FChooser.getSelectedFile();
            try{
                iPanel.setImage((BufferedImage) getScaledImage(ImageIO.read(IFile),600,600));
            } catch (IOException e){
                e.printStackTrace();
            }
        } else {
            System.exit(0);
        }
    }
    private void showGUI() {
        FMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        public void setImage(BufferedImage b){
            imageBuffer = b;
        }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(imageBuffer, 0, 0, 600, 600, this);
        }
    }
    private BufferedImage[][] getSplittedImage(BufferedImage b){
        BufferedImage[][] biArray = new BufferedImage[20][20];
        for (int i=0; i*30 < 600; i++){
            for (int j=0; j*30 < 600; j++){
                biArray[i][j] = b.getSubimage(i*30, j*30, 30, 30);
            }
        }
        return biArray;
    }
    private Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }
    public static void main(String[] args) throws Exception {
        ImageServer hehe = new ImageServer(new BorderLayout());
//        hehe.max_connection_set(Integer.parseInt(args[0]));
        hehe.go();
    };
    public void go(){
        showGUI();
        listenClient();
    }
    public void listenClient(){
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
                    ClientHandler cl = new ClientHandler(cHandleSocket);
                    peercount++;
                    cl.start();
            } catch (IOException e) {
                infoBox("Accept failed: 8000","Fail!");
            }
        }
    }
    public class ClientHandler extends Thread {

        private boolean socket_Accpeted = false;
        private Socket socket;
        private File jf = IFile;
        private String jfs = jf.getName();
        private OutputStream out;
        private ByteArrayOutputStream baout = new ByteArrayOutputStream();
        public ClientHandler(Socket s){
            this.socket = s;
        }
        public void sendImage() throws IOException{
            jf = IFile;
            jfs = jf.getName();
            BufferedImage bfImage = (BufferedImage) getScaledImage(ImageIO.read(jf),600,600);
            // System.out.println("Image to BABuffer: "+ImageIO.write(bfImage, jfs.substring(jfs.lastIndexOf(".")+1,jfs.length()), baout));
            // System.out.print("File Size: "+baout.size());
            // byte[] size = ByteBuffer.allocate(4).putInt(baout.size()).array();
            // byte[] realbyte = baout.toByteArray();
            // out.write(size);
            // out.flush();
            // out.write(realbyte);
            // out.flush();
            // baout.reset();

            BufferedImage[][] bfImageArray = getSplittedImage(bfImage);
            for (int i=0; i*30 < 600; i++){
                for (int j=0; j*30 < 600; j++){
                    System.out.println("Image to BABuffer: "+ImageIO.write(bfImageArray[i][j], jfs.substring(jfs.lastIndexOf(".")+1,jfs.length()), baout));
                    System.out.println("File Size: "+baout.size());
                    byte[] size = ByteBuffer.allocate(4).putInt(baout.size()).array();
                    byte[] realbyte = baout.toByteArray();
                    out.write(size);
                    out.flush();
                    out.write(realbyte);
                    out.flush();
                    baout.reset();
                }
            }
            // JDialog dialog = new JDialog();
            // dialog.setPreferredSize(new Dimension(600,600));
            // JLabel label = new JLabel(new ImageIcon(realbyte));
            // dialog.add( label );
            // dialog.pack();
            // dialog.setVisible(true);

        }
        public void run(){
            try{
                out = socket.getOutputStream();
                sendImage();
                while(true){
                    infoBox("(Server) Connected Client"+peercount,"");
                    if (!jf.equals(IFile)){
                        infoBox("Picture changed!","");
                        sendImage();
                    }
                    try{
                        this.sleep(5000);
                    } catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
