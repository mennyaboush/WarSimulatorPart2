//package War.clientServer;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.Socket;
//import java.util.Date;
//import java.util.Random;
//
//import javax.swing.JOptionPane;
//
//public class Client {
//
//	public static void main(String[] args) {
//		Socket socket = null;
//		ObjectInputStream fromNetInputStream;
//		ObjectOutputStream toNetOutputStream;
//
//		try {
//			socket = new Socket("localhost", Server.PORT);
//
//			// NOTE: have to set the output stream before the input stream!
//			toNetOutputStream = new ObjectOutputStream(socket.getOutputStream());
//			fromNetInputStream = new ObjectInputStream(socket.getInputStream());
//
//			Random rand = new Random();
//			boolean fContinue = true;
//
//			// massage for check the connection.
//			String text = (String) fromNetInputStream.readObject();
//			System.out.println("Recieved from server: " + text);
//
//			do {
//				// print menu and take choice from client.
//				String menu = "1) Add Launcher\r\n" + "2) Add Launcher Destructor\r\n" + "3) Add Missile Destructor\r\n"
//						+ "4) Launch Missile\r\n" + "5) Destruct Missile\r\n" + "6) Destruct Launcher\r\n"
//						+ "7) Show Statistics\r\n" + "8) Exit";
//				String temp = JOptionPane.showInputDialog(menu);
//				int action = Integer.parseInt(temp);
//			
//				if (action > 0 && action < 9)
//					toNetOutputStream.writeInt(action); // send the number to the Server.
//
//				if (action == 8)
//					fContinue = false;
//			} while (fContinue);
//
//		} catch (Exception e) {
//			System.out.println("*** " + e.getMessage());
//		} finally {
//			try {
//				socket.close();
//				System.out.println("Client said goodbye..");
//			} catch (IOException e) {
//			}
//		}
//	}
//
//}
package War.clientServer;

import java.io.IOException;

import java.io.ObjectInputStream;

import java.io.ObjectOutputStream;

import java.net.Socket;

import java.net.UnknownHostException;

import java.util.Date;

import java.util.Scanner;

import javax.swing.JOptionPane;

public class Client {
	private static Scanner s = new Scanner(System.in);

	public static void main(String[] args) {

		Socket socket = null;
		ObjectInputStream objectInputStream;
		ObjectOutputStream objectOutputStream;
		try {
			socket = new Socket("localhost", Server.PORT);
			System.out.println(
					new Date() + " Conected to server at " + socket.getLocalAddress() + ":" + socket.getLocalPort());
			objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			objectInputStream = new ObjectInputStream(socket.getInputStream());
			boolean fContinue = true;
			String text = objectInputStream.readObject().toString();
			System.out.println("Resive :" + text + " frome server.");
			do {
				String menu = "\n1 - add launcher." + "\n2 - fire from launcher." + "\n3 - fire from missileDestructor."
						+ "\n4 - EXIT.";
				String temp = JOptionPane.showInputDialog(menu);
				int command = Integer.parseInt(temp);
				if (command > 0 && command < 5)
					objectOutputStream.writeObject(command);
				if (command == 4) {
					fContinue = false;
					System.out.println("client EXIT from server.");
				}
			} while (fContinue);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			s.close();
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("client Exit");
		}
	}

}
