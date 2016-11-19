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

public class SchedulerController {
	private List<Task> tasks = new ArrayList<Task>();
	private View view;
	
	public SchedulerController(View view) {
		this.view = view;
	}
	
	/**
	 * Adds a task to the list.
	 */
	public void addTask() {
		try {
			String name = this.view.fields[0].getText();
			
			for(Task task : tasks) {
				if(name.equals(task.getName())) {
					JOptionPane.showMessageDialog(this.view.getContentPane(), "Task name is already used", "Name Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			
			tasks.add(new Task(name,
					Integer.parseInt(this.view.fields[1].getText()),
					Integer.parseInt(this.view.fields[2].getText()),
					Integer.parseInt(this.view.fields[3].getText()),
					Integer.parseInt(this.view.fields[4].getText())));
			
			this.view.fields[0].setText("");
			this.view.fields[1].setText("");
			this.view.fields[2].setText("");
			this.view.fields[3].setText("");
			this.view.fields[4].setText("");
		}
		catch(Exception e) {
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
				
				Task tempTask = tasks.remove(selectedIndex);
				
				for(Task task : tasks) {
					if(name.equals(task.getName())) {
						JOptionPane.showMessageDialog(this.view.getContentPane(), "Task name is already used", "Name Error", JOptionPane.ERROR_MESSAGE);
						tasks.add(selectedIndex, tempTask);
						return;
					}
				}
				
				tasks.add(new Task(name,
						Integer.parseInt(this.view.fields[1].getText()),
						Integer.parseInt(this.view.fields[2].getText()),
						Integer.parseInt(this.view.fields[3].getText()),
						Integer.parseInt(this.view.fields[4].getText())));
				
				this.view.fields[0].setText("");
				this.view.fields[1].setText("");
				this.view.fields[2].setText("");
				this.view.fields[3].setText("");
				this.view.fields[4].setText("");
			}
			catch(Exception e) {
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
			this.view.chartDataset = Scheduler.createSchedule(this.tasks, new TaskComparator(), new TaskInstanceComparator());
		}
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
		
		if(this.view.taskList.getModel().getSize() > selectedIndex && selectedIndex > -1) {
			this.view.taskList.setSelectedIndex(selectedIndex);
		}
		else if(this.view.taskList.getModel().getSize() > 0) {
			this.view.taskList.setSelectedIndex(0);
		}
	}
}
