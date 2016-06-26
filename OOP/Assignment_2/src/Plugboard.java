import java.util.HashMap;

/**
 * <p>
 * A class that implements the basic functionalities of a reflector, part of the
 * pipeline structure that models the inner working of the Enigma machine.
 * </p>
 * @author Theodor Stoican
 *
 */

public class Plugboard implements PipelineElement, Configuration {
	private HashMap<Character, Character> mapping;
	private char output;
	private String alphabet;
	/**
	 * <p>
	 * The constructor of the class. It initializes the class' fields.
	 * </p>
	 * @param none
	 * @return none
	 */
	public Plugboard(){
		mapping = new HashMap<Character, Character>();
	}
	/**
	 * <p>
	 * A method that sets up the alphabet on which the plugboard will
	 * later on work.
	 * </p>
	 * @return none
	 * @param none
	 * @param alphabet the alphabet of the machine
	 */
	public void setAlphabet(String alphabet){
		this.alphabet = alphabet;
	}
	/**
	 * <p>
	 * A method that sets up the configuration for the plugboard, necessary
	 * for further mapping.
	 * </p>
	 * @param elem an array of char in pairs (A,B), which will represent the configuration
	 */
	public void setConfiguration(char[] elem) {
		for (int i = 0 ; i <= elem.length -1; i += 2)
		{
			mapping.put(elem[i], elem[i+1]);
			mapping.put(elem[i+1], elem[i]);
		}
	}
	/**
	 * <p>
	 * A method that is used for encoding the input and for storing it.
	 * </p>
	 * @param input the character to be encoded
	 * @param type a string that indicates whether there is a direct or inverse
	 * mapping
	 * @exception catches a NullPointerException if the input is not
	 * among the letters within the alphabet
	 * @return void nothing is returned
	 */
	@Override
	public void transmitInput(char input, String type) {
		if (mapping.size() == 0)
			output = input;
		if (alphabet.indexOf(input) < 0){
			output=0;
			return;
		}
		try {
			output = mapping.get(input);
			if (alphabet.indexOf(output) < 0)
				output=0;
		} catch (NullPointerException e) {
			output = input;
		}		
	}
	/**
	 * <p>
	 * A method that sends the output earlier calculated back.
	 * </p>
	 * @param none
	 * @return none
	 */
	@Override
	public char receiveOutput() {
		return output;
	}
}
