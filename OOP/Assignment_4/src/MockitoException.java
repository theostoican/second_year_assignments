/**
 * <p>
 * A class that defines an exception, which will be thrown when the verification
 * method fails.
 * </p>
 * 
 * @author Theodor Stoican
 *
 */
public class MockitoException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MockitoException() {
		super();
	}

	public MockitoException(String message) {
		super(message);
	}
}
