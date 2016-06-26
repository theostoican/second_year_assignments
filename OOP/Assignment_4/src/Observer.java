/**
 * <p>
 * An interface that provides the necessary methods for an observer.
 * </p>
 * 
 * @author Theodor Stoican
 *
 */
public interface Observer {
	void reset();

	void update();

	int getCounter();
}

/**
 * <p>
 * A class used for monitoring the number of times the execute method was
 * called. It implements the Observer interface.
 * </p>
 * 
 * @author Theodor Stoican
 *
 */
class CounterObserver implements Observer {
	int counter;

	/**
	 * <p>
	 * A method that resets the counter to 0 every time it is called.
	 * </p>
	 */
	@Override
	public void reset() {
		counter = 0;
	}

	/**
	 * <p>
	 * A method that updates the counter, by incrementing it by one.
	 * </p>
	 */
	@Override
	public void update() {
		++counter;
	}

	/**
	 * <p>
	 * A method that the returns the computed counter.
	 * </p>
	 */
	@Override
	public int getCounter() {
		return counter;
	}
}