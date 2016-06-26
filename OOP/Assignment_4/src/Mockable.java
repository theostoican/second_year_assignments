/**
 * 
 * <p>
 * An interface that defines a common pattern for the objects that Mockito can
 * mock. Mockito will interact only with objects that implement this interface.
 * A Mock object will also implement this interface.
 * </p>
 * 
 * @author Theodor Stoican
 *
 * @param <A>
 * @param <B>
 */
public interface Mockable<A, B> {
	/**
	 * <p>
	 * A method that will be implemented and that will simulate the behaviour of
	 * a mock , receiving no parameters.
	 * </p>
	 * 
	 * @return Unit an object to simulate void
	 * @throws RuntimeException
	 */
	Unit execute() throws RuntimeException;

	/**
	 * <p>
	 * A method that simulates the behaviour of a mock, providing an execute
	 * method with parameters.
	 * </p>
	 * 
	 * @param arg
	 * @return B a type given by the user
	 * @throws RuntimeException
	 */
	B execute(A arg) throws RuntimeException;
}
