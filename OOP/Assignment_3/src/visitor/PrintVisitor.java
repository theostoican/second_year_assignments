package visitor;

import java.io.PrintWriter;
import java.util.List;
import nodes.ActNode;
import nodes.AssignmentNode;
import nodes.ConstantNode;
import nodes.CubeNode;
import nodes.LvalNode;
import nodes.OutputNode;
import nodes.ProductNode;
import nodes.ProgramNode;
import nodes.QuotientNode;
import nodes.RValNode;
import nodes.SceneNode;
import nodes.ASTNode;
import nodes.DifferenceNode;
import nodes.SquareNode;
import nodes.SumNode;

/**
 * <p>
 * A class that implements the Visitor class. It provides an algorithm
 * for every object of the tree in order to print the abstract syntax tree..
 * </p>
 * @author Theodor Stoican
 *
 */
public class PrintVisitor implements Visitor {
	private PrintWriter writer;
	private int levelIndentation;
	
	/**
	 * <p>
	 * Constructor that initializes the inner fields of this class.
	 * </p>
	 * @return none
	 * @param String a string that represents the scene number
	 */
	public PrintVisitor(PrintWriter writer){
		levelIndentation = 0;
		this.writer = writer;
	}
	/**
	 * <p>
	 * A method used for traversing the node's sons and for calling
	 * the accept method on each of them. It also calculates the necessary
	 * tab indentation and prints the type of the node.
	 * </p>
	 * @return none
	 * @param ProgramNode 
	 */
	@Override
	public void visit(ProgramNode node) {
		String nodeOutput="";
		for (int i = 0; i<levelIndentation; ++i)
			nodeOutput=nodeOutput.concat("\t");
		writer.println(nodeOutput+"ProgramNode");
		++levelIndentation;
		List<ASTNode> sons = node.getSons();
		for (ASTNode son : sons){
			son.accept(this);
			--levelIndentation;
		}
	}
	/**
	 * <p>
	 * A method used for traversing the node's sons and for calling
	 * the accept method on each of them. It also calculates the necessary
	 * tab indentation and prints the type of the node.
	 * </p>
	 * @return none
	 * @param ActNode 
	 */
	@Override
	public void visit(ActNode node) {
		String nodeOutput="";
		for (int i = 0; i < levelIndentation; ++i){
			nodeOutput=nodeOutput.concat("\t");
		}
		writer.println (nodeOutput+"ActNode" + " " + node.getActNumber());
		++levelIndentation;
		List<ASTNode> sons = node.getSons();
		for (ASTNode son : sons){
			son.accept(this);
			--levelIndentation;
		}
	}
	/**
	 * <p>
	 * A method used for traversing the node's sons and for calling
	 * the accept method on each of them. It also calculates the necessary
	 * tab indentation and prints the type of the node.
	 * </p>
	 * @return none
	 * @param SceneNode 
	 */
	@Override
	public void visit(SceneNode node) {
		String nodeOutput="";
		for (int i = 0; i<levelIndentation; ++i)
			nodeOutput=nodeOutput.concat("\t");
		writer.println(nodeOutput+"SceneNode"+" "+node.getSceneNumber());
		++levelIndentation;
		List<ASTNode> sons = node.getSons();
		for (ASTNode son : sons){
			if (son !=null){
				son.accept(this);
				--levelIndentation;
			}
		}
	}
	/**
	 * <p>
	 * A method used for traversing the node's sons and for calling
	 * the accept method on each of them. It also calculates the necessary
	 * tab indentation and prints the type of the node.
	 * </p>
	 * @return none
	 * @param AssignmentNode 
	 */
	@Override
	public void visit(AssignmentNode node) {
		String nodeOutput="";
		for (int i = 0; i < levelIndentation; ++i)
			nodeOutput=nodeOutput.concat("\t");
		writer.println(nodeOutput+"AssignmentNode");
		++levelIndentation;
		List<ASTNode> sons = node.getSons();
		for (ASTNode son : sons){
			son.accept(this);
			--levelIndentation;
		}
		
	}
	/**
	 * <p>
	 * A method used for traversing the node's sons and for calling
	 * the accept method on each of them. It also calculates the necessary
	 * tab indentation and prints the type of the node.
	 * </p>
	 * @return none
	 * @param LvalNode 
	 */
	@Override
	public void visit(LvalNode node) {
		String nodeOutput="";
		for (int i = 0; i < levelIndentation; ++i)
			nodeOutput=nodeOutput.concat("\t");
		writer.println(nodeOutput+"LvalNode " + node.getCharacter());
		++levelIndentation;
	}
	/**
	 * <p>
	 * A method used for traversing the node's sons and for calling
	 * the accept method on each of them. It also calculates the necessary
	 * tab indentation and prints the type of the node.
	 * </p>
	 * @return none
	 * @param ConstantNode 
	 */
	@Override
	public void visit(ConstantNode node) {
		String nodeOutput="";
		for (int i = 0; i < levelIndentation; ++i)
			nodeOutput=nodeOutput.concat("\t");
		writer.println(nodeOutput+"ConstantNode " + node.getValue());
		++levelIndentation;
	}
	/**
	 * <p>
	 * A method used for traversing the node's sons and for calling
	 * the accept method on each of them. It also calculates the necessary
	 * tab indentation and prints the type of the node.
	 * </p>
	 * @return none
	 * @param DifferenceNode 
	 */
	@Override
	public void visit(DifferenceNode node) {
		String nodeOutput="";
		for (int i = 0; i < levelIndentation; ++i)
			nodeOutput = nodeOutput.concat("\t");
		writer.println(nodeOutput+"DifferenceNode");
		List<ASTNode> sons = node.getSons();
		++levelIndentation;
		for (ASTNode son : sons)
		{
			son.accept(this);
			--levelIndentation;
		}
	}
	/**
	 * <p>
	 * A method used for traversing the node's sons and for calling
	 * the accept method on each of them. It also calculates the necessary
	 * tab indentation and prints the type of the node.
	 * </p>
	 * @return none
	 * @param SumNode 
	 */
	public void visit (SumNode node){
		String nodeOutput="";
		for (int i = 0; i < levelIndentation; ++i)
			nodeOutput=nodeOutput.concat("\t");
		writer.println(nodeOutput+"SumNode");
		List<ASTNode> sons = node.getSons();
		++levelIndentation;
		for (ASTNode son : sons)
		{
			son.accept(this);
			--levelIndentation;
		}
	}
	/**
	 * <p>
	 * A method used for traversing the node's sons and for calling
	 * the accept method on each of them. It also calculates the necessary
	 * tab indentation and prints the type of the node.
	 * </p>
	 * @return none
	 * @param RValNode 
	 */
	@Override
	public void visit(RValNode node) {
		String nodeOutput="";
		for (int i = 0; i < levelIndentation; ++i)
			nodeOutput=nodeOutput.concat("\t");
		writer.println(nodeOutput+"RValNode " + node.getCharacter());
		++levelIndentation;		
	}
	/**
	 * <p>
	 * A method used for traversing the node's sons and for calling
	 * the accept method on each of them. It also calculates the necessary
	 * tab indentation and prints the type of the node.
	 * </p>
	 * @return none
	 * @param OutputNode 
	 */
	@Override
	public void visit(OutputNode node) {
		String nodeOutput="";
		for (int i = 0; i<levelIndentation; ++i)
			nodeOutput = nodeOutput.concat("\t");
		writer.println(nodeOutput+"OutputNode " + node.getCharacter());
		++levelIndentation;			
	}
	/**
	 * <p>
	 * A method used for traversing the node's sons and for calling
	 * the accept method on each of them. It also calculates the necessary
	 * tab indentation and prints the type of the node.
	 * </p>
	 * @return none
	 * @param ProductNode 
	 */
	@Override
	public void visit(ProductNode node) {
		String nodeOutput="";
		for (int i = 0; i < levelIndentation; ++i)
			nodeOutput=nodeOutput.concat("\t");
		writer.println(nodeOutput+"ProductNode");
		List<ASTNode> sons = node.getSons();
		++levelIndentation;
		for (ASTNode son : sons)
		{
			son.accept(this);
			--levelIndentation;
		}
	}
	/**
	 * <p>
	 * A method used for traversing the node's sons and for calling
	 * the accept method on each of them. It also calculates the necessary
	 * tab indentation and prints the type of the node.
	 * </p>
	 * @return none
	 * @param SquareNode 
	 */
	@Override
	public void visit(SquareNode node) {
		String nodeOutput="";
		for (int i = 0; i < levelIndentation; ++i)
			nodeOutput=nodeOutput.concat("\t");
		writer.println(nodeOutput+"SquareNode");
		List<ASTNode> sons = node.getSons();
		++levelIndentation;
		for (ASTNode son : sons)
		{
			son.accept(this);
			--levelIndentation;
		}
	}
	/**
	 * <p>
	 * A method used for traversing the node's sons and for calling
	 * the accept method on each of them. It also calculates the necessary
	 * tab indentation and prints the type of the node.
	 * </p>
	 * @return none
	 * @param CubeNode 
	 */
	@Override
	public void visit(CubeNode node) {
		String nodeOutput="";
		for (int i = 0; i < levelIndentation; ++i)
			nodeOutput=nodeOutput.concat("\t");
		writer.println(nodeOutput+"CubeNode");
		List<ASTNode> sons = node.getSons();
		++levelIndentation;
		for (ASTNode son : sons)
		{
			son.accept(this);
			--levelIndentation;
		}
	}
	/**
	 * <p>
	 * A method used for traversing the node's sons and for calling
	 * the accept method on each of them. It also calculates the necessary
	 * tab indentation and prints the type of the node.
	 * </p>
	 * @return none
	 * @param QuotientNode 
	 */
	@Override
	public void visit(QuotientNode node) {
		String nodeOutput="";
		for (int i = 0; i < levelIndentation; ++i)
			nodeOutput=nodeOutput.concat("\t");
		writer.println(nodeOutput+"QuotientNode");
		List<ASTNode> sons = node.getSons();
		++levelIndentation;
		for (ASTNode son : sons)
		{
			son.accept(this);
			--levelIndentation;
		}
	}
}
