package War.View;

import War.BusinessLogic.WarControllerFacade;

import java.util.concurrent.ExecutionException;

public interface ViewFacade {

    void addLauncher();

    void addLauncherDestructor();

    void addMissileDestructor();

    void launchMissile() throws ExecutionException, InterruptedException;

    void destructMissile();

    void destructLauncher();

    void showStatistics();

    void exit();

}
