package wisc.madison.cs.cs707scale;

import java.io.File;
import java.io.FileWriter;

public class DirUtils {
	public static void storeDir(String location, String toStore) {
		if(!toStore.endsWith("/")) {
			toStore += "/";
		} 
		
		
		
		try {
			File f = new File(location);
			if(!f.exists()) {
				f.createNewFile();
			} else {
				toStore = "\n" + toStore;
			}
			
			FileWriter out = new FileWriter(f, true);
			out.write(toStore);
			out.close();
		} catch (Exception e) {
			
		}
	}
}
