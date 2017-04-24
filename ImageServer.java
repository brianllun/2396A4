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
    protected ImagePanel iPanel;
    {
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
    public ImageServer(LayoutManager layout){
        this(layout, true);
    }
    public ImageServer(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }
    public ImageServer(boolean isDoubleBuffered) {
        super(null, isDoubleBuffered);
    }
    public ImageServer() {
        this(true);
}
    private void getImage(){
        int returnVal = FChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION){
                iPanel = new ImagePanel(FChooser.getSelectedFile());
                iPanel.setPreferredSize(new Dimension(600, 600));
                add(iPanel,BorderLayout.CENTER);
        } else {
            System.exit(0);
        }
    }
    private static void showGUI() {
        JFrame FMain = new JFrame("Image Server");
        FMain.setDefaultCloseOperation(FMain.EXIT_ON_CLOSE);
        FMain.setBounds(0,0,600,600);
        FMain.getContentPane().add(new ImageServer(new BorderLayout()),BorderLayout.CENTER);
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
    		g.drawImage(imageBuffer, 0, 0, 600, 600, this);
    	}
    }
}
