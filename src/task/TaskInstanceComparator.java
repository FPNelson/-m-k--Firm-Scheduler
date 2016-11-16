package task;

import java.util.Comparator;

public class TaskInstanceComparator implements Comparator<TaskInstance> {
	/**
	 * Orders the tasks using (m, k)-Firm algorithm, the task with the highest priority goes first.
	 */
	public int compare(TaskInstance task1, TaskInstance task2) {
		return task1.getP() - task2.getP();
	}
}