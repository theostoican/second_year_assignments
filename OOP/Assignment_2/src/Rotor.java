import java.util.Arrays;
import java.util.HashMap;

/**
 * <p>
 * A class that implements two interfaces and implements the basic component of
 * an Enigma machine : a rotor. It deals with rotation of rotors, encoding and 
 * so on. All of these are implemented as inner functionalities.
 * </p>
 * @author Theodor Stoican
 *
 */

public class Rotor implements PipelineElement, Configuration {
	private Rotor neighbour1;
	private char offsetRing;
	private char offsetRotor;
	private char notch;
	private char[] notches;
	private int numRotor;
	private char output;
	private String alphabet;
	private HashMap<Character, Character> mapping;
	private HashMap<Character, Character> inverseMapping;
	/**
	 * <p>
	 * A constructor that deals with initialization of the class' fields.
	 * </p>
	 * @param none
	 * @return none
	 */
	public Rotor()
	{
		mapping = new HashMap<Character, Character>();
		inverseMapping = new HashMap<Character, Character>();
		notches = new char[2];
	}
	/**
	 * <p>
	 * A method that sets the alphabet for the rotor, necessary when
	 * realizing a specific mapping (encoding).
	 * </p>
	 * @return none
	 * @param alphabet a string that represents the alphabet
	 */
	public void setAlphabet(String alphabet){
		this.alphabet = alphabet;
	}
	/**
	 * <p>
	 * A method that sets up the configuration of a rotor. It will be useful
	 * when mapping a specific character. It is an overriden method.
	 * </p>
	 * @param elem an array of char representing pairs (A,B) which
	 * will be mapped one on another
	 * @return void nothing is returned
	 */
	public void setConfiguration(char[] elem) {
		for (int i = 0; i <= elem.length - 1; ++i)
		{
			mapping.put((char)(65+i),elem[i]);
			inverseMapping.put(elem[i], (char)(65+i));
		}
		
	}
	/**
	 * <p>
	 * A method which is used for setting the notches, Grundstellung, 
	 * Ringstellung and the number of the rotors which will be later
	 * used on computing the codification of a specific input.
	 * </p>
	 * @param numRotor the number of the rotor
	 * @param ringStellung the character offset of the ring
	 * @param grundStellung the character offset of the rotor
	 * @param notch a character which will chain rotations
	 * @void nothing is returned
	 */
	public void setStellung (int numRotor,char ringStellung,
			char grundStellung, char notch){
		this.numRotor = numRotor;
		this.offsetRing = ringStellung;
		this.offsetRotor = grundStellung;
		this.notch = notch;
	}
	/**
	 * <p>
	 * A method that overloads the other method with the same name, but
	 * which will be used for the 6th, 7th and 8th rotors, since these possess
	 * more than a single notch.
	 * </p>
	 * @param numRotor
	 * @param ringStellung
	 * @param grundStellung
	 * @param notch
	 * @return void nothing is returned
	 */
	public void setStellung (int numRotor, char ringStellung,
			char grundStellung, char[] notch){
		this.numRotor = numRotor;
		this.offsetRing = ringStellung;
		this.offsetRotor = grundStellung;
		this.notches = Arrays.copyOf(notch, notch.length);
	}
	/**
	 * <p>
	 * A method that will set a specific neighbour.
	 * </p>
	 * @param neighbour1 a neighbour of type Rotor
	 * @return void nothing is returned
	 */
	public void setNeighbour1(Rotor neighbour1){
		this.neighbour1 = neighbour1;
	}

	private void incrementOffsetRotor(){
		++offsetRotor;
	}

	private void rotate(){
		if (numRotor == 1 ){
			++offsetRotor;
			if (offsetRotor > 'Z')
				offsetRotor -= 26;
			//the ususal case
			if (offsetRotor == notch || offsetRotor == notches[0] 
					|| offsetRotor == notches[1]){
				neighbour1.incrementOffsetRotor();
			}
			//the double stepping case
			if (offsetRotor == notch + 1 || offsetRotor == notches[0] + 1 
					|| offsetRotor == notches[1] + 1)
			{
				neighbour1.rotate();
			}
			
		}
		else {
			//if the middle rotor has "almost" reached its notch, move the leftmost
			//rotor
			if (offsetRotor + 1 == notch || offsetRotor + 1 == notches[0] 
					|| offsetRotor + 1 == notches[1])
	
			{
				if (neighbour1 != null)
				{
					neighbour1.incrementOffsetRotor();
				}
				++offsetRotor;

			}
		}
	}
	/**
	 * <p>
	 * A method that encodes a specific character, calls the rotation function
	 * for the rotors and so on. It is based on direct or inverse mapping, depending
	 * therefore on an input.
	 * </p>
	 * @param input the character to be encoded
	 * @param type the type of the codification
	 * @return void, nothing is returned
	 */
	@Override
	public void transmitInput(char input, String type) {
		if (numRotor == 1 && type.equals("direct"))
			rotate();
		char diff = (char)(offsetRotor - offsetRing);
		output = (char)(input + diff);
		//verify not to have exceeded the ASCII limits for
		//the alphabet
		if (output > 'Z')
			output -=26;
		if (output < 'A')
			output += 26;
		if (alphabet.indexOf(output) < 0)
		{
			output = 0;
			return;
		}
		if (type.equals("direct")){
			output = mapping.get(output);
		}
		else if (type.equals("inverse"))
			output = inverseMapping.get(output);
		output = (char)(output - diff);
		//verify not to have to exceeded the ASCII limits, again
		if (output < 'A')
			output += 26;
		if (output > 'Z')
			output -= 26;
		if (alphabet.indexOf(output) < 0)
		{
			output = 0;
		}
	}
	/**
	 * <p>
	 * A method that will be called for getting back the encoded input.
	 * </p>
	 * @param none
	 * @return char returns the encoded character
	 */
	@Override
	public char receiveOutput() {
		return output;
	}
}