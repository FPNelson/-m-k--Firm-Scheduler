package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.data.category.IntervalCategoryDataset;

import controller.SchedulerController;

/**
 * Class for the GUI.
 * @author Franklin Nelson
 *
 */
public class View extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public IntervalCategoryDataset chartDataset;
	
	public JList<String> taskList;
	public ListModel<String> taskListModel;
	
	public JFormattedTextField[] fields = new JFormattedTextField[5];
	
	public JTextArea textArea = new JTextArea();
	
	private JTabbedPane tabbedPane = new JTabbedPane();
	private ChartPanel chartPanel;
	private JSplitPane ganttPane, taskPane;
	private JScrollPane scrollPane = new JScrollPane(this.textArea);
	private JPanel taskEditPanel, taskListPanel;
	
	private SchedulerController controller;
	
	public static void main(String[] args) {
		final View frame = new View();
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	/**
	 * Create the GUI for this simulator
	 */
	private View() {
		super("(m, k)-RMS Simulator");
		this.setSize(800, 500);
		
		this.taskListPanel = getTaskListPanel();
		this.taskEditPanel = getTaskEditPanel();
		
		this.textArea.setEditable(false);
		
		this.taskPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.taskEditPanel, this.taskListPanel);
		this.taskPane.setEnabled(false);
		
		this.chartPanel = getChartPanel(this.chartDataset, "(m, k)-RMS Schedule");
		
		this.ganttPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.chartPanel, this.scrollPane);
		this.ganttPane.setEnabled(false);
		
		this.tabbedPane.add("Task Editor", this.taskPane);
		this.tabbedPane.add("(m, k)-RMS Schedule", this.ganttPane);
		this.tabbedPane.setEnabledAt(1, false);
		
		this.refreshChartPanel();
		
		this.controller = new SchedulerController(this);
		
		this.setContentPane(this.tabbedPane);
		
		this.tabbedPane.setSelectedIndex(0);
	}
	
	/**
	 * Refreshes the panel containing the schedule.
	 */
	public void refreshChartPanel() {
		this.ganttPane.remove(this.chartPanel);
		this.chartPanel = getChartPanel(this.chartDataset, "(m, k)-RMS Schedule");
		this.ganttPane.add(this.chartPanel);
		
		this.tabbedPane.repaint();
		this.tabbedPane.setSelectedIndex(1);
		
		this.scrollPane.setPreferredSize(new Dimension(800, 50));
	}
	
	/**
	 * Creates the panel with the list of tasks.
	 * @return Panel containing list of tasks
	 */
	private JPanel getTaskListPanel() {
		this.taskListModel = new DefaultListModel<String>();
		this.taskList = new JList<String>(this.taskListModel);
		this.taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.taskList.setLayoutOrientation(JList.VERTICAL);
		this.taskList.setVisibleRowCount(-1);
		this.taskList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if(!taskList.isSelectionEmpty()) {
					String[] vals = taskList.getSelectedValue().split("\\s(?:=\\s\\{\\s|\\})|,\\s");
					for(int i = 0; i < fields.length; i++) {
						fields[i].setText(vals[i]);
					}
				}
			}
		});
		
		JScrollPane listScroller = new JScrollPane(this.taskList);
		listScroller.setPreferredSize(new Dimension(250, 80));
		
		JPanel listViewPanel = new JPanel(new BorderLayout());
		listViewPanel.setBorder(new TitledBorder("Task = { c, p, m, k }"));
		listViewPanel.add(listScroller);
		
		return listViewPanel;
	}
	
	/**
	 * Creates the panel with the task editor.
	 * @return Panel containing task editor
	 */
	private JPanel getTaskEditPanel() {
		JPanel panel = new JPanel();
		
		// Task Name
		this.fields[0] = new JFormattedTextField();
		this.fields[0].setPreferredSize(new Dimension(200, 20));
		JPanel nFieldBorder = new JPanel(new BorderLayout());
		nFieldBorder.setBorder(new TitledBorder("Task Name"));
		nFieldBorder.add(fields[0], BorderLayout.WEST);
		panel.add(nFieldBorder);
		
		// Computation Time
		this.fields[1] = new JFormattedTextField();
		this.fields[1].setPreferredSize(new Dimension(200, 20));
		JPanel cFieldBorder = new JPanel(new BorderLayout());
		cFieldBorder.setBorder(new TitledBorder("Computation Time (c)"));
		cFieldBorder.add(fields[1], BorderLayout.WEST);
		panel.add(cFieldBorder);
		
		// Period
		this.fields[2] = new JFormattedTextField();
		this.fields[2].setPreferredSize(new Dimension(200, 20));
		JPanel pFieldBorder = new JPanel(new BorderLayout());
		pFieldBorder.setBorder(new TitledBorder("Period (p)"));
		pFieldBorder.add(fields[2], BorderLayout.WEST);
		panel.add(pFieldBorder);
		
		// Mandatory Instances
		this.fields[3] = new JFormattedTextField();
		this.fields[3].setPreferredSize(new Dimension(200, 20));
		JPanel mFieldBorder = new JPanel(new BorderLayout());
		mFieldBorder.setBorder(new TitledBorder("Mandatory Instances (m)"));
		mFieldBorder.add(fields[3], BorderLayout.WEST);
		panel.add(mFieldBorder);
		
		// Consecutive Instances
		this.fields[4] = new JFormattedTextField();
		this.fields[4].setPreferredSize(new Dimension(200, 20));
		JPanel kFieldBorder = new JPanel(new BorderLayout());
		kFieldBorder.setBorder(new TitledBorder("Consecutive Instances (k)"));
		kFieldBorder.add(fields[4], BorderLayout.WEST);
		panel.add(kFieldBorder);
		
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
		
		// Schedule Button
		final JButton scheduleButton = new JButton("Create Schedule");
		scheduleButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		scheduleButton.setHorizontalTextPosition(SwingConstants.CENTER);
		scheduleButton.setSize(new Dimension(200, 30));
		scheduleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		scheduleButton.setEnabled(taskListModel.getSize() > 0);
		scheduleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tabbedPane.setEnabledAt(1, true);
				controller.scheduleTasks();
			}
		});
		
		// Add Button
		JButton addButton = new JButton("Add Task");
		addButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		addButton.setHorizontalTextPosition(SwingConstants.CENTER);
		addButton.setSize(new Dimension(200, 30));
		addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.addTask();
				scheduleButton.setEnabled(taskListModel.getSize() > 0);
			}
		});
		
		// Edit Button
		JButton editButton = new JButton("Edit Task");
		editButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		editButton.setHorizontalTextPosition(SwingConstants.CENTER);
		editButton.setSize(new Dimension(200, 30));
		editButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.editTask();
			}
		});
		
		// Delete Button
		JButton deleteButton = new JButton("Delete Task");
		deleteButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		deleteButton.setHorizontalTextPosition(SwingConstants.CENTER);
		deleteButton.setSize(new Dimension(200, 30));
		deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.deleteTask();
				scheduleButton.setEnabled(taskListModel.getSize() > 0);
			}
		});
		
		buttons.add(addButton);
		buttons.add(Box.createRigidArea(new Dimension(0, 10)));
		
		buttons.add(editButton);
		buttons.add(Box.createRigidArea(new Dimension(0, 10)));
		
		buttons.add(deleteButton);
		buttons.add(Box.createRigidArea(new Dimension(0, 10)));
		
		buttons.add(scheduleButton);
		buttons.add(Box.createRigidArea(new Dimension(0, 10)));
		
		panel.add(buttons);
		panel.setPreferredSize(new Dimension(250, 400));
		
		return panel;
	}
	
	/**
	 * Creates the panel with the schedule.
	 * @param chartDataset used to create the chart
	 * @param title of the chart
	 * @return Panel containing schedule
	 */
	private ChartPanel getChartPanel(IntervalCategoryDataset chartDataset, String title) {
		final JFreeChart chart = ChartFactory.createGanttChart(title, "Task", "Time", chartDataset, true, false, false);
		
		((DateAxis) chart.getCategoryPlot().getRangeAxis()).setDateFormatOverride(new SimpleDateFormat("Y"));
		((DateAxis) chart.getCategoryPlot().getRangeAxis()).setTickUnit(new DateTickUnit(DateTickUnitType.YEAR, 5));
		chart.getCategoryPlot().getRangeAxis().setMinorTickCount(5);
		chart.getCategoryPlot().getRangeAxis().setMinorTickMarksVisible(true);
		
		LegendItemCollection legendItems = new LegendItemCollection();
		legendItems.add(new LegendItem("Mandatory Task", Color.GREEN));
		legendItems.add(new LegendItem("Optional Task", Color.RED));
		chart.getCategoryPlot().setFixedLegendItems(legendItems);
		
		chart.getCategoryPlot().getDomainAxis().setLowerMargin(0);
		chart.getCategoryPlot().getDomainAxis().setUpperMargin(0);
		chart.getCategoryPlot().getDomainAxis().setCategoryMargin(0);
		
		chart.getCategoryPlot().getRenderer().setSeriesPaint(0, new Color(0f, 0f, 0f, 0f));
		
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(750, 350));
		return chartPanel;
	}
}