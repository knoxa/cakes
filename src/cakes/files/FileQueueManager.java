package cakes.files;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FileQueueManager {

	public static final long FILE_QUEUE_TIMEOUT = 10;

	private static BlockingQueue<File> files = null;

	private static int FILE_QUEUE_LENGTH = 4;

	public static synchronized BlockingQueue<File> getFileQueue() {
		
		if ( files == null ) {
			
			files = new ArrayBlockingQueue<File>(FILE_QUEUE_LENGTH);
		}
		return files;
	}
	

	public static void queueFile(File file) {
		
		try {
			FileQueueManager.getFileQueue().put(file);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
	
}
