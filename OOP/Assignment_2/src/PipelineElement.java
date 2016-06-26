
/**
 * <p>
 * An interface that is used for modeling the pipeline data structure.
 * </p>
 * @author Theodor Stoican
 *
 */

public interface PipelineElement {
	/**
	 * <p>
	 * A method that can be implements by Enigma components for 
	 * receiving an input, for codifying it and storing it.
	 * </p>
	 * @param input the input to be codified
	 * @param type the type of mapping (direct or inverse)
	 */
	void transmitInput (char input, String type);
	/**
	 * <p>
	 * A method that can be overriden for getting back the input 
	 * that was earlier codified.
	 * </p>
	 * @return char an encoded character
	 */
	char receiveOutput ();
}
