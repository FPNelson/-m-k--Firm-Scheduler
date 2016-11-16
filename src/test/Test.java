package test;

import java.util.ArrayList;
import java.util.List;

import scheduler.Scheduler;
import task.Task;
import task.TaskComparator;
import task.TaskInstanceComparator;

public class Test {
	public static void main(String[] args) {
		List<Task> list = new ArrayList<Task>();
		list.add(new Task("R1", 3, 6, 2, 3));
		list.add(new Task("R2", 4, 7, 2, 3));
		Scheduler.createSchedule(list, new TaskComparator(), new TaskInstanceComparator());
	}
}