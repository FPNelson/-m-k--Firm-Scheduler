package controller;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import scheduler.Scheduler;
import task.Task;
import task.TaskComparator;
import task.TaskInstanceComparator;
import view.View;

/**
 * Class for interacting with the task list.
 * @author Franklin Nelson
 *
 */
public class SchedulerController {
	private List<Task> tasks = new ArrayList<Task>();
	private View view;
	
	/**
	 * Constructor for SchedulerController tied to the view.
	 * @param view to tie the SchedulerController to
	 */
	public SchedulerController(View view) {
		this.view = view;
	}
	
	/**
	 * Adds a task to the list.
	 */
	public void addTask() {
		try {
			String name = this.view.fields[0].getText();
			int c = Integer.parseInt(this.view.fields[1].getText());
			int p = Integer.parseInt(this.view.fields[2].getText());
			int m = Integer.parseInt(this.view.fields[3].getText());
			int k = Integer.parseInt(this.view.fields[4].getText());
			
			for(Task task : tasks) {
				if(name.equals(task.getName())) {
					JOptionPane.showMessageDialog(this.view.getContentPane(), "Task name is already used", "Name Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			
			tasks.add(new Task(name, c, p, m, k));
			this.refreshTasks();
		}
		catch(NumberFormatException e) {
			JOptionPane.showMessageDialog(this.view.getContentPane(), "Please fill out all fields", "Input Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Edits the selected task from the list.
	 */
	public void editTask() {
		int selectedIndex = this.view.taskList.getSelectedIndex();
		if(selectedIndex >= 0) {
			try {
				String name = this.view.fields[0].getText();
				int c = Integer.parseInt(this.view.fields[1].getText());
				int p = Integer.parseInt(this.view.fields[2].getText());
				int m = Integer.parseInt(this.view.fields[3].getText());
				int k = Integer.parseInt(this.view.fields[4].getText());
				
				for(int i = 0; i < tasks.size(); i++) {
					if(i != selectedIndex && name.equals(tasks.get(i).getName())) {
						JOptionPane.showMessageDialog(this.view.getContentPane(), "Task name is already used", "Name Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				
				tasks.set(selectedIndex, new Task(name, c, p, m, k));
				this.refreshTasks();
			}
			catch(NumberFormatException e) {
				JOptionPane.showMessageDialog(this.view.getContentPane(), "Please fill out all fields", "Input Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else {
			JOptionPane.showMessageDialog(this.view.getContentPane(), "No task is selected", "Selection Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Deletes the selected task from the list.
	 */
	public void deleteTask() {
		int selectedIndex = this.view.taskList.getSelectedIndex();
		if(selectedIndex >= 0) {
			tasks.remove(selectedIndex);
			this.refreshTasks();
		}
		else {
			JOptionPane.showMessageDialog(this.view.getContentPane(), "No task is selected", "Selection Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Schedules the tasks in the list.
	 */
	public void scheduleTasks() {
		if(this.tasks.size() > 0) {
			this.view.textArea.setText("");
			this.view.chartDataset = Scheduler.createSchedule(this.tasks, new TaskComparator(), new TaskInstanceComparator(), this.view.textArea);
		}
		
		this.view.refreshChartPanel();
	}
	
	/**
	 * Updates the task list.
	 */
	public void refreshTasks() {
		int selectedIndex = this.view.taskList.getSelectedIndex();
		
		((DefaultListModel<String>) this.view.taskListModel).removeAllElements();
		
		for(Task task : tasks) {
			((DefaultListModel<String>) this.view.taskListModel).addElement(task.toString());
		}
		
		this.view.taskList.setSelectedIndex(Math.min(selectedIndex, this.view.taskList.getModel().getSize() - 1));
	}
}
