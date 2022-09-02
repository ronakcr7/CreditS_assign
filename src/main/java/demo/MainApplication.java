package demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import demo.Objects.FinalEventData;
import demo.Objects.ProcessEventData;

public class MainApplication {
	public static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

	public static void main(String[] args) {

		logger.info("---START---");
		Scanner sc= new Scanner(System.in); //System.in is a standard input stream  
		logger.info("Enter the path of Event log file:");  
		logger.info("for Example:**C:\\Users\\ronak\\Downloads\\ChecksLog-master\\CreditS_assign\\src\\main\\resources\\logfile.txt**");  
		String filepath= sc.nextLine();    
		readEvents(filepath);
		logger.info("---END---");

	}

	protected static void readEvents(String path) {
		Map<String, ProcessEventData> map = new HashMap<>();
		try (Stream<String> lines = Files.lines(Paths.get(path))) {
			lines.forEach(line -> process(map, line));
		} catch (IOException e) {
			logger.error("File Path not found exception: {}", e.getMessage());
		}

	}

	private static void process(Map<String, ProcessEventData> map, String line) {
		ProcessEventData logEvent = new Gson().fromJson(line, ProcessEventData.class);
		ProcessEventData previus = map.putIfAbsent(logEvent.getId(), logEvent);
		if (previus != null) {
			FinalEventData event = getEventFromLogs(previus, logEvent);
			logger.debug(event.toString());
			map.remove(previus.getId());
		}
	}

	private static FinalEventData getEventFromLogs(ProcessEventData e1, ProcessEventData e2) {
		FinalEventData event = new FinalEventData();
		event.setId(e2.getId());
		event.setDuration(calDuration(e1.getTimestamp(), e2.getTimestamp()));
		event.setHost(e2.getHost());
		event.setType(e2.getType());
		event.setAlert(event.getDuration() > 4);
		return event;
	}

	private static long calDuration(long t1, long t2) {
		return t1 > t2 ? t1 - t2 : t2 - t1;
	}

	protected static boolean checkFile(String path) {
		try {
			Paths.get(path);

		} catch (InvalidPathException | NullPointerException ex) {
			logger.error("File Not Found");
			return false;
		}
		return true;
	}

}