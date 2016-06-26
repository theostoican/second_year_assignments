import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * <p>
 * A class that is used for testing the enigma machine, for providing 
 * input, for creating Enigma elements and for testing purposes.
 * </p> 
 * @author Theodor Stoican
 *
 */

public class Main {
	public static void main (String[] args){
		int contor = 1;
		String alphabet = new String();
		String[] plugboard = new String[0];
		char charReflector='\0';
		int[] rotorsNum = new int[3];
		String rotorPos = new String();
		String ringPos = new String();
		String[] temp = new String[0];
		Rotor[] rotors = new Rotor[3];
		String configuration = new String();
		Reflector reflector = new Reflector();
		Plugboard plug = new Plugboard();
		String textToEncript = null;
		char notch = '\u0000';
		char notches[] = Arrays.copyOf(StringsConfiguration.notch678, 2);
		
		//the part with reading from file
		Path file = Paths.get(args[0]);
		try (InputStream in = Files.newInputStream(file);
			    BufferedReader reader =
			      new BufferedReader(new InputStreamReader(in))) {
			    String line = null;
			    while ((line = reader.readLine()) != null) {
			    	//depending on what line it is, create a specific
			    	//Enigma element
			    	switch (contor) {
			    	case 1 :alphabet = line;
			    			++contor;
			    			break;
			    	case 2 :plugboard = line.split("\\)\\s\\(");
			    			if (plugboard.length != 1){
								temp = plugboard[0].split("\\(");
								plugboard[0] = temp[1];
								temp = plugboard[plugboard.length-1].split("\\)");
								plugboard[plugboard.length-1] = temp[0];
			    			}
							++contor;
							break;
			    	case 3 :temp = line.split("\\s");
			    			charReflector = temp[0].charAt(0);
			    			rotorsNum[0] = Integer.parseInt(temp[1]);
			    			rotorsNum[1] = Integer.parseInt(temp[2]);
			    			rotorsNum[2] = Integer.parseInt(temp[3]);
			    			++contor;
			    			break;
			    	case 4 :ringPos = line;
			    			++contor;
			    			break;
			    	case 5 :rotorPos = line;
			    			++contor;
			    			break;
			    	case 6: ++contor;
			    			break;
			    	default: if (textToEncript == null){
			    				textToEncript = line;
			    				}			
			    			 else {
			    				 textToEncript = textToEncript.concat(line);
			    			 }
			    			 break;
			    	}			    	
			    }
			    //set up the configuration for the rotors
			    for (int i = 0 ;i <= 2; ++i){	
				    switch (rotorsNum[i]) {
						case 1:
							configuration = StringsConfiguration.configuration1;
							notch = StringsConfiguration.notch1;
							break;
						case 2:
							configuration = StringsConfiguration.configuration2;
							notch = StringsConfiguration.notch2;
							break;
						case 3:
							notch = StringsConfiguration.notch3;
							configuration = StringsConfiguration.configuration3;
							break;
						case 4:
							notch = StringsConfiguration.notch4;
							configuration = StringsConfiguration.configuration4;
							break;
						case 5:
							notch = StringsConfiguration.notch5;
							configuration = StringsConfiguration.configuration5;
							break;
						case 6:
							configuration = StringsConfiguration.configuration6;
							break;
						case 7:
							configuration = StringsConfiguration.configuration7;
							break;
						case 8:
							configuration = StringsConfiguration.configuration8;
							break;
						default:
							break;
				    }
				    rotors[i] = new Rotor();
				    rotors[i].setConfiguration(configuration.toCharArray());
				    if (rotorsNum[i] < 6){
				    	rotors[i].setStellung(rotors.length - i , ringPos.charAt(i),
				    			rotorPos.charAt(i), notch);
				    }
				    else {
				    	rotors[i].setStellung(rotors.length - i  , ringPos.charAt(i), 
				    			rotorPos.charAt(i), notches);
				    }
			    }
			    //set up the configuration for the rotor
			    switch (charReflector){
				    case 'B': reflector.setConfiguration
				    			(StringsConfiguration.configurationReflB.toCharArray());
				    		  break;
				    case 'C': reflector.setConfiguration
				    			(StringsConfiguration.configurationReflC.toCharArray());
				    		  break;
				    default : break;
			    }

				char[] allchar = new char[plugboard.length * 2]; 
				int counter = 0;
				for (int i = 0; i < plugboard.length; i++) { 
				    for (int j = 0; j < plugboard[i].length(); j++) { 
				    	allchar[counter++] = plugboard[i].charAt(j);
				    }
				}
				plug.setConfiguration(allchar);
				
				//instantiate the Enigma machine and encode the specific text
				Enigma enigma = new Enigma (alphabet,rotors,reflector,plug);
				enigma.encode(textToEncript);
			} catch (IOException x) {
			    System.err.println(x);
			}
	}
}
