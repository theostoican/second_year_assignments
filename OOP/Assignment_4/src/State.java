/**
 * <p>
 * A class that represents the state of Mockito, preparing it for listening to
 * certain behavioral commands, for executing or for verifying. It consists of
 * merely static methods and field.
 * </p>
 * 
 * @author Theodor Stoican
 *
 */
public class State {
	private static boolean isWatching;
	private static boolean isVerifying;
	private static Mock<?, ?> currentMock;

	/**
	 * <p>
	 * A static method used for monitoring the watching state.
	 * </p>
	 * 
	 * @return boolean a value signifying if it is watching or not
	 */
	public static boolean isWatching() {
		return (isWatching == true);
	}

	/**
	 * <p>
	 * A static method used by Mockito to signal the start of its watching.
	 * state.
	 * </p>
	 */
	public static void beginWatching() {
		isWatching = true;
		isVerifying = false;
	}

	/**
	 * <p>
	 * A static method that signifies the cease of the watching state.
	 * </p>
	 */
	public static void stopWatching() {
		isWatching = false;
	}

	/**
	 * <p>
	 * A static method that provides information about whether mockito is
	 * verifying something or not.
	 * </p>
	 * 
	 * @return boolean a value representing if Mockito is verifying or not
	 */
	public static boolean isVerifying() {
		return (isVerifying == true);
	}

	/**
	 * <p>
	 * A method used for telling Mockito to enter the verifying state.
	 * </p>
	 */
	public static void beginVerifying() {
		isVerifying = true;
		isWatching = false;
	}

	/**
	 * <p>
	 * A method that tells Mockito to exit the verifying state.
	 * </p>
	 */
	public static void stopVerifying() {
		isVerifying = false;
	}

	/**
	 * <p>
	 * A method that returns the respective mock object on which the user is
	 * performing some operations.
	 * </p>
	 * 
	 * @return Mock a Mock type object
	 */
	public static Mock<?, ?> getCurrentMock() {
		return currentMock;
	}

	/**
	 * <p>
	 * A static method used for setting a mock on which the user is performing
	 * some operations.
	 * </p>
	 * 
	 * @param mock
	 */
	public static void setCurrentMock(Mock<?, ?> mock) {
		currentMock = mock;
	}
}
