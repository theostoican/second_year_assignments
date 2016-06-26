package nodes;

import java.util.List;
import visitor.*;
/**
 * <p>
 * An interface used for every node inside the abstract syntax tree. 
 * Useful in operations.
 * </p>
 * @author Theodor Stoican
 *
 */
public interface ASTNode extends Visitable {
	/**
	 * <p>
	 * A method that will be implemented inside every class that represents a node of
	 * the AST, used for getting the suns of the respective node.
	 * </p>
	 * @return List<ASTNode> returns the sons of the node as a lists of
	 * ASTNode objects
	 * @param none
	 */
	List<ASTNode> getSons();
}
