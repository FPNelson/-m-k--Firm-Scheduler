package scheduler;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import task.Task;

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
	 * Calculates the least common multiple of all values in array.
	 * @param periods of each task
	 * @return LCM of the task periods
	 */
	public static long lcm(Long[] periods) 
	{
		long lcm = periods[0];
		
		for(int i = 1; i < periods.length; i++) {
			lcm *= periods[i] / BigInteger.valueOf(lcm).gcd(BigInteger.valueOf(periods[i])).longValue();
		}
		
		return lcm;
	}
	
	/**
	 * Calculates the least common multiple of all values in list.
	 * @param periods of each task
	 * @return LCM of the task periods
	 */
	public static long lcm(List<Long> periods) {
		return lcm(periods.toArray(new Long[periods.size()]));
	}
	
	/**
	 * Performs (m, k)-RMS schedulability check on task array.
	 * @param tasks array
	 * @return True if mkLoad <= n(2^(1/n) - 1), false otherwise
	 */
	public static boolean isSchedulable(Task[] tasks) {
		return isSchedulable(Arrays.asList(tasks));
	}
	
	/**
	 * Performs (m, k)-RMS schedulability check on task list.
	 * @param tasks list
	 * @return True if mkLoad <= n(2^(1/n) - 1), false otherwise
	 */
	public static boolean isSchedulable(List<Task> tasks) {
		double mkLoad = 0d;
		
		for(Task task : tasks) {
			mkLoad += (double)(task.getC() * task.getM()) / (double)(task.getP() * task.getK());
		}
		
		return mkLoad <= tasks.size() * (Math.pow(2, 1d / tasks.size()) - 1);
	}
	
	public static Date dateYear(final int year) {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(year, 01, 01, 01, 0, 0);
		return calendar.getTime();
	}
}