package War.DAL;

import War.Entities.*;

public class WarDaoImpl implements WarDao {
    private WeaponDao<Launcher> launcherDao;
    private WeaponDao<LauncherDestructor> LauncherDestructorDao;
    private WeaponDao<MissileDestructor> MissileDestructorDao;
    private WeaponDao<Missile> missileDao;

    @Override
    public void saveLauncher(Launcher launcher) {
        launcherDao.save(launcher);
    }

    @Override
    public void saveLauncherDestructor(LauncherDestructor launcherDestructor) {
    	LauncherDestructorDao.save(launcherDestructor);
    }

    @Override
    public void saveMissileDestructor(MissileDestructor missileDestructor) {
    	MissileDestructorDao.save(missileDestructor);
    }

    @Override
    public void saveMissile(Missile missile) {
        missileDao.save(missile);
    }
}
