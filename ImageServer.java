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

public class ImageServer extends JPanel{
    protected JFileChooser FChooser = new JFileChooser();
    public ImageServer(){
        int returnVal = FChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION){
                ImagePanel iPanel = new ImagePanel(FChooser.getSelectedFile());
                add(iPanel,BorderLayout.CENTER);
        } else {
            System.exit(0);
        }
        JButton reloadButton = new JButton("Load another image");
        add(reloadButton,BorderLayout.SOUTH);
    }

    private static void showGUI() {

        JFrame FMain = new JFrame("Image Server");
        FMain.setDefaultCloseOperation(FMain.EXIT_ON_CLOSE);

        ImageServer newContentPane = new ImageServer();

        newContentPane.setOpaque(true);
        FMain.setBounds(0,0,600,600);
        FMain.getContentPane().add(newContentPane,BorderLayout.CENTER);
        FMain.pack();
        FMain.setVisible(true);
    }


    public static void main(String[] args) throws Exception {
        showGUI();
    }

    public static class ImagePanel extends JPanel {
    	private BufferedImage imageBuffer;
    	ImagePanel(File f){
            BufferedImage iBuffer_temp;
            try{
                    iBuffer_temp = ImageIO.read(f);
            }
			catch(Exception e){
                    javax.swing.JOptionPane.showMessageDialog(null, "Cannot load image "+ f.getName());
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
