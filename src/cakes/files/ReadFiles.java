package cakes.files;

import java.io.File;
import java.io.FileNotFoundException;

public class ReadFiles {

	private static void processDirectory(File dir) throws FileNotFoundException {
		
		if ( !dir.isDirectory() ) {
			
			System.err.println("Error: \"" + dir.getAbsolutePath() + "\" is not a directory.");
			return;
		}
		
		for ( File file : dir.listFiles() ) {
			
			if ( file.isDirectory() )  processDirectory(file);	
			else  processFile(file);
		}		
	}
	

	private static void processFile(File file) throws FileNotFoundException {
		
		FileQueueManager.queueFile(file);
	}

	
	public static void start(final File dir) {
		
		Runnable queueLoader = new Runnable() {

			@Override
			public void run() {

				try {
					processDirectory(dir);
				}
				catch (FileNotFoundException e) {

					e.printStackTrace();
				}
			}			
		};

		new Thread(queueLoader).start();
	}
	
}
