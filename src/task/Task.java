package task;

public class Task {
	/**
	 * Name of Task
	 */
	private String name;
	
	/**
	 * Computation Time
	 */
	private int c;
	
	/**
	 * Period
	 */
	private int p;
	
	/**
	 * Mandatory Deadlines
	 */
	private int m;
	
	/**
	 * Consecutive Instances
	 */
	private int k;
	
	/**
	 * Create a new task which cannot miss any deadlines.
	 * @param name - Task Name
	 * @param c - Computation Time
	 * @param p - Period
	 */
	public Task(String name, int c, int p) {
		this(name, c, p, 1, 1);
	}
	
	/**
	 * Create a new task which must complete m/k deadlines.
	 * @param name - Task Name
	 * @param c - Computation Time
	 * @param p - Period
	 * @param m - Mandatory Deadlines
	 * @param k - Consecutive Instances
	 */
	public Task(String name, int c, int p, int m, int k) {
		setName(name);
		setP(p);
		setC(c);
		setK(k);
		setM(m);
	}
	
	/**
	 * Get the name of this task.
	 * @return Name of task
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the name of this task.
	 * @param name to use for this task
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get the computation time of this task.
	 * @return Computation time of task
	 */
	public int getC() {
		return c;
	}
	
	/**
	 * Set the computation time of this task. The value must be<br>
	 * less than or equal to the period of the task.
	 * @param c value of new computation time
	 */
	public void setC(int c) {
		try {
			if(c > p) throw new IllegalArgumentException("c > p, changing value of p to " + c);
		}
		catch(IllegalArgumentException e) {
			e.printStackTrace();
			this.p = c;
		}
		
		this.c = c;
	}
	
	/**
	 * Get the period of this task
	 * @return Period of task
	 */
	public int getP() {
		return p;
	}
	
	/**
	 * Set the period of this task. The value must be<br>
	 * greater than or equal to the computation time of the task.
	 * @param p value of new period
	 */
	public void setP(int p) {
		try {
			if(p < c) throw new IllegalArgumentException("p < c, changing value of c to " + p);
		}
		catch(IllegalArgumentException e) {
			e.printStackTrace();
			this.c = p;
		}
		
		this.p = p;
	}
	
	/**
	 * Get the value 'm' of this task.
	 * @return Number of mandatory deadlines per 'k' consecutive instances
	 */
	public int getM() {
		return m;
	}
	
	/**
	 * Set the value 'm' of this task. The value must be<br>
	 * less than or equal to the value 'k' of the task.
	 * @param m value of new 'm'
	 */
	public void setM(int m) {
		try {
			if(m > k) throw new IllegalArgumentException("m > k, changing value of k to " + m);
		}
		catch(IllegalArgumentException e) {
			e.printStackTrace();
			this.k = m;
		}
		
		this.m = m;
	}
	
	/**
	 * Get the value 'k' of this task.
	 * @return Number of 'k' consecutive instances
	 */
	public int getK() {
		return k;
	}
	
	/**
	 * Set the value 'k' of this task. The value must be<br>
	 * greater than or equal to the value 'm' of the task.
	 * @param k value of new 'k'
	 */
	public void setK(int k) {
		try {
			if(k < m) throw new IllegalArgumentException("k < m, changing value of m to " + k);
		}
		catch(IllegalArgumentException e) {
			e.printStackTrace();
			this.m = k;
		}
		
		this.k = k;
	}
}