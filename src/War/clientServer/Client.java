package War.clientServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Scanner;

import javax.swing.JOptionPane;

import War.BusinessLogic.WarController;
import War.Entities.Launcher;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application {

	private static Scanner s = new Scanner(System.in);
	private static Scene mainScene;

	public static void main(String[] args) {
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				Socket socket = null;
				ObjectInputStream objectInputStream;
				ObjectOutputStream objectOutputStream;
				try {
					socket = new Socket("localhost", Server.PORT);
					System.out.println(new Date() + " Conected to server at " + socket.getLocalAddress() + ":"
							+ socket.getLocalPort());
					objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
					objectInputStream = new ObjectInputStream(socket.getInputStream());
					boolean fContinue = true;
					String text = objectInputStream.readObject().toString();
					System.out.println("Resive :" + text + " frome server.");
					do {
						String menu = "\n1 - add launcher." + "\n2 - fire from launcher."
								+ "\n3 - fire from missileDestructor." + "\n4 - EXIT.";
						String temp = JOptionPane.showInputDialog(menu);

						int command = Integer.parseInt(temp);// getComandFromeUser();//1- addLauncher , 2 -fire from
																// launcher , 3 - destruct missile.
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
		}).start();
		launch(args);

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
