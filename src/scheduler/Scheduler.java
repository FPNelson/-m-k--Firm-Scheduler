package scheduler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTextArea;

import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

import task.Task;
import task.TaskInstance;

/**
 * Class to schedule a set of periodic tasks.
 * @author Franklin Nelson
 *
 */
public class Scheduler {
	/**
	 * Create an (m, k)-RMS schedule of the list of tasks given.
	 * @param tasks - List of tasks which need to be scheduled
	 * @param taskComparator - Scheduling algorithm used on the base tasks [RMS]
	 * @param taskInstanceComparator - Scheduling algorithm used on the task instances [(m, k)-Firm]
	 * @param textArea - Text box for all messages when scheduling
	 * @return TaskSeriesCollection of all tasks in schedule
	 */
	public static TaskSeriesCollection createSchedule(List<Task> tasks, Comparator<Task> taskComparator, Comparator<TaskInstance> taskInstanceComparator, JTextArea textArea) {
		Map<String, org.jfree.data.gantt.Task> taskMap = new LinkedHashMap<String, org.jfree.data.gantt.Task>();
		
		int curTaskStartTime = 0, curTime = 0;
		TaskInstance curTaskInstance = null;
		
		boolean schedulingFailed = false;
		
		List<TaskInstance> taskInstances = new ArrayList<TaskInstance>();
		
		Collections.sort(tasks, taskComparator);
		
		long[] periods = new long[tasks.size()];
		
		// Populate the first instances of each task
		for(int i = 0; i < tasks.size(); i++) {
			Task task = tasks.get(i);
			
			periods[i] = BigInteger.valueOf(task.getM()).gcd(BigInteger.valueOf(task.getK())).intValue();
			task.setM(task.getM() / (int)periods[i]);
			task.setK(task.getK() / (int)periods[i]);
			
			periods[i] = 0;
			
			// Check if any other task instances have the same optional instances
			for(int j = 0; j < i; j++) {
				if(task.getK() == tasks.get(j).getK()) {
					periods[i] += Math.min(task.getM(), tasks.get(j).getM());
				}
			}
			
			taskInstances.add(new TaskInstance(task, 0, (int)periods[i], i, tasks.size(), 0));
			
			periods[i] = task.getP();
			curTime = Math.max(curTime, (int)periods[i] * task.getK());
		}
		
		// The total length of the schedule
		long lcm = Math.max(SchedulerUtils.lcm(periods), curTime) - 1;
		
		
		double[] taskMQR = new double[tasks.size()];
		
		for(int i = 0; i < tasks.size(); i++) {
			taskMap.put(tasks.get(i).getName(), SchedulerUtils.createTask(tasks.get(i).getName(), 1, (int)lcm));
			taskMQR[i] = 0.0;
		}
		
		curTime = 0;
		
		for(boolean curTimeUsed = false; curTime <= lcm; curTimeUsed = false) {
			schedulingFailed = checkDeadlines(schedulingFailed, taskInstances, curTime, textArea);
			
			Collections.sort(taskInstances, taskInstanceComparator);
			
			for(int i = 0; i < tasks.size() && !curTimeUsed; i++) {
				TaskInstance taskInstance = taskInstances.get(i);
				
				if(taskInstance.getR() <= curTime) {
					// This is a continuing task instance
					if(!taskInstance.equals(curTaskInstance)) {
						if(curTaskInstance != null) {
							org.jfree.data.gantt.Task task = SchedulerUtils.createTask(curTaskInstance.getParent().getName(), curTaskStartTime-1, curTime);
							task.setPercentComplete(curTaskInstance.isMandatory() ? 1.0 : 0.0);
							taskMap.get(curTaskInstance.getParent().getName()).addSubtask(task);
						}
						
						curTaskStartTime = curTime + 1;
						curTaskInstance = taskInstance;
					}
					
					// Check if the task instance can execute
					if(taskInstance.doComputation(curTime, curTime + 1)) {
						curTimeUsed = true;
						curTime++;
						taskMQR[tasks.indexOf(taskInstance.getParent())] += 1.0 / taskInstance.getParent().getC();
					}
					
					// Task instance is about to finish
					if(taskInstance.getT() < 1) {
						org.jfree.data.gantt.Task task = SchedulerUtils.createTask(curTaskInstance.getParent().getName(), curTaskStartTime-1, curTime);
						task.setPercentComplete(curTaskInstance.isMandatory() ? 1.0 : 0.0);
						taskMap.get(curTaskInstance.getParent().getName()).addSubtask(task);
						taskInstances.set(i, new TaskInstance(taskInstance.getParent(), taskInstance.getA() + 1, taskInstance.getA2(), i, taskInstances.size(), (taskInstance.getA() + 1) * taskInstance.getParent().getP()));
						curTaskInstance = null;
					}
				}
			}
			if(!curTimeUsed) {
				curTime++;
			}
		}
		
		TaskSeriesCollection taskCollection = new TaskSeriesCollection();
		TaskSeries taskSeries = new TaskSeries("");
		
		schedulingFailed = checkDeadlines(schedulingFailed, taskInstances, curTime, textArea);
		
		List<Double> avgMQR = new ArrayList<Double>();
		
		// Get the MQR of every task
		for(int i = 0; i < tasks.size(); i++) {
			Task task = tasks.get(i);
			
			double k = (double)(lcm + 1) / task.getP();
			double m = k * ((double)task.getM() / task.getK());
			
			if(task.getM() != task.getK()) {
				avgMQR.add((taskMQR[i] - m) / (k - m));
			}
			
			textArea.append(task.getName() + " MQR: (" + taskMQR[i] + " - " + m + ") / (" + k + " - " + m + ") = " + (task.getM() == task.getK() ? 0.0 : (taskMQR[i] - m) / (k - m)) + "\n");
		}
		
		double mqr = 0.0;
		
		textArea.append("Average MQR: " + (avgMQR.size() < 2 ? (avgMQR.isEmpty() ? "0.0" : "") : "("));
		
		for(int i = 0; i < avgMQR.size(); i++) {
			mqr += avgMQR.get(i);
			textArea.append(avgMQR.get(i) + (i < avgMQR.size() - 1 ? " + " : avgMQR.size() < 2 ? "" : ") / " + avgMQR.size() + " = " + mqr / avgMQR.size()));
		}
		
		textArea.append("\n" + (schedulingFailed ? "Scheduling Failed" : "Scheduling Succeeded"));
		
		// Populate the graph with task instances
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
	 * @param textArea - Text box for all messages when scheduling
	 * @return True if a deadline was missed, false otherwise
	 */
	public static boolean checkDeadlines(boolean missDeadline, List<TaskInstance> taskInstances, int curTime, JTextArea textArea) {
		for(int i = 0; i < taskInstances.size();) {
			TaskInstance taskInstance = taskInstances.get(i);
			
			if(taskInstance.getT() > 0 && taskInstance.isPastDeadline(curTime)) {
				if(taskInstance.isMandatory()) {
					textArea.append("Task " + taskInstance.getParent().getName() + " missed deadline at time " + curTime + ".\n");
					missDeadline = true;
				}
				
				taskInstances.set(i, new TaskInstance(taskInstance.getParent(), taskInstance.getA() + 1, taskInstance.getA2(), i, taskInstances.size(), (taskInstance.getA() + 1) * taskInstance.getParent().getP()));
			}
			else {
				i++;
			}
		}
		
		return missDeadline;
	}
}