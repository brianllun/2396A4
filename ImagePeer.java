import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.imageio.*;

@SuppressWarnings("serial")
/**
 * This is the ImagePeer class
 * @author 3035290733
 *
 */
public class ImagePeer extends JPanel{
	protected GridLayout glayout = new GridLayout(20,20);
	protected ImagePanel iPanel;
	protected JFrame FMain = new JFrame("Image Client");
	protected InetAddress IPAdd;
	protected BufferedImage imageBuffer;
	protected JLabel[][] gridPanel= new JLabel[20][20];
	protected byte[] byteAr;
	protected HashMap<Integer, String> peer_list = new HashMap<Integer, String>();
	{
		iPanel = new ImagePanel(glayout);
        iPanel.setPreferredSize(new Dimension(600, 600));
        add(iPanel,BorderLayout.CENTER);
		for (int i=0; i < 20; i++) for (int j=0; j < 20; j++){
			gridPanel[i][j] = new JLabel();
			iPanel.add(gridPanel[i][j]);
		}
	}
	public static void main(String[] args) throws Exception {
		ImagePeer ipeer = new ImagePeer();
		ipeer.go();
	};
	public ImageIcon fetchImage(byte[] b, InputStream is){
		try{
			int nbrRd = 0;
			int nbrLeftToRead = b.length;
			while(nbrLeftToRead > 0){
				int readin =is.read(b, nbrRd, nbrLeftToRead);
			 	if(readin < 0) break;
			  	nbrRd += readin;
			  	nbrLeftToRead -= readin;
			}
			System.out.println("Got image. nbrToRead=" + b.length + ", nbrRd=" + nbrRd);
			return (new ImageIcon(b));
		}catch (Exception ex){
			ex.printStackTrace();
			return(null);
		}
	}
	private void setIcon(Socket sock){
		byte[] sizeAr = new byte[4];
		try{

			InputStream in = sock.getInputStream();
			for (int i=0; i <20; i++){
				for (int j=0; j<20; j++){
					if(in.read(sizeAr) < 0){sock.close(); break;};
					byteAr = new byte[ByteBuffer.wrap(sizeAr).asIntBuffer().get()];
					ImageIcon tempImage = fetchImage(byteAr, in);
					gridPanel[j][i].setIcon(tempImage);
				}
			}
		}catch (IOException e){
			e.printStackTrace();
			infoBox("Connection Closed!","");
			// System.exit(0);
		}
		iPanel.repaint();
	}
	public void go(){
		boolean connected = false;
		int tPort;
		showGUI();
		try{
			IPAdd = InetAddress.getByName(inputBox("Input your Server IP!",""));
		} catch (UnknownHostException ex){ex.printStackTrace();}
		try{
			Socket sock = new Socket(IPAdd.getHostAddress(), 8000);
			// ObjectOutputStream msgout = new ObjectOutputStream(sock.getOutputStream());
			// ObjectInputStream msgin = new ObjectInputStream(sock.getInputStream());
			String name = inputBox("Login Name:","Login");
			String pw = inputBox("Password:", "Login");
			// msgout.writeObject(loginMessage(name,pw));
			// msgout.close();
			// Message login_respond = (Message) msgin.readObject();
			// if (login_respond.command.equals("LOGIN_FAIL")){
				// infoBox("Login Failed!","");
			// } else{
				// System.exit(0);
			// }
			// msgout.close();
			// msgin.close();
			BufferedReader tin = new BufferedReader (new InputStreamReader(sock.getInputStream()));
			PrintWriter tout = new PrintWriter(sock.getOutputStream(), true);
			tPort = Integer.parseInt(tin.readLine());
			Socket tsock = new Socket(IPAdd.getHostAddress(),tPort);
			/*
			*	Got Communication Port
			*/
			new asPeer(tsock).start();
		} catch (Exception ex) {
			ex.printStackTrace();
			infoBox("Connection Closed!","Fail");
			// System.exit(0);
		}
	}
	public Message loginMessage(String login,String pw){
		Message m = new Message();
		m.sender = login;
		m.receiver = "Teacher";
		m.command = "LOGIN";
		m.data_iname = null;
		m.data_blockno = null;
		m.data_content = pw;
		m.peer_list = null;
		return m;
	}
	public static void infoBox(String infoMessage, String titleBar){
	   JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
	}
	public static String inputBox(String infoMessage, String titleBar){
		String s;
		s = JOptionPane.showInputDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
		return (s);
	}
	private void showGUI(){
		FMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		FMain.getContentPane().add(this,BorderLayout.CENTER);
		FMain.pack();
		FMain.setVisible(true);
	}
	public class ImagePanel extends JPanel {
		private BufferedImage imageBuffer;
		ImagePanel(GridLayout g){
			setLayout(g);
        }
		public void setImage(BufferedImage f){
			imageBuffer = f;
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
            g.drawImage(imageBuffer, 0, 0, 600, 600, null);
        }
    }
	public class asPeer extends Thread{
		private Socket sock;
		public asPeer(Socket s){
			this.sock = s;
		}
		public void run(){
			try{
				System.out.println("Running");
				PrintWriter cout = new PrintWriter(sock.getOutputStream(), true);
			  	BufferedReader cin = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				while (true){
					// System.out.println("While true");
					String readln = cin.readLine();
					// if (readln != null){
					// 	System.out.println(readln);
					// }
					if (readln.equals("UPDATE")){
						synchronized(sock){
							setIcon(sock);
						}
					}
					if (readln.equals("ACTIVECHECK")){
						synchronized(sock){
							cout.println("ACTIVE");
						}
					}
				}
			}catch (IOException e){
				infoBox("Connection Failed!", "");
				// System.exit(0);
			}
		}
	}
}
