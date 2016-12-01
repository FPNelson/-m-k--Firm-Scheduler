package task;

import java.util.Comparator;

/**
 * Orders the tasks using RMS, the task with lowest period goes first.
 * @author Franklin Nelson
 *
 */
public class TaskComparator implements Comparator<Task> {
	/**
	 * Orders the tasks using RMS, the task with lowest period goes first.
	 */
	public int compare(Task task1, Task task2) {
		return task1.getP() - task2.getP();
	}
}