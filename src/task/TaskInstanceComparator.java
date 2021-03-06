package task;

import java.util.Comparator;

/**
 * Orders the tasks using (m, k)-Firm algorithm, the task with the highest priority goes first.
 * @author Franklin Nelson
 *
 */
public class TaskInstanceComparator implements Comparator<TaskInstance> {
	/**
	 * Orders the tasks using (m, k)-Firm algorithm, the task with the highest priority goes first.
	 */
	public int compare(TaskInstance task1, TaskInstance task2) {
		return task1.isMandatory() ^ task2.isMandatory() ? task1.getP() - task2.getP() : task1.getParent().getP() - task2.getParent().getP();
	}
}