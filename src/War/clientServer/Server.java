
package War.clientServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import com.sun.media.jfxmediaimpl.platform.Platform;

import War.BusinessLogic.WarController;
import War.Entities.Destination;
import War.Entities.Launcher;
import War.Entities.LauncherDestructor;
import War.Entities.Missile;
import War.Entities.MissileDestructor;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.*;
public class Server extends Application {
	static final int PORT = 7030;
	private static Scene mainScene;
	private static final WarController controller = (WarController) WarController.getInstance();

	public static void main(String[] args) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				Socket socket = null;
				ServerSocket server = null;
				try {
					server = new ServerSocket(PORT);
					ObjectInputStream inputStream;
					ObjectOutputStream outputStream;

					System.out.println(new Date() + " --> Server waits for client...");
					socket = server.accept(); // blocking
					System.out.println(new Date() + " --> Client connected from " + socket.getInetAddress() + ":"
							+ socket.getPort());

					outputStream = new ObjectOutputStream(socket.getOutputStream());
					inputStream = new ObjectInputStream(socket.getInputStream());

					outputStream.writeObject("Welcome to server!");
					System.out.println("*** Sent welcome message to client");
					int press;
					do {
						press = (int) inputStream.readObject();
						if (press > 0 && press < 4)
							serverAction(press);
					} while (press != 4);
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					try {
						socket.close();
						server.close();
						System.out.println("Sever is closing after client is disconnected");
					} catch (IOException e) {
					}
				}

			}
		}).start();
		launch(args);
	}

	private static void serverAction(int press) {
		switch (press) {
		case 1:
			// add launcher.
			Launcher launcher = new Launcher(); // need to fix the ID
			controller.addLauncher(launcher);
			System.out.println("************ " + press);

			break;
		case 2:
			// fire from launcher.
			/*
			 * get random launcher Create or get destination random damage use launch
			 * missile of the controller
			 */
			
//			Launcher FireLauncher = controller.getRandomLauncher();
//			Destination destination = new Destination("target #0");
//			double damage = 1000 * Math.random(); // 0-1000
//			controller.launchMissile(FireLauncher, destination, damage);
			
			/*work but the missiles stay alive after they hit the target*/
			Launcher FireLauncher = controller.getRandomLauncher();
			Destination destination = new Destination("target #0");
			double damage = 1000 * Math.random(); // 0-1000
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					controller.launchMissile(FireLauncher, destination, damage);
				}
			}).start();
			
			/**
			 * run later work sometimes
			 * */
			
//			javafx.application.Platform.runLater(new Runnable() {
//				Launcher FireLauncher = controller.getRandomLauncher();
//				Destination destination = new Destination("target #0");
//				double damage = 1000 * Math.random(); // 0-1000
//					
//				@Override
//				public void run() {
//					controller.launchMissile(FireLauncher, destination, damage);		
//				}
//			});
//			System.out.println(press);
			break;

		case 3:
			// fire from missile destructor.
			/*
			 * ` get random missile destructor get random missile and distract him
			 */

			// controller.addMissileDestructor(new MissileDestructor());
			Missile missileToDestruct = controller.getRandommissile();
			MissileDestructor missileDestructor = controller.getRandomMissileDestructor();
			if (missileToDestruct != null)
				controller.destructMissile(missileDestructor, missileToDestruct);
			break;
		case 4:
			// exit
			controller.exit();
			break;

		default:
			System.out.println("default in server.");
			break;
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/War/View/WarMainWindow.fxml"));
		mainScene = new Scene(loader.load());
		primaryStage.setScene(mainScene);
		primaryStage.setFullScreen(false);
		// primaryStage.setResizable(false);
		primaryStage.setOnCloseRequest(v -> {
			primaryStage.close();
			FXMLLoader statisticsLoader = new FXMLLoader(getClass().getResource("/War/View/WarStatisticsWindow.fxml"));
			try {
				Scene statScene = new Scene(statisticsLoader.load());
				Stage statStage = new Stage();
				statStage.setScene(statScene);
				statStage.setResizable(false);
				statStage.showAndWait();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		primaryStage.show();
		WarController.startTime();
	}

}