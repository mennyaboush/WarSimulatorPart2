package War.DAL;


import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import War.Entities.Destination;
import War.Entities.Launcher;
import War.Entities.LauncherDestructor;
import War.Entities.Missile;
import War.Entities.MissileDestructor;

public class DALmongoDB implements DALInterface {

	private static DALmongoDB theService = null;
	private static Object mutex = new Object();
	//private Connection connection = null;
	private MongoClient mongoClient;
	private MongoDatabase database;

	private DALmongoDB() {
		mongoClient = new MongoClient("localhost", 27017);
		database = mongoClient.getDatabase("war");
		database.getCollection("missilelaunchers").deleteMany(new Document());
		database.getCollection("missiledestructors").deleteMany(new Document());
		database.getCollection("missile").deleteMany(new Document());
		database.getCollection("launcherdestructors").deleteMany(new Document());
		database.getCollection("destructedlauncher").deleteMany(new Document());
		database.getCollection("destructdmissiles").deleteMany(new Document());
	}

	public static DALmongoDB getInstance() {
		DALmongoDB result = theService;
		if (result == null) {
			synchronized (mutex) {
				result = theService;
				if (result == null)
					theService = result = new DALmongoDB();
			}
		}
		return result;
	}

	@Override
	public void saveLauncher(Launcher launcher) {
		MongoCollection<Document> collection = database.getCollection("missilelaunchers");
		Document doc = new Document("id", launcher.getId()).append("isHidden", launcher.isHidden());
		collection.insertOne(doc);
	}

	@Override
	public void saveMissileDestructor(MissileDestructor missileDestructor) {
		MongoCollection<Document> collection = database.getCollection("missiledestructors");
		Document doc = new Document("_id", missileDestructor.getId());
		collection.insertOne(doc);
	}

	@Override
	public void saveLauncherDestructor(LauncherDestructor launcherDestructor) {
		MongoCollection<Document> collection = database.getCollection("launcherdestructors");
		Document doc = new Document("_id", launcherDestructor.getId()).append("_type", launcherDestructor.getType());
		collection.insertOne(doc);	
	}

	@Override
	public void saveMissile(Missile missile, String launcherId, boolean hit) {
		MongoCollection<Document> collection = database.getCollection("missile");
		Document doc = new Document("_missileId",missile.getId())
				.append("destination", missile.getDestination().toString())
				.append("launchTime", missile.getLaunchTime())
				.append("flyTime", missile.getFlightTime())
				.append("damage", missile.getDamage())
				.append("isHit", hit)
				.append("launcherId",launcherId);
		collection.insertOne(doc);
	}

	@Override
	public void saveDestructedLauncher(LauncherDestructor launcherDestructor, Launcher launcher, long destructTime) {
		MongoCollection<Document> collection = database.getCollection("destructedlauncher");
		Document doc = new Document("launcherDestructorID", launcherDestructor.getId())
				.append("type",launcherDestructor.getType())
				.append("launcherId", launcher.getId())
				.append("destructTime", destructTime)
				.append("success", false);
		collection.insertOne(doc);
	}

	@Override
	public void saveDestructedMissile(MissileDestructor missileDestructor, Missile missile, long time) {
		MongoCollection<Document> collection = database.getCollection("destructdmissiles");
		Document doc = new Document("missileDestructorID", missileDestructor.getId())
				.append("missileID", missile.getId())
				.append("destructAfterLaunch", time)
				.append("success", false);
		collection.insertOne(doc);	
	}
}