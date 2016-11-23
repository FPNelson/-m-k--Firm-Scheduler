package scheduler;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

public class SchedulerUtils {
	/**
	 * Calculates the least common multiple of all values in array.
	 * @param periods of each task
	 * @return LCM of the task periods
	 */
	public static long lcm(long[] periods) 
	{
		long lcm = periods[0];
		
		for(int i = 1; i < periods.length; i++) {
			lcm *= periods[i] / BigInteger.valueOf(lcm).gcd(BigInteger.valueOf(periods[i])).longValue();
		}
		
		return lcm;
	}
	
	/**
	 * Create a Gantt chart task object for the name, start time, and end time.
	 * @param name of task
	 * @param start time of task
	 * @param end time of task
	 * @return A Gantt chart task object for the name, start time, and end time
	 */
	public static org.jfree.data.gantt.Task createTask(String name, int start, int end) {
		return new org.jfree.data.gantt.Task(name, SchedulerUtils.dateTime(start), SchedulerUtils.dateTime(end));
	}
	
	/**
	 * Converts an int value into a Date object.
	 * @param time value to convert into Date
	 * @return Date object representing int value
	 */
	public static Date dateTime(final int time) {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(time, 01, 01, 01, 0, 0);
		return calendar.getTime();
	}
}