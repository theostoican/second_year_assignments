import java.util.List;
import java.util.ArrayList;

/**
 * <p>
 * A class that actually models the encoding machine. Once instantiated,
 * it can be given an input that will be codified and returned back.
 * </p>
 * <p>
 * The machine accepts rotors, reflector and plugboard for configuration 
 * purposes and uses them for codification.
 * </p>
 * @author Theodor Stoican
 *
 */

public class Enigma {
	private Rotor[] rotors;
	private List<PipelineElement> elements;
	
	/**
	 * <p>
	 * A constructor that takes as input the alphabet, the rotors and so on
	 * and initializes the class' inner fields.
	 * </p>
	 * @param alphabet the alphabet of the machine
	 * @param rotors instances of the rotors to be used
	 * @param refl an instance of the reflector class
	 * @param plug an instance of plugboard
	 */
	public Enigma (String alphabet, Rotor[] rotors,
			Reflector refl, Plugboard plug) {
		elements = new ArrayList<PipelineElement>();
		int i;
		this.rotors = rotors;
		elements.add(plug);
		plug.setAlphabet(alphabet);
		for (i = rotors.length - 1; i >= 0; --i)
		{
			rotors[i].setAlphabet(alphabet);
			elements.add(rotors[i]);
		}
		refl.setAlphabet(alphabet);
		elements.add(refl);
	}
	/**
	 * <p>
	 * A method that codifies a specific text given as input and prints it
	 * at standard output
	 * </p>
	 * @param input the text to be encoded
	 */
	public void encode (String input){
		for (int j=rotors.length-1;j>=1; --j)
		{
			rotors[j].setNeighbour1(rotors[j-1]);
		}
		try{
			for (int i = 0; i <= input.length() - 1; ++i){
				String type = new String("direct");
				char in = input.charAt(i);
				for (int j = 0; j <= elements.size() - 1; ++j){
					elements.get(j).transmitInput(in, type);
					in = elements.get(j).receiveOutput();
					if (in == 0)
						break;
				}
				if (in == 0) 
					break;
				type = "inverse";
				for (int j = elements.size() - 2; j >= 0; --j){
					elements.get(j).transmitInput(in, type);
					in = elements.get(j).receiveOutput();
					if (in == 0) break;
				}
				if (in == 0) continue;
				System.out.print(in);
			}
		}catch (NullPointerException e){
			return;
		}
	}
}
