

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
import javax.swing.table.*;

@SuppressWarnings("serial")
/**
 * This is the ImageServer class
 * @author 3035290733
 *
 */
public class ImageServer extends JPanel{

    protected ImagePanel iPanel;
    protected JFileChooser FChooser = new JFileChooser();
    protected JFrame FMain = new JFrame("Image Server");
    protected File IFile;
    private int max_connection;
    private static int peercount = 0;
    private HashMap<String, ClientHandler> activePeerList = new HashMap<String, ClientHandler>();
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
    /**
     * Constructor
     *
     */
    public ImageServer(){
        super(null, true);
    }
    /**
     * Constructor
     * @param layout layoutmanger
     */
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
    private static void infoBox(String infoMessage, String titleBar)
    {
       JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
    private void max_connection_set (int i){
        max_connection = i;
    }
    private static class ImagePanel extends JPanel {
        private BufferedImage imageBuffer;
        ImagePanel(){
        }
        /**
         * This is a setter
         *
         */
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
       hehe.max_connection_set(Integer.parseInt(args[0]));
        hehe.go();
    };
    /**
     * This method runs the program
     *
     */
    public void go(){
        showGUI();
        new CheckActive().start();
        listenClient();
    }
    private Message loginMessage(String receiver, boolean loginok){
        Message m = new Message();
        m.sender = "Teacher";
        m.receiver = receiver;
        if(loginok){
            m.command = "LOGIN_OK";
        } else{
            m.command = "LOGIN_FAILED";
        }
        m.data_iname = IFile.getName();
        m.data_blockno = Integer.toString(400);
        m.data_content = null;
        if (activePeerList.size() < max_connection){
            for (Map.Entry<String, ClientHandler> ap : activePeerList.entrySet()){
                m.peer_list.put(ap.getKey(),ap.getValue().getSocket().getInetAddress().getHostAddress());
                String connect = null;
            }
        } else{
            for (int i=0; i < max_connection; i++){
                Random random = new Random();
                java.util.List<String> keys = new ArrayList<String>(activePeerList.keySet());
                String randomKey = keys.get( random.nextInt(keys.size()) );
                if (m.peer_list.get(randomKey) == null){
                    m.peer_list.put(randomKey,
                    activePeerList.get(randomKey).getSocket().getInetAddress().getHostAddress());
                }
            }
        }
        return m;
    }
    private void listenClient(){
        HashMap<String,Socket> ClientList = new HashMap<String,Socket>();
        ServerSocket serverSocket = null;
        Socket cSocket = null;
        final int ServerPort = 8000;
        boolean connected = false;
        int tPort = 8001;
        BufferedReader cin;
        PrintWriter cout, tout;
        try{
            serverSocket = new ServerSocket(ServerPort);
        } catch (IOException e) {
            infoBox("Fail to initiate at port 8000","Fail!");
        }
        while(true){
            try{
                cSocket = serverSocket.accept();
                while(!connected){
                    try{
                        // ObjectOutputStream msgout = new ObjectOutputStream(cSocket.getOutputStream());
            			// ObjectInputStream msgin = new ObjectInputStream(cSocket.getInputStream());
                        synchronized(cSocket){
                            // Message loginmsg = (Message) msgin.readObject();
                            // if (activePeerList.get(loginmsg.sender) != null){
                                // msgout.writeObject(loginMessage(loginmsg.sender, true));
                                // msgout.close();
                                // msgin.close();
                                ServerSocket t = new ServerSocket(tPort);
                                // msgout.close();
                                tout = new PrintWriter(cSocket.getOutputStream(), true);
                                tout.println(tPort);
                                activePeerList.put(Integer.toString(peercount),new ClientHandler(t.accept(),tPort));
                                activePeerList.get(Integer.toString(peercount)).start();
                                peercount++;
                                connected = true;
                            // } else{
                            //     msgout.writeObject(loginMessage(loginmsg.sender, false));
                            //     // msgout.close();
                            //     // msgin.close();
                            //     cSocket.close();
                            //     break;
                            // }
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        tPort++;
                    }
                }
                tPort++;
                connected = false;
            } catch (IOException e) {
                infoBox("Accept failed: 8000","Fail!");
            }
        }
    }
    private class CheckActive extends Thread{
        public void run(){
            String column[]={"Nubmer","IP","Port","Active?"};
            JDialog dialog = new JDialog();
            String server[] = {"-1","localhost","8000","Ofcourse"};
            JTable table = new JTable(new String[0][0], column);
            dialog.setTitle("Active Peer");
            dialog.add( table );
            dialog.pack();
            dialog.setVisible(true);
            while (true){
                DefaultTableModel tableModel = new DefaultTableModel(column, 0);
                tableModel.addRow(server);
                for (Map.Entry<String, ClientHandler> ap : activePeerList.entrySet()){
                    boolean active = ap.getValue().activeCheck();
                    String connect = null;
                    if (active){
                        if (active){
                            connect = "Connected";
                        }else{
                            connect = "Disconnected";
                        }
                        String[] temp = {ap.getKey(),
                            ap.getValue().getSocket().getInetAddress().getHostAddress(),
                            Integer.toString(ap.getValue().getPort()),
                            connect
                        };
                        tableModel.addRow(temp);
                    }
                }
                table.setModel(tableModel);
                dialog.pack();
                try{
                    sleep(5000);
                } catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
    private class ClientHandler extends Thread {
        private BufferedReader sin;
        private PrintWriter sout;
        private boolean socket_Accpeted = false;
        private Socket socket;
        private File jf = IFile;
        private String jfs = jf.getName();
        private OutputStream out;
        private int tPort;
        private ByteArrayOutputStream baout = new ByteArrayOutputStream();
        public ClientHandler(Socket s,int t){
            this.socket = s;
            this.tPort = t;
            try{
                sout = new PrintWriter(s.getOutputStream(), true);
                sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        /**
         * This is a getter
         *
         */
        public int getPort(){
            return tPort;
        }
        /**
         * This is a getter
         *
         */
        public Socket getSocket(){
            return socket;
        }
        /**
         * This is the runnable of the class
         *
         */
        public void run(){
            try{
                out = socket.getOutputStream();
                synchronized(socket){
                    sout.println("UPDATE");
                    sendImage();
                    System.out.println("Sent");
                }
                while(true){
                    if (!jf.equals(IFile)){
                        synchronized(socket){
                            sout.println("UPDATE");
                            infoBox("Picture changed!","");
                            sendImage();
                        }
                    }
                    try{
                        sleep(2000);
                    } catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        private boolean activeCheck(){
            synchronized(socket){
                sout.println("ACTIVECHECK");
                try{
                    if (sin.readLine().equals("ACTIVE")){
                        return(true);
                    }
                } catch(IOException e){
                    e.printStackTrace();
                    return(false);
                }
            }
            return(false);
        }
        private void sendImage() throws IOException{
            jf = IFile;
            jfs = jf.getName();
            BufferedImage bfImage = (BufferedImage) getScaledImage(ImageIO.read(jf),600,600);
            BufferedImage[][] bfImageArray = getSplittedImage(bfImage);
            for (int i=0; i*30 < 600; i++){
                for (int j=0; j*30 < 600; j++){
                    ImageIO.write(bfImageArray[i][j], jfs.substring(jfs.lastIndexOf(".")+1,jfs.length()), baout);
                    // System.out.println("Image to BABuffer: ");
                    // System.out.println("File Size: "+baout.size());
                    byte[] size = ByteBuffer.allocate(4).putInt(baout.size()).array();
                    byte[] realbyte = baout.toByteArray();
                    out.write(size);
                    out.flush();
                    out.write(realbyte);
                    out.flush();
                    baout.reset();
                }
            }
            // System.out.println("Sent.");
            // JDialog dialog = new JDialog();
            // dialog.setPreferredSize(new Dimension(600,600));
            // JLabel label = new JLabel(new ImageIcon(realbyte));
            // dialog.add( label );
            // dialog.pack();
            // dialog.setVisible(true);
        }
    }
}
