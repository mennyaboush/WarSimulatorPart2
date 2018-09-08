package gson;

import War.BusinessLogic.WarController;
import War.BusinessLogic.WarControllerFacade;
import War.Entities.Missile;
import War.Entities.LauncherDestructor;
import War.Entities.MissileDestructor;
import War.Entities.Launcher;
import gson.Adapters.GsonAdapters;
import gson.entities.*;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import java.util.function.Function;

public class JsonHandler implements Runnable{
    private WarControllerFacade controller = WarController.getInstance();
    private GsonAdapters adapter = new GsonAdapters();
    private ScheduledExecutorService scheduledPool;
    private ExecutorService gsonServices = Executors.newCachedThreadPool();

    private HashMap<gson.entities.Missile, Missile> gsonMissilesToEntities = new HashMap<>();
    private HashMap<gson.entities.Launcher, Launcher> gsonLaunchersToEntities = new HashMap<>();
    private HashMap<Destructor_, LauncherDestructor> gsonLauncherDestructorsToEntities = new HashMap<>();
    private HashMap<Destructor, MissileDestructor> gsonMissileDestructorsToEntities = new HashMap<>();

    private gson.entities.War gWar;

    public JsonHandler(gson.entities.War gWar){
        final int NUM_OF_THREADS = 8;
        setgWar(gWar);
        scheduledPool = Executors.newScheduledThreadPool(NUM_OF_THREADS);
    }

    public void setgWar(gson.entities.War gWar) {
        this.gWar = gWar;
    }

    @Override
    public void run() {
        ExecutorService es = Executors.newFixedThreadPool(3);

        addLaunchers(gWar.getMissileLaunchers().getLauncher());

        addMissileDestructors(gWar.getMissileDestructors().getDestructor());

        addLauncherDestructors(gWar.getMissileLauncherDestructors().getDestructor());

        WarController.startTime();

         es.execute(() -> launchMissile(gWar.getMissileLaunchers().getLauncher()));

         es.execute(() ->  destructMissile(gWar.getMissileDestructors().getDestructor()));

         es.execute(() ->  destructLauncher(gWar.getMissileLauncherDestructors().getDestructor()));
    }

    private void addLaunchers(List<gson.entities.Launcher> jLaunchers){
        for (gson.entities.Launcher launcher : jLaunchers){
            Launcher entityLauncher = adapter.adaptLauncher(launcher);
            controller.addLauncher(entityLauncher);
            gsonLaunchersToEntities.put(launcher, entityLauncher);
            for(gson.entities.Missile missile : launcher.getMissile()){
                gsonMissilesToEntities.put(missile, adapter.adaptMissile(missile));
            }
        }
    }

    private void addMissileDestructors(List<gson.entities.Destructor> jMissileDestructors){
        for (gson.entities.Destructor missileDestructor : jMissileDestructors){
            MissileDestructor missileDestructorEntity = adapter.adaptMissileDestructor(missileDestructor);
            controller.addMissileDestructor(missileDestructorEntity);
            gsonMissileDestructorsToEntities.put(missileDestructor, missileDestructorEntity);
        }
    }

    private void addLauncherDestructors(List<gson.entities.Destructor_> jLauncherDestructors){
        for (gson.entities.Destructor_ launcherDestructor : jLauncherDestructors){
            LauncherDestructor launcherDestructorEntity = adapter.adaptLauncherDestructor(launcherDestructor);
            controller.addLauncherDestructor(launcherDestructorEntity);
            gsonLauncherDestructorsToEntities.put(launcherDestructor, launcherDestructorEntity);
        }
    }

    private void launchMissile(List<gson.entities.Launcher> jlaunchers){
        for(gson.entities.Launcher launcher : jlaunchers){
            for(gson.entities.Missile missile : launcher.getMissile()){
                long time = missile.getLaunchTime();
                Launcher launcherParam = gsonLaunchersToEntities.get(launcher);

                runWithDelay((e) -> {
                            controller.launchMissile(launcherParam, gsonMissilesToEntities.get(missile));
                            return null; },
                        time );
            }
        }
    }


    private void destructMissile(List<gson.entities.Destructor> jMissileDestructors) {
        for (gson.entities.Destructor missileDestructor : jMissileDestructors) {
            for (DestructedMissile gDestructedMissile : missileDestructor.getDestructedMissile()) {
                for (gson.entities.Missile missile : gsonMissilesToEntities.keySet()) {
                    if (gDestructedMissile.getId().equals(missile.getId())) {
                       runWithDelay(e -> {
                                    controller.destructMissile(
                                            adapter.adaptMissileDestructor(missileDestructor),
                                            gsonMissilesToEntities.get(missile),
                                            gDestructedMissile.getDestructAfterLaunch());
                                    return null;
                                    },
                                missile.getLaunchTime());

                    }
                }
            }
        }
    }

    private void destructLauncher(List<gson.entities.Destructor_> jlauncherDestructors) {
        for (gson.entities.Destructor_ launcherDestructor : jlauncherDestructors) {
            for (DestructedLauncher gsonDestructedLauncher : launcherDestructor.getDestructedLauncher()) {
                for (gson.entities.Launcher gsonLauncher : gsonLaunchersToEntities.keySet()) {
                    if (gsonDestructedLauncher.getId().equals(gsonLauncher.getId())) {
                            int time = gsonDestructedLauncher.getDestructTime();
                           runWithDelay((e) -> {
                                        try {
                                            controller.destructLauncher(
                                                    gsonLauncherDestructorsToEntities.get(launcherDestructor),
                                                    gsonLaunchersToEntities.get(gsonLauncher),
                                                    time);
                                        } catch (InterruptedException ex) {
                                            ex.printStackTrace();
                                        }
                                        return null;
                                    } ,
                                    0);
                            break;
                    }
                }
            }
        }
    }

    private void runWithDelay(Function<Void,Void> task, long delay){
        gsonServices.execute(() ->{
            try {
                Thread.sleep(delay * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            task.apply(null);
        });
    }
}
