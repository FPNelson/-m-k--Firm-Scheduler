package task;

public class TaskInstance {
	/**
	 * Parent Task
	 */
	private Task parent;
	
	/**
	 * Instance Number
	 */
	private int a;
	
	/**
	 * Instance Number modifier (for cases where 2 tasks are both optional in schedule)
	 */
	private int a2;
	
	/**
	 * Priority
	 */
	private int p;
	
	/**
	 * Deadline
	 */
	private int d;
	
	/**
	 * Remaining Computation Time
	 */
	private int t;
	
	/**
	 * Ready Time
	 */
	private int r;
	
	/**
	 * True - Mandatory<br>
	 * False - Optional
	 */
	private boolean isMandatory;
	
	/**
	 * Create a new task instance of a task.
	 * @param parent - Parent Task
	 * @param a - Instance Number
	 * @param p - Priority
	 * @param r - Ready Time
	 */
	public TaskInstance(Task parent, int a, int p, int r) {
		this(parent, a, p, p, r);
	}
	
	/**
	 * Create a new task instance of a task with priority determined by (m, k)-Firm scheduling.
	 * @param parent - Parent Task
	 * @param a - Instance Number
	 * @param pm - Mandatory Priority
	 * @param po - Optional Priority
	 * @param r - Ready Time
	 */
	public TaskInstance(Task parent, int a, int pm, int po, int r) {
		this(parent, a, 0, pm, po, r);
	}
	
	/**
	 * Create a new task instance of a task with priority determined by (m, k)-Firm scheduling.
	 * @param parent - Parent Task
	 * @param a - Instance Number
	 * @param a2 - Modifier for Instance Number
	 * @param pm - Mandatory Priority
	 * @param po - Optional Priority
	 * @param r - Ready Time
	 */
	public TaskInstance(Task parent, int a, int a2, int pm, int po, int r) {
		this.parent = parent;
		this.a = a;
		this.a2 = a2;
		this.setMandatory();
		this.p = isMandatory ? pm : po;
		this.d = parent.getP() * (a + 1);
		this.t = parent.getC();
		this.r = r;
	}
	
	/**
	 * Get the parent task which this is an instance of.
	 * @return Parent task
	 */
	public Task getParent() {
		return parent;
	}
	
	/**
	 * Set the parent task which this is an instance of.
	 * @param parent task to change to
	 */
	public void setParent(Task parent) {
		this.parent = parent;
		this.setMandatory();
		this.d = parent.getP() * (a + 1);
		this.t = parent.getC();
	}
	
	/**
	 * Get the instance number of this task instance.
	 * @return Value of instance number
	 */
	public int getA() {
		return a;
	}
	
	/**
	 * Set the instance number of this task instance.
	 * @param a value of new instance number
	 */
	public void setA(int a) {
		this.a = a;
		this.setMandatory();
		this.d = parent.getP() * (a + 1);
	}
	
	/**
	 * Get the instance number modifier of this task instance.
	 * @return Value of instance number modifier
	 */
	public int getA2() {
		return a2;
	}
	
	/**
	 * Set the instance number modifier of this task instance.
	 * @param a2 value of new instance number modifier
	 */
	public void setA2(int a2) {
		this.a2 = a2;
	}
	
	/**
	 * Get the priority of this task instance.
	 * @return Value of priority
	 */
	public int getP() {
		return p;
	}
	
	/**
	 * Set the priority of this task instance.
	 * @param p value of new priority
	 */
	public void setP(int p) {
		this.p = p;
	}
	
	/**
	 * Set the priority of this task instance based on (m, k)-Firm scheduling.
	 * @param pm value of new priority for mandatory task
	 * @param po value of new priority for optional task
	 */
	public void setP(int pm, int po) {
		this.p = isMandatory ? pm : po;
	}
	
	/**
	 * Get the deadline of this task instance.
	 * @return Value of deadline
	 */
	public int getD() {
		return d;
	}
	
	/**
	 * Set the deadline of this task instance.
	 * @param d value of new deadline
	 */
	public void setD(int d) {
		this.d = d;
	}
	
	/**
	 * Get the remaining computation time of this task instance.
	 * @return Value of remaining computation time
	 */
	public int getT() {
		return t;
	}
	
	/**
	 * Set the remaining computation time of this task instance.
	 * @param t value of new remaining computation time
	 */
	public void setT(int t) {
		this.t = t;
	}
	
	/**
	 * Get the ready time of this task instance.
	 * @return Value of ready time
	 */
	public int getR() {
		return r;
	}
	
	/**
	 * Set the ready time of this task instance.
	 * @param r value of new ready time
	 */
	public void setR(int r) {
		this.r = r;
	}
	
	/**
	 * Get the status of this task instance as mandatory or not.
	 * @return True if mandatory, false if optional
	 */
	public boolean isMandatory() {
		return isMandatory;
	}
	
	/**
	 * Check if this instance of the task is mandatory, then set the 'isMandatory' variable to the result.
	 */
	private void setMandatory() {
		this.isMandatory = (a + a2) == (int)Math.floor(Math.ceil((double)((a + a2) * parent.getM()) / parent.getK()) * ((double)parent.getK() / parent.getM()));
	}
	
	/**
	 * Executes the task from start to end and returns the result.
	 * @param start time
	 * @param end time
	 * @return True if task finishes before deadline, false otherwise
	 */
	public boolean doComputation(int start, int end) {
		if(end < start || end > d+1 || start > d) {
			return false;
		}
		else if(t >= (end - start)) {
			t -= (end - start);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if the current time is past the deadline.
	 * @param curTime of schedule
	 * @return True if time is past deadline, false otherwise
	 */
	public boolean isPastDeadline(int curTime) {
		return curTime >= d;
	}
}