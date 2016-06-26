/**
 * <p>
 * An interface that provides a common pattern for every strategy that will be
 * implemented.
 * </p>
 * 
 * @author Theodor Stoican
 *
 */
public interface VerifierStrategy {
	public boolean checkIfEqual(int arg);

	public String getMessage();
}

/**
 * <p>
 * A class that implements the VerifierStrategy interface, representing a
 * certain strategy(execute was called more than once) that Mockito uses for
 * verification purposes.
 * </p>
 * 
 * @author Theodor Stoican
 *
 */
class LeastOnceStrategy implements VerifierStrategy {
	private String message;

	/**
	 * <p>
	 * An overriden method that checks if the given argument is greater than 1.
	 * </p>
	 * 
	 * @param int
	 *            an integer
	 * @return boolean true if arg is greater than 1, false, otherwise
	 */
	@Override
	public boolean checkIfEqual(int arg) {
		// TODO Auto-generated method stub
		message = new String("expected at least one call");
		if (arg >= 1)
			return true;
		return false;
	}

	/**
	 * <p>
	 * A method that returns a message, which will be afterwards used by
	 * MockitoException.
	 * </p>
	 * 
	 * @return String the message
	 */
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return message;
	}
}

/**
 * <p>
 * A class that implements the VerifierStrategy interface, representing a
 * certain strategy(execute was called only once) that Mockito uses for
 * verification purposes.
 * </p>
 * 
 * @author Theodor Stoican
 *
 */
class ExactlyOnceStrategy implements VerifierStrategy {
	private String message;

	/**
	 * <p>
	 * An overriden method that checks if the given argument equals with 1.
	 * </p>
	 * 
	 * @param int
	 *            an integer
	 * @return boolean true if arg is greater than 1, false, otherwise
	 */
	@Override
	public boolean checkIfEqual(int arg) {
		// TODO Auto-generated method stub
		message = new String("expected " + 1 + " calls, got " + arg);
		if (arg == 1)
			return true;
		return false;
	}

	/**
	 * <p>
	 * A method that returns a message, which will be afterwards used by
	 * MockitoException.
	 * </p>
	 * 
	 * @return String the message
	 */
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return message;
	}
}

/**
 * <p>
 * A class that implements the VerifierStrategy interface, representing a
 * certain strategy(execute was called n times) that Mockito uses for
 * verification purposes.
 * </p>
 * 
 * @author Theodor Stoican
 *
 */
class TimesStrategy implements VerifierStrategy {
	int times;
	private String message;

	/**
	 * <p>
	 * A constructor that settles how many times we will check the execute
	 * method has been called.
	 * </p>
	 * 
	 * @param times
	 */
	public TimesStrategy(int times) {
		this.times = times;
	}

	/**
	 * <p>
	 * An overriden method that checks if the given argument equals with the
	 * values settled in constructor.
	 * </p>
	 * 
	 * @param int
	 *            an integer
	 * @return boolean true if arg is greater than 1, false, otherwise
	 */
	@Override
	public boolean checkIfEqual(int arg) {
		// TODO Auto-generated method stub
		message = new String("expected " + times + " calls, got " + arg);
		if (arg == times)
			return true;
		return false;
	}

	/**
	 * <p>
	 * A method that returns a message, which will be afterwards used by
	 * MockitoException.
	 * </p>
	 * 
	 * @return String the message
	 */
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return message;
	}
}