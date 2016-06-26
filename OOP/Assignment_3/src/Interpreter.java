import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import nodes.*;
import visitor.*;


/**
 * <p>
 * A class that represents the whole interpreter. It provides a main() function 
 * will read the source files and interpret them accordingly.
 * </p>
 * @author Theodor Stoican
 *
 */
public class Interpreter {
	
	Interpreter() {

	}
	/**
	 * <p> A method used for parsing interpreting the text and printing
	 * the AST (it uses the visitor pattern and prints the result in two
	 * specific files given by the parameters).
	 * </p>
	 * @param currentProgram the root node of the AST
	 * @param counterFile the number of file to be created
	 */
	void parse (ProgramNode currentProgram, int counterFile) {
		PrintWriter writer, writer2;
		String outputFile="./output/test" + counterFile + ".ast";
		String outputFile2="./output/test" + counterFile + ".out";
		try {
			writer = new PrintWriter(outputFile);
			writer2 = new PrintWriter(outputFile2);
			Visitor v = new PrintVisitor(writer);
			Visitor w = new InterpretVisitor(writer2);
			currentProgram.accept(v);
			currentProgram.accept(w);
			writer.close();
			writer2.close();
		} catch (FileNotFoundException e) {
			System.out.println("Not able to print");
			System.exit(1);
		}
	}
	/**
	 * <p> The main() method that does the parsing part and creates the AST 
	 * </p>
	 * @param args command line arguments
	 */
	public static void main(String[] args)  {
		String wordlist = new String("./wordlists");
		Interpreter interp = new Interpreter();		
		int counterFile = 1;
		List<String> allCharacters = new ArrayList<String>(); 
		//cand am multe operatii de tip sum si difference, mathSym retine 
		//elementele si le scoate cand nu mai pot adauga copii(astfel, stiu 
		//care sunt copii caror parinti).
		List<MathNode> mathSym = new ArrayList<MathNode>();
		//characters este un array care retine ce personaje sunt in scena 
		//la un anumit moment
		String[] characters = new String[2];
		while (counterFile <= 10){
			String fileToRead = new String("./tests/test");
			fileToRead += counterFile;
			fileToRead += ".spl";
			try{
				characters[0] = null;characters[1]=null;
				int numCharacter = 0;
				int adjNum = 0;
				ProgramNode currentProgram = null;
				ActNode currentAct = null;
				SceneNode currentScene = null;
				AssignmentNode assNode = null;
				
				Parser parser = new Parser(wordlist, fileToRead);
				Parser.Token t = parser.getNext();
				currentProgram = new ProgramNode();
				while (t != null)
				{
					switch(t.type)
					{
					case irelevant: 
						if (t.value.charAt(t.value.length()-1) == ':'){
							//cand avem ceva de genul "Hamlet:", vrem sa stim
							//ce personaj vorbeste
								//setez indexul pe personajul caruia ii este destinata replica
							if (!(characters[numCharacter] == null 
									|| characters[(numCharacter+1)%2] == null)
									  && t.value.contains(characters[numCharacter]))
							{
								++numCharacter;
								numCharacter = numCharacter % 2;
							}
						}
						break;
					
					case keyword: 
						if (t.value.equals("Act"))
						{
							//obtin numarul actului
							t = parser.getNext();
							currentAct = new ActNode(t.value);
							currentProgram.addNode(currentAct);
						while (!(t.type == TYPE.terminator))
								t = parser.getNext();
						}	
						else if (t.value.equals("Scene"))
						{
							//obtin numarul scenei
							t = parser.getNext();
							currentScene = new SceneNode(t.value);
							currentAct.addNode(currentScene);
							while (!(t.type == TYPE.terminator))
								t = parser.getNext();
						}
						else if (t.value.equals("["))
						{
							//cazul introducerii sau stergerii de caractere
							t = parser.getNext();
							if (t.value.equals("Enter")) {
								while (!t.value.equals("]")) {
									t = parser.getNext();
									if (t.type == TYPE.character 
											&& allCharacters.contains(t.value) == true){
										//cand sterg un caracter, trebuie sa las 
										//contorul pe celalalt
										++numCharacter;
										numCharacter = numCharacter%2;
										characters[numCharacter] = t.value;
									}
								}
							}
							else if(t.value.equals("Exit") || t.value.equals("Exeunt")){
								while (!t.value.equals("]")){
									t = parser.getNext();
									if (t.type == TYPE.character 
											&& allCharacters.contains(t.value) == true){
										if (characters[(numCharacter+1)%2] != null 
												&& characters[(numCharacter+1)%2].equals(t.value)){
											characters[(numCharacter+1)%2] = null;	
										}
										else if (characters[numCharacter].equals(t.value)){
											characters[numCharacter] = null;
										}
										++numCharacter;
										numCharacter %= 2;
									}
								}
								//cazul exeunt
								if (characters[numCharacter] != null  && characters[(numCharacter+1)%2] != null)
								{
									characters[numCharacter] = null;
									characters[(numCharacter+1)%2] = null;
								}
							}
						}		
						break;
					case character:
						if (assNode != null)
						{
							RValNode node = new RValNode(t.value);
							while (mathSym.size() > 0 && !mathSym.get(mathSym.size()-1).canAddSons())
								mathSym.remove(mathSym.size()-1);
							if (mathSym.size() > 0)				
								mathSym.get(mathSym.size()-1).addSons(node);
							else { 
								if (assNode.canAddSons())
									assNode.addSons(node);
							}
							break;
						}
						allCharacters.add(t.value);
						while (t!= null && t.type != TYPE.terminator){
							t = parser.getNext();
						}
						break;
					case second_person:
						if (characters[numCharacter] == null 
							|| characters[(numCharacter+1)%2] == null) 
						{
							while (t.type != TYPE.terminator)
								t = parser.getNext();
							break;
						}
						else { 
							//intotdeauna creez un assignment node cand apare second_person
							assNode = new AssignmentNode();
							if (assNode.canAddSons())
								assNode.addSons(new LvalNode(characters[numCharacter]));
							currentScene.addNode(assNode);
						}
						break;
					case first_person_reflexive:
						RValNode node = new RValNode(characters[(numCharacter+1)%2]);
						while (mathSym.size() > 0 && !mathSym.get(mathSym.size()-1).canAddSons())
							mathSym.remove(mathSym.size()-1);
						if (mathSym.size() > 0)				
							mathSym.get(mathSym.size()-1).addSons(node);
						else if (assNode != null)
							if (assNode.canAddSons())
								assNode.addSons(node);
						break;
					case second_person_reflexive:
						node = new RValNode(characters[numCharacter]);
						while (mathSym.size() > 0 && !mathSym.get(mathSym.size()-1).canAddSons())
							mathSym.remove(mathSym.size()-1);
						if (mathSym.size() > 0)				
							mathSym.get(mathSym.size()-1).addSons(node);
						else if (assNode != null)
							if (assNode.canAddSons())
								assNode.addSons(node);
						break;
					//acelasi lucru se intampla pentru oricare adjectiv
					case negative_adjective:
					case neutral_adjective:
					case positive_adjective:
						++adjNum;
						break;
					case negative_noun:
						if (assNode != null){
							int value = (1<<adjNum) * (-1);
							allCharacters.add(characters[numCharacter]);
							ConstantNode constNode = new ConstantNode(value);
							if (mathSym.size()>0){
								if (mathSym.get(mathSym.size()-1).canAddSons()){
									mathSym.get(mathSym.size()-1).addSons(constNode);
								}
								else {
									mathSym.remove(mathSym.size()-1);
									if (mathSym.size()>0)
										mathSym.get(mathSym.size()-1).addSons(constNode);
	
								}
								adjNum = 0;
								break;
							}
							if (assNode.canAddSons())
								assNode.addSons(constNode);
							adjNum = 0;
							assNode = null;
							break;
						}
					//pentru neutru si positiv, avem acelasi lucru
					case neutral_noun:
					case positive_noun:
						if (assNode != null){
							int value = 1<<adjNum;
							allCharacters.add(characters[numCharacter]);
							ConstantNode constNode = new ConstantNode(value);
							if (mathSym.size()>0){
								//daca pot adauga copii, adaug 
								if (mathSym.get(mathSym.size()-1).canAddSons())
								{
									mathSym.get(mathSym.size()-1).addSons(constNode);
								}
								//daca nu, scot elementul (ca un pop se comporta functia)
								//si il adaug pe cel curent
								else {
									mathSym.remove(mathSym.size()-1);
									if (mathSym.size()>0)
										mathSym.get(mathSym.size()-1).addSons(constNode);
								}
								adjNum = 0;
								break;
							}
							if (assNode.canAddSons())
								assNode.addSons(constNode);
							adjNum = 0;
							break;
						}
					case math:
						//cazurile de noduri de operatii
						//numarul de adjective devine 0, datorita
						//sintaxei limbajului
						adjNum = 0;
						if (t.value.equals("difference")){
							DifferenceNode diff = new DifferenceNode();
							while (mathSym.size() > 0 && !mathSym.get(mathSym.size()-1).canAddSons())
								mathSym.remove(mathSym.size()-1);
							if (mathSym.size() > 0)				
								mathSym.get(mathSym.size()-1).addSons(diff);
							mathSym.add(diff);
						}
						else if (t.value.equals("sum")){
							SumNode sum = new SumNode();
							while (mathSym.size() > 0 && !mathSym.get(mathSym.size()-1).canAddSons())
								mathSym.remove(mathSym.size()-1);
							if (mathSym.size() > 0)				
								mathSym.get(mathSym.size()-1).addSons(sum);
							mathSym.add(sum);
						}
						else if (t.value.equals("product")){
							ProductNode prod = new ProductNode();
							while (mathSym.size() > 0 && !mathSym.get(mathSym.size()-1).canAddSons())
								mathSym.remove(mathSym.size()-1);
							if (mathSym.size() > 0)				
								mathSym.get(mathSym.size()-1).addSons(prod);
							mathSym.add(prod);
						}
						else if (t.value.equals("square")){
							SquareNode square = new SquareNode();
							while (mathSym.size() > 0 && !mathSym.get(mathSym.size()-1).canAddSons())
								mathSym.remove(mathSym.size()-1);
							if (mathSym.size() > 0)				
								mathSym.get(mathSym.size()-1).addSons(square);
							mathSym.add(square);
						}
						else if (t.value.equals("cube")){
							CubeNode cube = new CubeNode();
							while (mathSym.size() > 0 && !mathSym.get(mathSym.size()-1).canAddSons())
								mathSym.remove(mathSym.size()-1);
							if (mathSym.size() > 0)				
								mathSym.get(mathSym.size()-1).addSons(cube);
							mathSym.add(cube);
						}
						else if (t.value.equals("quotient")) {
							QuotientNode quot = new QuotientNode();
							while (mathSym.size() > 0 && !mathSym.get(mathSym.size()-1).canAddSons())
								mathSym.remove(mathSym.size()-1);
							if (mathSym.size() > 0)				
								mathSym.get(mathSym.size()-1).addSons(quot);
							mathSym.add(quot);
						}
						break;
					case speak:
						OutputNode out = null;
						if (t.value.equals("Open")){
							out = new OutputNode(characters[numCharacter], 'I');
						}
						else if (t.value.equals("Speak")){
							out = new OutputNode(characters[numCharacter], 'A');
						}
						currentScene.addNode(out);
						break;
					case terminator:
						if (assNode != null){
							while (mathSym.size() > 1)
							{
								mathSym.remove(mathSym.size()-1);
							}
							if (mathSym.size() == 1){
								if (assNode.canAddSons())
									assNode.addSons(mathSym.get(mathSym.size()-1));
								mathSym.remove(mathSym.size()-1);
							}
							//assNode devine null la intalnirea unui terminator,
							//pentru a putea initia un alt assignment node la finalul
							//propozitiei, dar nu si al replicii
							assNode = null;
						}
						break;
					default: 
						break;
					}					
					t = parser.getNext();
				}
				interp.parse(currentProgram, counterFile);
			} 
			catch (FileNotFoundException e){
				System.out.println("Error at files");
			}
			++counterFile;
		}
	}
}
