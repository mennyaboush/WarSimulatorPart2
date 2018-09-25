package War.BusinessLogic;

import War.BusinessLogic.controllers.LauncherController;
import War.BusinessLogic.controllers.LauncherDestructorController;
import War.BusinessLogic.controllers.MissileController;
import War.BusinessLogic.controllers.MissileDestructorController;
import War.DAL.ConfigFile;
import War.DAL.DALInterface;
import War.DAL.DALmongoDB;
import War.DAL.DALmySql;
import War.Entities.*;
import War.WarObserver.WarObservable;
import War.WarObserver.WarObserver;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.*;

import javax.swing.JOptionPane;

import static java.time.temporal.ChronoUnit.SECONDS;

public class WarController extends WarObservable implements WarControllerFacade { // Singleton
	private static WarController warController = new WarController();

	private static LocalTime startRunTime = LocalTime.now();
	private HashMap<Launcher, LauncherController> missileLaunchers;
	private HashMap<MissileDestructor, MissileDestructorController> missileDestructors;
	private HashMap<LauncherDestructor, LauncherDestructorController> launcherDestructors;
	private List<Missile> activeMissiles = new ArrayList<>();
	// @AutoWired
	private DALInterface dal = null;//DALmongoDB.getInstance();

	private Statistics statistics = new Statistics();

	private ExecutorService executorService = Executors.newCachedThreadPool();

	private WarController() {
	
		ConfigFile configFile =  new ConfigFile();
		missileLaunchers = new HashMap<>();
		missileDestructors = new HashMap<>();
		launcherDestructors = new HashMap<>();
//		int temp = Integer.parseInt(JOptionPane.showInputDialog("1 - for mongoDB\n"
//				+ "2 - for mysql."));
//		if(temp == 1)
//			dal = DALmongoDB.getInstance();
//		else
//			dal = DALmySql.getInstance();
		if(configFile.getValue("dal").compareTo("mysql") == 0)
			dal = DALmySql.getInstance();
		else
			dal = DALmongoDB.getInstance();
	}

	public static void startTime() {
		startRunTime = LocalTime.now();
	}

	public static WarControllerFacade getInstance() {
	return warController;
	}

	@Override
	public void addLauncher(Launcher launcher) {
		LauncherController launcherController = new LauncherController(launcher);
		executorService.submit(launcherController);

		missileLaunchers.put(launcher, launcherController);

		publish(subscriber -> subscriber.launcherWasAdded(launcher));

		dal.saveLauncher(launcher);

	}

	@Override
	public void addLauncherDestructor(LauncherDestructor launcherDestructor) {
		launcherDestructors.put(launcherDestructor, new LauncherDestructorController(launcherDestructor));
		publish(subscriber -> subscriber.launcherDestructorWasAdded(launcherDestructor));
		dal.saveLauncherDestructor(launcherDestructor);
		// dal.saveDestructedLauncher(launcherDestructor.getId(),launcherDestructor.getType(),
		// launcherDestructor.);// ?
	}

	@Override
	public void addMissileDestructor(MissileDestructor missileDestructor) {
		missileDestructors.put(missileDestructor, new MissileDestructorController(missileDestructor));
		publish(subscriber -> subscriber.missileDestructorWasAdded(missileDestructor));
		dal.saveMissileDestructor(missileDestructor);
	}

	@Override
	public boolean launchMissile(Launcher launcher, Destination destination, double potentialDamage) {
		final int MAX_TIME = 11;
		int maxFlightTime = new Random().nextInt(MAX_TIME) + 4;
		return launchMissile(launcher, destination, potentialDamage, maxFlightTime);
	}

	@Override
	public boolean launchMissile(Launcher launcher, Destination destination, double potentialDamage,
			long maxFlightTime) {
		Missile missile = new Missile(potentialDamage, destination, maxFlightTime, getTime());
		activeMissiles.add(missile);
		return launchMissile(launcher, missile);
	}

	@Override
	public boolean launchMissile(Launcher launcher, Missile missile) {
		boolean hit = false;
		LauncherController launcherController = missileLaunchers.getOrDefault(launcher, null);

		if (launcherController == null) {
			activeMissiles.remove(missile);
			return false;
		}

		publish(subscriber -> subscriber.missileLaunched(launcher, missile, missile.getDestination()));

		statistics.increaseNumOfLaunchedMissiles();

		try {
			hit = executorService.submit(() -> launching(launcherController, missile, missile.getFlightTime())).get();

			if (hit) {
				publish(subscriber -> subscriber.missileHit(launcher, missile, missile.getDestination()));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		dal.saveMissile(missile, launcher.getId(),hit);
		activeMissiles.remove(missile);
		return hit;
	}

	private boolean launching(LauncherController launcherController, Missile missile, long maxFlightTime) {
		try {
			boolean hit = false;
			if (!launcherController.isDestructed())
				hit = launcherController.launch(missile, maxFlightTime);

			if (hit) {
				statistics.increaseNumOfHits();
				statistics.raiseDamage(missile.getDamage());
			}
			return hit;
		} catch (InterruptedException | ExecutionException e1) {
			e1.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean destructMissile(MissileDestructor missileDestructor, Missile missile) {
		final int MAX = 5, OFFSET = 1;
		return destructMissile(missileDestructor, missile, new Random().nextInt(MAX) + OFFSET);
	}

	@Override
	public boolean destructMissile(MissileDestructor missileDestructor, Missile missile, long time) {
		boolean destruct = false;
		long timeFromLaunch;
		MissileDestructorController missileDestructorController = missileDestructors.get(missileDestructor);
		for (LauncherController launcherController : missileLaunchers.values()) {
			if (launcherController.isCurrentlyLaunching()) {
				MissileController missileController = launcherController.getActiveMissileController();

				if (missileController.getMissile().equals(missile)) {
					publish(subscriber -> subscriber.missileDestructed(missileDestructor, missile));

					sleepSpecificTime(time);

					timeFromLaunch = getTime() - missileController.getMissile().getLaunchTime();

					Future<Boolean> destructionFuture = executorService.submit(
							() -> destructingMissile(missileDestructorController, missileController, timeFromLaunch));
					try {
						destruct = destructionFuture.get();
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
					dal.saveDestructedMissile(missileDestructor, missile, timeFromLaunch);
					return destruct;
				}
			}
		}

		return false;
	}

	private boolean destructingMissile(MissileDestructorController missileDestructorController,
			MissileController missileController, long timeFromLaunch) {
		boolean destruct = missileDestructorController.destruct(missileController, timeFromLaunch); // assumption:
		if (destruct) {
			statistics.increaseNumOfDestructedMissiles();
		}
		dal.saveDestructedMissile((MissileDestructor)missileDestructorController.getDestructor(), missileController.getMissile(), timeFromLaunch);
		return destruct;
	}

	@Override
	public boolean destructLauncher(LauncherDestructor launcherDestructor, Launcher launcher)
			throws InterruptedException {
		final int MAX_TIME = 5, TIME_OFFSET = 1;
		int destructionTime = new Random().nextInt(MAX_TIME) + TIME_OFFSET;
		return destructLauncher(launcherDestructor, launcher, destructionTime);
	}

	@Override
	public boolean destructLauncher(LauncherDestructor launcherDestructor, Launcher launcher, int destructionTime)
			throws InterruptedException {
		boolean succeed;

		LauncherController launcherController = missileLaunchers.getOrDefault(launcher, null);

		if (launcherController == null)
			throw new IllegalArgumentException("launcher doesn't not exist or already destructed");
		LauncherDestructorController launcherDestructorController = launcherDestructors.get(launcherDestructor);

		publish(subscriber -> subscriber.tryToDestructLauncher(launcherDestructor, launcher, destructionTime));
		sleepSpecificTime(destructionTime);

		succeed = launcherDestructorController.destruct(launcherController, getTime());

		if (succeed) {
			publish(subscriber -> subscriber.launcherDestructed(launcherDestructor, launcher));
			missileLaunchers.remove(launcher);
			statistics.increaseNumOfDestructedLaunchers();
		} else {
			publish(subscriber -> subscriber.launcherWasHidden(launcherDestructor, launcher));
		}
		dal.saveDestructedLauncher(launcherDestructor, launcher, destructionTime);
		return succeed;
	}

	@Override
	public Statistics getStatistics() {
		return statistics;
	}

	@Override
	public void exit() {
		executorService.shutdown();
		publish(WarObserver::endOfWar);
		System.exit(0);
	}

	@Override
	public Set<Launcher> retrieveLaunchers() {
		return missileLaunchers.keySet();
	}

	@Override
	public Set<MissileDestructor> retrieveMissileDestructors() {
		return missileDestructors.keySet();
	}

	@Override
	public Set<LauncherDestructor> retrieveLauncherDestructors() {
		return launcherDestructors.keySet();
	}

	@Override
	public Set<Missile> retrieveActiveMissiles() {
		Set<Missile> activeMissiles = new HashSet<>();
		for (LauncherController launcherController : missileLaunchers.values()) {
			if (launcherController.isCurrentlyLaunching()) {
				activeMissiles.add(launcherController.getActiveMissileController().getMissile());
			}
		}

		return activeMissiles;
	}

	public long getTime() {
		return SECONDS.between(startRunTime, LocalTime.now());
	}

	private void sleepSpecificTime(long seconds) {
		try {
			Thread.sleep(1000 * seconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {

		}
	}

	public Launcher getRandomLauncher() {
		int size = missileLaunchers.size();
		int keyIndex = (int) Math.random() * size;
		Object[] launchers = missileLaunchers.keySet().toArray();
		return (Launcher) launchers[keyIndex];
	}

	public LauncherDestructor getRandomDestructor() {
		int size = launcherDestructors.size();
		int keyIndex = (int) Math.random() * size;
		Object[] destructors = launcherDestructors.keySet().toArray();
		return (LauncherDestructor) destructors[keyIndex];
	}

	/*remove null if there no are active missiles*/
	public Missile getRandommissile() {
		return activeMissiles.get(0);
	}

	public MissileDestructor getRandomMissileDestructor() {
		int size = missileDestructors.size();
		int keyIndex = (int) Math.random() * size;
		Object[] destructors = missileDestructors.keySet().toArray();
		return (MissileDestructor) destructors[keyIndex];	}
}
