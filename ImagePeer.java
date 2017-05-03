import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.nio.*;

import javax.swing.*;
//import javax.swing.event.*;
import javax.imageio.*;

public class ImagePeer extends JPanel{
    /**
	 *
	 */
	private static final long serialVersionUID = 16476560113239447L;
	protected GridLayout glayout = new GridLayout(20,20);
	protected ImagePanel iPanel;
	protected JFrame FMain = new JFrame("Image Client");
	protected InetAddress IPAdd;
	protected BufferedImage imageBuffer;
	protected JLabel[][] gridPanel= new JLabel[20][20];
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
		// int max_connection = Integer.parseInt(args[0]);

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
			  	System.out.println("Got part of image. read in=" + readin + ", byte left= " + nbrLeftToRead
								+ " space available=" + is.available());
			}
			System.out.println("Got image. nbrToRead=" + b.length + ", nbrRd=" + nbrRd);
			return (new ImageIcon(b));
			// return (new ImageIcon(new ByteArrayInputStream(b)));
		}catch (Exception ex){
			ex.printStackTrace();
			return(null);
		}
	}
	public void go(){
		showGUI();
		try{
			IPAdd = InetAddress.getByName(inputBox("Input your fucking Server IP!","IP.getHostAddress()"));
		} catch (UnknownHostException ex){ex.printStackTrace();}
		try{
			Socket sock = new Socket(IPAdd.getHostAddress(), 8000);
			InputStream in = sock.getInputStream();
			infoBox("(Client)Connected!","");
			byte[] sizeAr = new byte[4];
			while (true){
				try{
					for (int i=0; i <= 19; i++){
						for (int j=0; j<=19; j++){
							if(in.read(sizeAr) < 0){sock.close(); break;};
							byte[] byteAr = new byte[ByteBuffer.wrap(sizeAr).asIntBuffer().get()];
							ImageIcon tempImage = fetchImage(byteAr, in);
							gridPanel[j][i].setIcon(tempImage);
							// iPanel.revalidate();
						}
					}
					iPanel.repaint();

					// JDialog dialog = new JDialog();
					// dialog.setPreferredSize(new Dimension(600,600));
					// JLabel label = new JLabel(tempImage);
					// dialog.add( label );
					// dialog.pack();
					// dialog.setVisible(true);
				}catch (IOException e){
					infoBox("Connection Closed!","");
					break;
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			infoBox("(Client)Connection Closed","Fail");
		}
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
	public static class ImagePanel extends JPanel {
        /**
		 *
		 */
		private static final long serialVersionUID = 768818327301329884L;
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
}
