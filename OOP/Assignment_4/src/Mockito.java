import java.util.ArrayList;

/**
 * <p>
 * A class that effectively represents the Mockito behavior. It provides certain
 * behaviors such as watch/verify by the usage of some static methods, as well
 * as some strategies with which you can perform some verifications on your
 * previous executions(at least once, times etc.).
 * </p>
 * 
 * @author Theodor Stoican
 *
 */
public class Mockito {
	private static ArrayList<Mock<?, ?>> mockedObjects;
	public static VerifierStrategy currentStrategy;

	/**
	 * <p>
	 * The very method used for mocking a real object. It is static and
	 * instantiates the Mock class.
	 * </p>
	 * 
	 * @param m
	 *            an object that implements the Mockable interface
	 * @return Mockable<A,B> a mock for the respective object, which implements
	 *         the aforementioned interface
	 */
	public static <A, B> Mockable<A, B> mock(Mockable<A, B> m) {
		Mock<A, B> mock = new Mock<A, B>();
		if (mockedObjects == null)
			mockedObjects = new ArrayList<Mock<?, ?>>();
		mockedObjects.add(mock);
		return mock;
	}

	/**
	 * <p>
	 * A method used for preparing the watching state.
	 * </p>
	 * 
	 * @return Mockito an instance of Mockito
	 */
	public static Mockito watch() {
		State.beginWatching();
		return new Mockito();
	}

	/**
	 * <p>
	 * A static method used for setting the current mock on which the user is
	 * performing operations.
	 * </p>
	 * 
	 * @param arg
	 *            the result of the non-initiated mock.execute() operation
	 * @return Mock the mock on which we are performing operations
	 */
	@SuppressWarnings("unchecked")
	public <A, B> Mock<A, B> when(Object arg) {
		return ((Mock<A, B>) State.getCurrentMock());
	}

	/**
	 * <p>
	 * A static method used for preparing the verification state.
	 * </p>
	 * 
	 * @param mock
	 *            a mock, on which the verification will be performed
	 * @param ver
	 *            a strategy of verification
	 * @return Mockable a mock with upcast to its parent
	 */
	public static <A, B> Mockable<A, B> verify(Mockable<A, B> mock,
			VerifierStrategy ver) {
		State.beginVerifying();
		currentStrategy = ver;
		return mock;
	}

	/**
	 * <p>
	 * A method used for preparing the verification, however, without mentioning
	 * the strategy this time.
	 * </p>
	 * 
	 * @param mock
	 *            a Mock object on which the verification is going to be
	 *            performed
	 * @return Mockable a mock with upcast to its parent
	 */
	public static <A, B> Mockable<A, B> verify(Mockable<A, B> mock) {
		State.beginVerifying();
		currentStrategy = new LeastOnceStrategy();
		return mock;
	}

	/**
	 * <p>
	 * A method used for getting the current strategy.
	 * </p>
	 * 
	 * @return an instantiation of the Strategy class
	 */
	public static VerifierStrategy getStrategy() {
		return currentStrategy;
	}

	/**
	 * <p>
	 * A static method used for creating a LeastOnceStrategy and for returning
	 * it.
	 * </p>
	 * 
	 * @return an instantion of the VerifierStrategy class
	 */
	public static VerifierStrategy atLeastOnce() {
		VerifierStrategy v = new LeastOnceStrategy();
		return v;
	}

	/**
	 * <p>
	 * A static method used for creating an ExactlyOnceStrategy and for
	 * returning it.
	 * </p>
	 * 
	 * @return an instantion of the VerifierStrategy class
	 */
	public static VerifierStrategy exactlyOnce() {
		VerifierStrategy v = new ExactlyOnceStrategy();
		return v;
	}

	/**
	 * <p>
	 * A static method used for creating a TimesStrategy and for returning it.
	 * </p>
	 * 
	 * @return an instantion of the VerificationStrategy class
	 * @param n
	 *            an integer signifying the number of times the execute method
	 *            was called
	 */
	public static VerifierStrategy times(int n) {
		VerifierStrategy v = new TimesStrategy(n);
		return v;
	}
}
