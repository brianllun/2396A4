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

public class ImageServer {
	static JFrame FMain;

    public static void main(String[] args) throws Exception {
        ImageServer iServer = new ImageServer();
    	iServer.FMain = new JFrame("Image Server");
    	iServer.FMain.setBounds(0,0,700,600);
    	JFileChooser FChooser = new JFileChooser();
    	int returnVal = FChooser.showOpenDialog(FMain);

			Container fCPane = FMain.getContentPane();
			JButton reloadButton = new JButton("Load another image");
			fCPane.add(reloadButton,BorderLayout.SOUTH);

    	if (returnVal == JFileChooser.APPROVE_OPTION) {
                ImagePanel iPanel = new ImagePanel(FChooser.getSelectedFile());
				fCPane.add(iPanel,BorderLayout.CENTER);
    		FMain.setVisible(true);
    		FMain.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    	} else {
    		System.exit(0);
    	}
    }

        public static class ImagePanel extends JPanel {
        	private BufferedImage imageBuffer;

        	ImagePanel(File f){
                BufferedImage iBuffer_temp;
                try
				{
                        iBuffer_temp = ImageIO.read(f);
                }
				catch(Exception e)
				{
                        javax.swing.JOptionPane.showMessageDialog(null, "Cannot load image "+ f.getName());
                        iBuffer_temp = null;
                }
                imageBuffer = iBuffer_temp;

        	}
        	protected void paintComponent(Graphics g) {
        		super.paintComponent(g);
        		g.drawImage(imageBuffer, 0, 0, this);
        	}
        }
}
