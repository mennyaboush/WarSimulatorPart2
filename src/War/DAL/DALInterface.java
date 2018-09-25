package War.DAL;

import War.Entities.Launcher;
import War.Entities.LauncherDestructor;
import War.Entities.Missile;
import War.Entities.MissileDestructor;

public interface DALInterface {

	//static DALInterface getInstance();

	void saveLauncher(Launcher launcher);

	void saveMissileDestructor(MissileDestructor missileDestructor);

	void saveLauncherDestructor(LauncherDestructor launcherDestructor);

	void saveMissile(Missile missile , String launcherId, boolean hit) ;
	
	void saveDestructedLauncher(LauncherDestructor launcherDestructor, Launcher launcher, long destructTime);

	void saveDestructedMissile(MissileDestructor missileDestructor, Missile missile, long time);

//	void saveDestructLauncherResult(String missileLauncherID, String destructorType, long destructTime,
//
//			boolean success);
//
//	void saveDestructMissileResult(String missileDestructorID, String missileID, long destructAfterLaunch,
//
//			boolean success);
//
//	void saveMissileResult(String id, boolean isHit);


}
