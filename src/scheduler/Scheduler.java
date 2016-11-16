package scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import task.Task;
import task.TaskInstance;

public class Scheduler {
	/**
	 * Create an (m, k)-RMS schedule of the list of tasks given.
	 * @param tasks - List of tasks which need to be scheduled
	 * @param taskComparator - Scheduling algorithm used on the base tasks [RMS]
	 * @param taskInstanceComparator - Scheduling algorithm used on the task instances [(m, k)-Firm]
	 */
	public static void createSchedule(List<Task> tasks, Comparator<Task> taskComparator, Comparator<TaskInstance> taskInstanceComparator) {
		if(tasks.isEmpty()) {
			return;
		}
		
		boolean curTimeUsed = false;
		boolean schedulingFailed = false;
		
		List<TaskInstance> taskInstances = new ArrayList<TaskInstance>();
		
		Collections.sort(tasks, taskComparator);
		
		long[] periods = new long[tasks.size()];
		long lcm = 0;
		
		for(int i = 0; i < tasks.size(); i++) {
			Task task = tasks.get(i);
			
			taskInstances.add(new TaskInstance(task, 0, i, i + 1 + tasks.size(), 0));
			periods[i] = task.getP();
		}
		
		lcm = SchedulerUtils.lcm(periods) - 1;
		
		int curTime = 0;
		
		while(curTime <= lcm) {
			curTimeUsed = false;
			
			schedulingFailed = checkDeadlines(schedulingFailed, taskInstances, curTime);
			
			Collections.sort(taskInstances, taskInstanceComparator);
			
			for(int i = 0; i < tasks.size() && curTimeUsed == false; i++) {
				TaskInstance taskInstance = taskInstances.get(i);
				
				if(taskInstance.getR() <= curTime) {
					if(taskInstance.doComputation(curTime, curTime + 1)) {
						curTimeUsed = true;
						curTime++;
					}
					
					if(taskInstance.getT() < 1) {
						taskInstances.set(i, new TaskInstance(taskInstance.getParent(), taskInstance.getA() + 1, i, i + 1 + taskInstances.size(), (taskInstance.getA() + 1) * taskInstance.getParent().getP()));
					}
					
					System.out.println("Time: " + curTime + ", Task: " + taskInstance.getParent().getName());
				}
			}
			if(curTimeUsed == false) {
				curTime++;
			}
		}
	}
	
	/**
	 * Check all tasks and see if they have missed their deadlines.
	 * @param missDeadline - A task has already missed the deadline
	 * @param taskInstances - List of task instances to check for deadlines
	 * @param curTime - Current time in schedule
	 * @return True if a deadline was missed, false otherwise
	 */
	public static boolean checkDeadlines(boolean missDeadline, List<TaskInstance> taskInstances, int curTime) {
		for(int i = 0; i < taskInstances.size(); i++) {
			TaskInstance taskInstance = taskInstances.get(i);
			
			if(taskInstance.getT() > 0 && taskInstance.isPastDeadline(curTime)) {
				if(missDeadline == false) {
					missDeadline = true;
				}
				
				System.out.println("Deadline Missed - Time: " + (curTime + 1) + ", Task: " + taskInstance.getParent().getName());
				taskInstances.set(i, new TaskInstance(taskInstance.getParent(), taskInstance.getA() + 1, i, i + 1 + taskInstances.size(), (taskInstance.getA() + 1) * taskInstance.getParent().getP()));
				
				i--;
				continue;
			}
		}
		
		return missDeadline;
	}
}