package War.clientServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;


import War.BusinessLogic.WarController;
import War.Entities.Destination;
import War.Entities.Destructor;
import War.Entities.Launcher;
import War.Entities.LauncherDestructor;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Server {
	static final int PORT = 7030;
	private static final WarController controller = (WarController) WarController.getInstance();;
	
	public static void main(String[] args) {
		
				Socket socket = null;
				ServerSocket server = null ;
				
					
				try {
					server = new ServerSocket(PORT);
					ObjectInputStream inputStream;
					ObjectOutputStream outputStream;
					
					System.out.println(new Date() + " --> Server waits for client...");
					socket = server.accept(); // blocking
					System.out.println(
							new Date() + " --> Client connected from " + socket.getInetAddress() + ":" + socket.getPort());

					outputStream = new ObjectOutputStream(socket.getOutputStream());
					inputStream = new ObjectInputStream(socket.getInputStream());

					outputStream.writeObject("Welcome to server!");
					System.out.println("*** Sent welcome message to client");
					int press;
					do {
						press = (int)inputStream.readObject();
						serverAction(press );
					} while (press != 4);
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					try {
						socket.close();
						server.close();
						System.out
								.println("Sever is closing after client is disconnected");
					} catch (IOException e) { }
				}
	}

	private static void serverAction(int press ) {
		switch(press) {
		case 1:
			// add launcher.
			Launcher launcher = new Launcher("L1"); // need to fix the ID
			controller.addLauncher(launcher);
			System.out.println("************ " +press);
			
			break;
		case 2:
			//fire from launcher.
			/*
			 * get random launcher 
			 * Create or get destination
			 * random damage 
			 * use launch missile of the controller
			 * */
			Launcher FireLauncher = controller.getRandomLauncher();
			Destination destination =  new Destination("target #0");
			double damage = 1000 * Math.random(); // 0-1000
			controller.launchMissile(FireLauncher, destination, damage);
			System.out.println(press);
			break;
			
		case 3:
			//fire from missile destructor.
			/*`
			 * get random missile destructor 
			 * get random launcher 
			 * Distract launcher*/
			Launcher lancherToDestruct = controller.getRandomLauncher();
			LauncherDestructor destructor = controller.getRandomDestructor();
			try {
				controller.destructLauncher(destructor, lancherToDestruct);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case 4:
			//exit
			controller.exit();
			break;
			
			default:
				System.out.println("default in server.");
				break;
		}
	}

	
}
