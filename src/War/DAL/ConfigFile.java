package War.DAL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

public class ConfigFile {
	File f;
	Scanner s;
	public static Properties properties = new Properties();

	public ConfigFile() {
		try {
			properties.load(new FileInputStream("config.dal"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getValue(String title) {
		if (properties.containsKey(title))
			return properties.getProperty(title);
		return null;
	}
}
