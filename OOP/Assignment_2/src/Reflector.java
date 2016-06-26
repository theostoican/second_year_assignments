import java.util.HashMap;

/**
 * <p>
 * A class that implements the basic functionalities of a reflector, part of the
 * pipeline structure that models the inner working of the Enigma machine.
 * </p>
 * <p>
 * It has its own inner configuration, which can be transmitted as input. The 
 * configuration is one among two, identical to the old Nazi machine.
 * </p> 
 * @author Theodor Stoican
 *
 */

public class Reflector implements PipelineElement, Configuration {
	private HashMap<Character, Character> configuration;
	private char output;
	private String alphabet;
	/**
	 * <p> A constructor with no parameters that initializes the fields of 
	 * the class.
	 * </p>
	 * @param none
	 * @return none
	 */
	public Reflector(){
		configuration = new HashMap<Character, Character>();
	}
	/**
	 * <p> A method that sets up the alphabet on which the Reflector will
	 * work.
	 * </p>
	 * @param alphabet a string of letters representing the alphabet
	 * @return void none
	 */
	public void setAlphabet(String alphabet){
		this.alphabet = alphabet;
	}
	/**
	 * <p>
	 * A method which will set up the configuration for the reflector.
	 * </p>
	 * @param config an array of char used for configuration
	 * @return void none
	 */
	public void setConfiguration(char[] config) {
		int i;
		for (i = 0; i <= config.length - 1; i+=2)
		{
			configuration.put(config[i], config[i+1]);
			configuration.put(config[i+1], config[i]);
		}
	}
	/**
	 * <p>
	 * A method that is specific to the PipelineElement interface. It deals with
	 * transmitting the input to be codified.
	 * @param input the input character
	 * @param type a string, which says if it's direct or inverse mapping
	 * @return none
	 * </p>
	 */
	@Override
	public void transmitInput(char input, String type) {
		output = configuration.get(input);
		if (alphabet.indexOf(output) < 0)
			output = 0;	
	}
	/**
	 * <p>
	 * A method that sends back the output for further processing.
	 * @param none
	 * @return none
	 * </p>
	 */
	@Override
	public char receiveOutput() {
		return output;
	}
}
