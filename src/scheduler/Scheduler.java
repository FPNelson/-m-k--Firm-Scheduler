package scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

import task.Task;
import task.TaskInstance;

public class Scheduler {
	/**
	 * Create an (m, k)-RMS schedule of the list of tasks given.
	 * @param tasks - List of tasks which need to be scheduled
	 * @param taskComparator - Scheduling algorithm used on the base tasks [RMS]
	 * @param taskInstanceComparator - Scheduling algorithm used on the task instances [(m, k)-Firm]
	 * @return TaskSeriesCollection of all tasks in schedule
	 */
	public static TaskSeriesCollection createSchedule(List<Task> tasks, Comparator<Task> taskComparator, Comparator<TaskInstance> taskInstanceComparator) {
		Map<String, org.jfree.data.gantt.Task> taskMap = new LinkedHashMap<String, org.jfree.data.gantt.Task>();
		
		int curTaskStartTime = 0;
		TaskInstance curTaskInstance = null;
		
		boolean schedulingFailed = false;
		
		List<TaskInstance> taskInstances = new ArrayList<TaskInstance>();
		
		Collections.sort(tasks, taskComparator);
		
		long[] periods = new long[tasks.size()];
		
		for(int i = 0; i < tasks.size(); i++) {
			Task task = tasks.get(i);
			
			int a2 = 0;
			for(int j = 0; j < i; j++) {
				if(task.getM() == tasks.get(j).getM() && task.getK() == tasks.get(j).getK()) {
					a2 += task.getM();
				}
			}
			
			taskInstances.add(new TaskInstance(task, 0, a2, i, 1 + tasks.size(), 0));
			periods[i] = task.getP();
		}
		
		long lcm = SchedulerUtils.lcm(periods) - 1;
		
		for(int i = 0; i < tasks.size(); i++) {
			taskMap.put(tasks.get(i).getName(), SchedulerUtils.createTask(tasks.get(i).getName(), 1, (int)lcm+1));
		}
		
		int curTime = 0;
		
		for(boolean curTimeUsed = false; curTime <= lcm; curTimeUsed = false) {
			schedulingFailed = checkDeadlines(schedulingFailed, taskInstances, curTime);
			
			Collections.sort(taskInstances, taskInstanceComparator);
			
			for(int i = 0; i < tasks.size() && curTimeUsed == false; i++) {
				TaskInstance taskInstance = taskInstances.get(i);
				
				if(taskInstance.getR() <= curTime) {
					if(!taskInstance.equals(curTaskInstance)) {
						if(curTaskInstance != null) {
							org.jfree.data.gantt.Task task = SchedulerUtils.createTask(curTaskInstance.getParent().getName(), curTaskStartTime, curTime+1);
							task.setPercentComplete(curTaskInstance.isMandatory() ? 1.0 : 0.0);
							taskMap.get(curTaskInstance.getParent().getName()).addSubtask(task);
						}
						
						curTaskStartTime = curTime + 1;
						curTaskInstance = taskInstance;
					}
					
					if(taskInstance.doComputation(curTime, curTime + 1)) {
						curTimeUsed = true;
						curTime++;
					}
					
					if(taskInstance.getT() < 1) {
						org.jfree.data.gantt.Task task = SchedulerUtils.createTask(curTaskInstance.getParent().getName(), curTaskStartTime, curTime+1);
						task.setPercentComplete(curTaskInstance.isMandatory() ? 1.0 : 0.0);
						taskMap.get(curTaskInstance.getParent().getName()).addSubtask(task);
						taskInstances.set(i, new TaskInstance(taskInstance.getParent(), taskInstance.getA() + 1, taskInstance.getA2(), i, 1 + taskInstances.size(), (taskInstance.getA() + 1) * taskInstance.getParent().getP()));
						curTaskInstance = null;
					}
					
					System.out.println("Time: " + curTime + ", Task: " + taskInstance.getParent().getName() + ", Instance: " + taskInstance.getA());
				}
			}
			if(curTimeUsed == false) {
				curTime++;
			}
		}
		
		TaskSeriesCollection taskCollection = new TaskSeriesCollection();
		TaskSeries taskSeries = new TaskSeries("Scheduled Tasks");
		
		schedulingFailed = checkDeadlines(schedulingFailed, taskInstances, curTime);
		
		for(String task : taskMap.keySet()) {
			if(taskMap.get(task).getSubtaskCount() > 0) {
				taskSeries.add(taskMap.get(task));
			}
			else {
				taskSeries.add(SchedulerUtils.createTask(task, 1, 1));
			}
		}
		
		taskCollection.add(taskSeries);
		
		return taskCollection;
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
				
				System.out.println("Deadline Missed - Time: " + (curTime + 1) + ", Task: " + taskInstance.getParent().getName() + ", Instance: " + taskInstance.getA() + ", Mandatory: " + taskInstance.isMandatory());
				taskInstances.set(i, new TaskInstance(taskInstance.getParent(), taskInstance.getA() + 1, taskInstance.getA2(), i, 1 + taskInstances.size(), (taskInstance.getA() + 1) * taskInstance.getParent().getP()));
				
				i--;
				continue;
			}
		}
		
		return missDeadline;
	}
}