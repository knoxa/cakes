package cakes.calendar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarXML {

	public static void main(String[] args) throws FileNotFoundException {
		
    	if ( args.length < 2 ) {
    		
    		System.err.println("Usage: CalendarXML <YEAR> <output file>");
    		System.exit(1);
    	}

    	int year = Integer.valueOf(args[0]);
    	String outputfile = args[1];
    	
    	Calendar calendar = Calendar.getInstance();	
		calendar.set(Calendar.YEAR, year);
		PrintWriter out = new PrintWriter(new File(outputfile));
		year(calendar, out);
		out.close();
	}
	
	public static void month(Calendar calendar, PrintWriter out) {
		
		SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM YYYY");
		
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

		int month = calendar.get(Calendar.MONTH);
		int year  = calendar.get(Calendar.YEAR);
		
		String mm = "0" + String.valueOf(month+1); mm = mm.substring(mm.length()-2);
		String yyyy = String.valueOf(year);
		String yyyymm = yyyy + mm;
		String monthText = String.format("%s", monthFormat.format(calendar.getTime()));
		
		int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR) - 1;
		String dd;
		
		out.write("<month id=\"" + yyyymm + "\" monthOfYear=\"" + month + "\" label=\"" + monthText + "\">\n");

		for (int i=0; i<daysInMonth; i++) {
			dd = "0" + String.valueOf(i+1); dd = dd.substring(dd.length()-2);
			out.write("<day id=\"" + yyyymm + dd + "\" dayOfWeek=\"" + (dayOfWeek + i)%7 + "\" weekOfMonth=\"" + (dayOfWeek + i)/7 + "\" dayOfMonth=\"" + i + "\" dayOfYear=\"" + dayOfYear++ + "\"/>\n");
		}

		out.write("</month>\n");
	}
	
	public static void year(Calendar calendar, PrintWriter out) {
		
		int year  = calendar.get(Calendar.YEAR);
		String yyyy = String.valueOf(year);

		out.write("<year id=\"" + yyyy + "\">\n");

		for (int i=0; i<12; i++) {
			calendar.set(Calendar.MONTH, i);
			month(calendar, out);
		}

		out.write("</year>\n");
	}
}

