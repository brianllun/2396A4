import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class ImageServer {
	static JFrame FMain;
    public static void main(String[] args) throws Exception {
    	FMain = new JFrame("Image Server");
    	FMain.setBounds(0,0,700,600);
    	final JFileChooser FChooser = new JFileChooser();
    	int returnVal = FChooser.showOpenDialog(FMain);
    	
    	if (returnVal == JFileChooser.APPROVE_OPTION) {
    		File selectedFile = FChooser.getSelectedFile();
    		FMain.setVisible(true);
    		FMain.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    	} else {
    		System.exit(0); 
    	}
    }
}
