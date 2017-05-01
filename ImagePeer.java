import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ImagePeer {

		protected static JFrame FMain = new JFrame("Image Client");
		protected static InetAddress IPAdd;

	public static void main(String[] args) throws Exception {
		// int max_connection = Integer.parseInt(args[0]);
		// showGUI();
		InetAddress IP=InetAddress.getLocalHost();
		IPAdd = IPAdd.getByName(inputBox("Input your fucking Server IP!","IP.getHostAddress()"));
		try{
			Socket sock = new Socket(IPAdd.getHostAddress(), 8000);
		} catch (Exception ex) {
			ex.printStackTrace();
			infoBox("Fail","Fail");
		}
	};
	public static void infoBox(String infoMessage, String titleBar){
	   JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
	}
	public static String inputBox(String infoMessage, String titleBar){
		String s;
		s = JOptionPane.showInputDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
		return (s);
	}
	public void showGUI(){}
}
