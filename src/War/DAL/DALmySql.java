package War.DAL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import War.Entities.Destination;
import War.Entities.Launcher;
import War.Entities.LauncherDestructor;
import War.Entities.Missile;
import War.Entities.MissileDestructor;

public class DALmySql implements DALInterface {
	private static DALmySql theService = null;
	private Connection connection = null;
	private static Object mutex = new Object();

	private DALmySql() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String dbUrl = "jdbc:mysql://localhost:3306/war";
			connection = DriverManager.getConnection(dbUrl,"root","");//,"root","root123"
			resetDB();
		} catch (SQLException | ClassNotFoundException ex) {

			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ((SQLException) ex).getSQLState());
			System.out.println("VendorError: " + ((SQLException) ex).getErrorCode());
		}

	}

	private void resetDB() throws SQLException {
		connection.prepareStatement("DELETE FROM `destructdmissiles` WHERE 1").executeUpdate();
		connection.prepareStatement("DELETE FROM `destructedlauncher` WHERE 1").executeUpdate();
		connection.prepareStatement("DELETE FROM `launcherdestructors` WHERE 1").executeUpdate();
		connection.prepareStatement("DELETE FROM `launchers` WHERE 1").executeUpdate();
		connection.prepareStatement("DELETE FROM `missiledestructors` WHERE 1").executeUpdate();
		connection.prepareStatement("DELETE FROM `missiles` WHERE 1").executeUpdate();
	}

	public static DALInterface getInstance() {
		DALmySql result = theService;
		if (result == null) {
			synchronized (mutex) {
				result = theService;
				if (result == null)
					theService = result = new DALmySql();
			}
		}
		System.out.println("============================ mySql==============");
		return result;
	}

	@Override
	public void saveLauncher(Launcher launcher) {
		try {
			PreparedStatement stmt = connection
					.prepareStatement("INSERT INTO `launchers`(`id`, `isHidden`) VALUES (?,?)");
			stmt.setString(1, launcher.getId());
			stmt.setBoolean(2, launcher.isHidden());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveMissileDestructor(MissileDestructor missileDestructor) {
		try {
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO `missiledestructors`(`id`) VALUES (?)");
			stmt.setString(1, missileDestructor.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveLauncherDestructor(LauncherDestructor launcherDestructor) {
		try {
			PreparedStatement stmt = connection
					.prepareStatement("INSERT INTO `launcherdestructors`(`id`,`type`) VALUES (?,?)");
			stmt.setString(1, launcherDestructor.getId());
			stmt.setString(2, launcherDestructor.getType().toString());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}

	@Override
	public void saveMissile(Missile missile , String lancherId , boolean hit) {
		try {
			PreparedStatement stmt = connection.prepareStatement(
					"INSERT INTO `missiles`(`id`,`destination`,`launchTime`,`flyTime`,`damage`,`isHit`,`launcherId`) VALUES (?,?,?,?,?,?,?)");
			stmt.setString(1, missile.getId());
			stmt.setString(2, missile.getDestination().getDest());
			stmt.setInt(3, (int)missile.getLaunchTime());
			stmt.setInt(4, (int)missile.getFlightTime());
			stmt.setDouble(5, missile.getDamage());
			stmt.setBoolean(6, hit);
			stmt.setString(7, lancherId);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveDestructedLauncher(LauncherDestructor launcherDestructor, Launcher launcher,long destructTime) {
		try {
			PreparedStatement stmt = connection.prepareStatement(
					"INSERT INTO `DestructedLauncher`(`launcherDestructorId`,`launcherId`,`destructTime`) VALUES (?,?,?)");
			stmt.setString(1, launcherDestructor.getId());
			stmt.setString(2, launcher.getId());
			stmt.setInt(3, (int)destructTime);
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveDestructedMissile(MissileDestructor missileDestructor, Missile missile , long time) {
		try {
			PreparedStatement stmt = connection.prepareStatement(
					"INSERT INTO `destructdmissiles`(`missileDestructorID`,`missileID`,`destructAfterLaunch`) VALUES (?,?,?)");
			stmt.setString(1, missileDestructor.getId());
			stmt.setString(2, missile.getId());
			stmt.setInt(3, (int)time);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
}