
/**
 * <p>
 * An interface that shows a common action for all the elements that make
 * up the Enigma machine. It helps providing a common method for setting
 * up the configuration of a specific element.
 * </p>
 * @author Theodor Stoican
 *
 */

public interface Configuration {
	/**
	 * <p>
	 * A method that will be overriden by Enigma components' classes
	 * and will be used for setting up the respective element's 
	 * configuration.
	 * </p>
	 * @param elem
	 */
	void setConfiguration(char[] elem);
}
