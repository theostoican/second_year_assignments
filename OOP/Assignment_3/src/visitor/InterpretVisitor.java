package visitor;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import nodes.ASTNode;
import nodes.ActNode;
import nodes.AssignmentNode;
import nodes.ConstantNode;
import nodes.CubeNode;
import nodes.DifferenceNode;
import nodes.LvalNode;
import nodes.OutputNode;
import nodes.ProductNode;
import nodes.ProgramNode;
import nodes.QuotientNode;
import nodes.RValNode;
import nodes.SceneNode;
import nodes.SquareNode;
import nodes.SumNode;

/**
 * <p>
 * A class that implements the Visitor class. It provides an algorithm
 * for every object of the tree in order to print the relevant output.
 * </p>
 * @author Theodor Stoican
 *
 */
public class InterpretVisitor implements Visitor {
	private int currentResult;
	private PrintWriter writer;
	private HashMap<String, Integer> characters;
	
	/**
	 * <p>
	 * Constructor that initializes the inner fields of this class.
	 * </p>
	 * @return none
	 * @param String a string that represents the scene number
	 */
	public InterpretVisitor(PrintWriter writer){
		this.writer = writer;
		currentResult = 0;
		characters = new HashMap<String, Integer>();
	}
	/**
	 * <p>
	 * A method used for traversing the node's sons and for calling
	 * the accept method on each of them.
	 * </p>
	 * @return none
	 * @param ProgramNode 
	 */
	@Override
	public void visit(ProgramNode node) {
		List<ASTNode> sons = node.getSons();
		for (ASTNode son : sons)
		{
			son.accept(this);
		}
	}
	/**
	 * <p>
	 * A method used for traversing the node's sons and for calling
	 * the accept method on each of them.
	 * </p>
	 * @return none
	 * @param ActNode an act-type node
	 */
	@Override
	public void visit(ActNode node) {
		List<ASTNode> sons = node.getSons();
		for (ASTNode son : sons)
		{
			son.accept(this);
		}
	}
	/**
	 * <p>
	 * A method used for traversing the node's sons and for calling
	 * the accept method on each of them.
	 * </p>
	 * @return none
	 * @param SceneNode 
	 */
	@Override
	public void visit(SceneNode node) {
		List<ASTNode> sons = node.getSons();
		for (ASTNode son : sons)
		{
			son.accept(this);
		}
	}
	/**
	 * <p>
	 * A method used for traversing the node's sons and for calling
	 * the accept method on each of them. It realizes the assignment operation
	 * as well.
	 * </p>
	 * @return none
	 * @param AssignmentNode 
	 */
	@Override
	public void visit(AssignmentNode node) {
		List<ASTNode> sons = node.getSons();
		//mai intai fac operatia si dupa atribuirea(de la dreapta la stanga)
		sons.get(1).accept(this);
		sons.get(0).accept(this);
	}
	/**
	 * <p>
	 * A method used for printing the necessary value in the required 
	 * format (ASCII or integer).
	 * </p>
	 * @return none
	 * @param OutputNode 
	 */
	@Override
	public void visit(OutputNode node) {
		int value = (int)(characters.get(node.getCharacter()));
		if (node.getType() == 'A'){
			char val = (char)value;
			writer.println(val);
		}
		else
			writer.println(value);
	}
	/**
	 * <p>
	 * A method used for getting the respective constant from the node.
	 * </p>
	 * @return none
	 * @param ConstantNode 
	 */
	@Override
	public void visit(ConstantNode node) {
		currentResult = node.getValue();
	}
	/**
	 * <p>
	 * A method used for getting the respective the respective values 
	 * from tis node's sons and for making the required difference.
	 * </p>
	 * @return none
	 * @param DifferenceNode 
	 */
	@Override
	public void visit(DifferenceNode node) {
		int result;
		List<ASTNode> sons = node.getSons();
		sons.get(0).accept(this);
		result = currentResult;
		sons.get(1).accept(this);
		currentResult = result - currentResult;
	}
	/**
	 * <p>
	 * A method used for getting the respective the respective values 
	 * from tis node's sons and for making the required sum.
	 * </p>
	 * @return none
	 * @param SumNode 
	 */
	@Override
	public void visit(SumNode node) {
		int result;
		List<ASTNode> sons = node.getSons();
		sons.get(0).accept(this);
		result = currentResult;
		sons.get(1).accept(this);
		currentResult = result + currentResult;
	}
	/**
	 * <p>
	 * A method used for calling the accept method on this node's son
	 * and for calculating the cube of the value obtaining from the son node.
	 * </p>
	 * @return none
	 * @param CubeNode 
	 */
	@Override
	public void visit(CubeNode node) {
		node.getSons().get(0).accept(this);
		currentResult = currentResult * currentResult * currentResult;
	}
	/**
	 * <p>
	 * A method used for getting the the respective values 
	 * from this node's sons and for calculating the required product.
	 * </p>
	 * @return none
	 * @param ProductNode 
	 */
	@Override
	public void visit(ProductNode node) {
		int result;
		List<ASTNode> sons = node.getSons();
		//se fac pe rand operatiile si rezultatul se retine local
		//pentru a putea efectua dupa aceea calculul final
		//analog se procedeaza si la celelalte noduri(difference,
		//product etc.).
		sons.get(0).accept(this);
		result = currentResult;
		sons.get(1).accept(this);
		currentResult = result * currentResult;
	}
	/**
	 * <p>
	 * A method used for getting the respective values 
	 * from this node's sons and for calculating the required division.
	 * </p>
	 * @return none
	 * @param QuotientNode 
	 */
	@Override
	public void visit(QuotientNode node) {
		int result;
		List<ASTNode> sons = node.getSons();
		sons.get(0).accept(this);
		result = currentResult;
		sons.get(1).accept(this);
		currentResult = result / currentResult;
	}
	/**
	 * <p>
	 * A method used for getting the respective value
	 * from this node's son and for calculating the required square.
	 * </p>
	 * @return none
	 * @param SquareNode 
	 */
	@Override
	public void visit(SquareNode node) {
		List<ASTNode> sons = node.getSons();
		sons.get(0).accept(this);
		currentResult = currentResult * currentResult;
	}
	/**
	 * <p>
	 * A method used for storing to a variable(character) a specific 
	 * value, previously calculated.
	 * </p>
	 * @return none
	 * @param LvalNode 
	 */
	@Override
	public void visit(LvalNode node) {
		characters.put(node.getCharacter(), currentResult);
	}
	/**
	 * <p>
	 * A method used for getting the value from a specific character in order
	 * to perform a calculation.
	 * </p>
	 * @return none
	 * @param RValNode
	 */
	@Override
	public void visit(RValNode node) {
		if (characters.containsKey(node.getCharacter()))
			currentResult = characters.get(node.getCharacter());
		else 
			currentResult = 0;
	}
}
