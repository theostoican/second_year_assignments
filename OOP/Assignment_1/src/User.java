import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.LinkedList;
/**
 * <p>
 * A class that implements a member of the social network. It represents,
 * in fact, a vertex in a graph. Implements DFS and BFS, which are
 * used in the class Network for further use. Uses ArrayList as a data
 * structure for the users.
 * </p>
 * <p>
 * User and Network are based on a HAS-A relationship. More specifically, 
 * the class Network has a list of User objects, on which it bases
 * its operations. Inheritance would not be suitable in this context.
 * </p>
 * @author Theodor Stoican
 *
 */
public class User {
	private Integer ID;
	private String name;
	private List<User> friends;
	/**
	 * <p>
	 * Constructs a user with the id "ID" and the name "name",
	 * with an initial empty list of friends.
	 * </p>
	 * @param ID the id of the user
	 * @param name the name of the user
	 */
	public User(int ID, String name){
		this.ID = ID;
		this.name = name;
	}
	/**
	 * <p>
	 * This method is used for adding a friend of a specific user, while
	 * keeping the list of friends sorted.
	 * @param u a user to be added as friend
	 * @return void nothing is returned
	 */
	public void addFriend (User u){
		if (friends == null)
			friends = new ArrayList<User>();
		for (int i = 0; i < friends.size(); ++i){
			if (u.ID == friends.get(i).ID){
				return;
			}
			else if (u.ID < friends.get(i).ID){
				friends.add(i, u);
				return;
			}
		}
		friends.add(u);
		
	}
	/**
	 * <p>
	 * This method looks for a specific user inside <i>this</i> user's
	 * list of friends to see if it is a friend or not. Used as a support
	 * method for removeFriend. 
	 * </p>
	 * @param u a user that will be looked for in the list of
	 * friends
	 * @return true, if u is a friend of the current user and 
	 * false, otherwise
	 */
	private boolean findFriend (User u){
		if (friends.contains(u))
		{
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * <p>
	 * This method removes the friend <i>u</i> from the list
	 * of user <i>this</i> and returns a boolean value, according
	 * to the succes of the operation.
	 * </p>
	 * @param u a user to be removed
	 * @return true, if it is was removed and false, if either the 
	 * list is empty or the user does not belong to the list
	 */
	public boolean removeFriend (User u){
		if (friends != null && friends.remove(u) == true)
			return true;
		else return false;
	}
	/**
	 * <p>
	 * A method that is used to unfriend two users. The logic is the 
	 * following:the user <i>this</i> will delete itself from all of
	 * his friends and, afterwards, it will be deleted itself from the
	 * list of users.
	 * </p>
	 *@param no parameters required
	 *@return void nothing is returned
	 */
	public void removeSelfFromFriends(){
		if (friends != null){
			for (User friend : friends){
				if (friend.friends != null)
					friend.friends.remove(this);
			}
		}
	}
	/**
	 * <p>
	 * Getter for the ID field.
	 * </p>
	 * @param none
	 * @return int the ID of the user
	 */
	public int getID(){
		return ID;
	}
	/**
	 * <p>
	 * Getter for the name field.
	 * </p>
	 * @param none
	 * @return String
	 */
	public String getName(){
		return name;
	}
	/**
	 * <p>
	 * Getter for the list of friends.
	 * </p>
	 * @param none
	 * @return List returns a list of users
	 */
	public List<User> getFriends(){
		return friends;
	}
	/**
	 * <p>
	 * This method prints all the details of a specific user (i.e.
	 * ID, name and a list of friends).
	 * </p>
	 * @param none
	 * @return void nothing is returned
	 */
	public void printUser(){
		System.out.print(ID + " " + name + ": ");
		if (friends != null){
			for (User friend : friends){
				System.out.print(friend.ID + " ");
			}
		}
		System.out.println();
	}
	/**
	 * <p>
	 * This method does a DFS traversal, starting with the user <i>this</i>.
	 * @param visited an array of users, which marks the users which were
	 * visited so far 
	 * @param connectedComp an array that stores the users who belong
	 * to a certain community
	 * @return void, nothing is returned
	 */
	public void DFS (ArrayList<User> visited, ArrayList<User> connectedComp){
		if (friends == null)
			return;
		for (User user : friends){
			if (!visited.contains(user)){
				visited.add(user);
				connectedComp.add(user);
				user.DFS(visited, connectedComp);
			}
		}
	}
	/**
	 * <p>
	 * This method does a BFS traversal, starting with the user <i>this</i>.
	 * @param index an integer which is used to map the list of friends with
	 * the vector of visited
	 * @return void, nothing is returned
	 */   
	public void BFS (int index, ArrayList<Integer> distances,
			ArrayList<User> community){
		/*A queue, which will be required later on for storing the vertexes
		 *(users) of the network. The traversal will start from the current 
		 *user (this) and iterate through its lsit of friend.
		 */
		Queue<Integer> queue = new LinkedList<Integer>();
		ArrayList<Boolean> visited = new ArrayList<Boolean>(community.size());
		User u;
		for (int i = 0; i<community.size(); ++i)
			visited.add(false);
		queue.add(index);
		visited.set(index, true);
		while (!queue.isEmpty()){
			 index = queue.remove();
			 u = community.get(index);
			for (int i = 0; i < community.size(); ++i)
			{
				if (!visited.get(i) && u.friends.contains(community.get(i))){
					distances.set(i, 1 + distances.get(index));
					visited.set(i, true);
					queue.add(i);
				}
			}
		}
	}
}