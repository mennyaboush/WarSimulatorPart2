package War.View.application;

import War.BusinessLogic.WarController;
import War.View.AbstractWarView;
import War.View.ConsoleView;
import War.clientServer.Client;
import War.clientServer.Server;
import gson.GsonReader;
import gson.JsonHandler;
import gson.JsonReaderFacade;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class War {

	public static void main(String[] args) {
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(() -> WarApplication.main(args));

        AbstractWarView abstractWarView = new ConsoleView(WarController.getInstance());

        abstractWarView.start();

        es.shutdown();

	}

	protected static void loadFromJson() {
		JsonReaderFacade jsonReader = new GsonReader();
		// jsonReader.readJson();

		JsonHandler jsonHandler = new JsonHandler(jsonReader.readJson());
		Executors.newSingleThreadExecutor().execute(jsonHandler);
	}

}
