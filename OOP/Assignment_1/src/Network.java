import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


/**
 * <p>
 * A class that mocks the social network and comprises all the necessary
 * operations (add, remove etc.). Implements the Singleton design pattern.
 * </p>
 * <p>
 * It has a list of users, which are, in fact, vertexes of a graph that models
 * the network.
 * </p> 
 * @author Theodor Stoican
 *
 */
public class Network {
	private List<User> users;
	private static final Network instance = new Network();
	/**
	 * <p>
	 * Constructs an ArrayList of users.
	 * </p>
	 * @param none
	 */
	private Network()
	{
		users = new ArrayList<User>();
	}
	/**
	 * <p>
	 * Obtain the one and only instance.
	 * </p>
	 * @param none 
	 * @return Network return the unique instance of the class
	 */
    public static Network getInstance() {
        return instance;
    }
	/**
	 * <p>
	 * A method that adds a user to the network, keeping the array
	 * of users sorted.
	 * </p>
	 * @param user the user to be added
	 * @return void, nothing returns, an output message will appear
	 */
	public void add (User user)
	{
		if (users.contains(user))
			System.out.println("DUPLICATE");
		int index = notFindDuplicate(user);
		if (index >= 0){
			users.add(index, user);
			System.out.println("OK");
		}
		else{
			System.out.println("DUPLICATE");
		}
	}
	/**
	 * <p>
	 * This method takes the ID of the user and deletes it from the network,
	 * keeping the array sorted.
	 * </p>
	 * @param ID the ID of the User that will be removed.
	 * @return void nothing is returned, an output message will appear
	 */
	public void remove(int ID){
		for (User existentUser : users){
			if (existentUser.getID() == ID){
				existentUser.removeSelfFromFriends();
				users.remove(existentUser);
				System.out.println("OK");
				return;
			}
		}
		System.out.println("INEXISTENT");
	}
	/**
	 * <p>
	 * method that takes two user IDs as parameters and make them friends
	 * within the network.
	 * </p>
	 * @param ID1 the id of one user
	 * @param ID2 the id of another user
	 * @return void nothing is return, an output message will appear
	 */
	public void makeFriends(int ID1, int ID2){
		User user1 = null;
		User user2 = null;
		for (User existentUser : users){
			if (existentUser.getID() == ID1){
				user1 = existentUser;
			}
			else if (existentUser.getID() == ID2){
				user2 = existentUser;
			}
			if (user1 != null && user2 != null){
				break;
			}
		}
		if (user1 == null || user2 == null){
			System.out.println("INEXISTENT");
		}
		else if (user1 != null && user2 != null){
			user1.addFriend(user2);
			user2.addFriend(user1);
			System.out.println("OK");
		}
	}
	/**
	 * <p>
	 * A method that takes two IDs as parameters and looks for 
	 * the users that have those IDs. In its inner methoding, it
	 * will call another method from the User class and unfriends these
	 * users from their inner lists of friends as well.
	 * </p>
	 * @param ID1 the ID of one user
	 * @param ID2 the ID of another user
	 * @return void nothing is returned, an output message of success or not
	 * will appear
	 */
	public void unfriend (int ID1, int ID2){
		User user1 = null;
		User user2 = null;
		for (User existentUser : users){
			if (existentUser.getID() == ID1)
				user1 = existentUser;
			else if (existentUser.getID() == ID2)
				user2 = existentUser;
		}
		if (user1 != null && user2 != null)
		{
			if (user1.removeFriend(user2) == true)
			{
				user2.removeFriend(user1);
				System.out.println("OK");
				return;
			}
		}
		System.out.println("INEXISTENT");
	}
	/**
	 * <p>
	 * Private method that is used only inside this class, for 
	 * methodality purposes. It optimizes the add method and looks for
	 * a duplicate inside the list of users.
	 * </p>
	 * @param user the user that will be looked for
	 * @return int returns -1 if a duplicate is found and a positive number -
	 * signifying the position at which it will be added - otherwise.
	 */
	private int notFindDuplicate(User user){		
		for (int i = 0; i < users.size(); ++i){
			if (users.get(i).getID() == user.getID()){
				//System.out.println("DUPLICATE");
				return -1;
			}
			else if(users.get(i).getID() > user.getID()){
				return i;
			}
		}
		return users.size();
	}
	/**
	 * <p>
	 * A basic method used for printing the whole network of users
	 * </p>
	 * @param none
	 * @return void type, nothing is returned.
	 */
	public void print(){
		if (users == null) return;
		for (User user : users)
		{
			user.printUser();
		}
	}
	/**
	 * <p>
	 * A method that prints everything related to a specific user in the
	 * network. If the user does not exist, a relevant message will show up
	 * at standard output.
	 * @param ID the ID of a user
	 * @return void nothing is returned
	 */
	public void print(int ID){
		for (User existentUser : users){
			if (existentUser.getID() == ID)
			{
				existentUser.printUser();
				return;
			}
		}
		System.out.println("INEXISTENT");
	}
	/**
	 * <p>
	 * This method is used for printing all the communities of users,
	 * who belong to the network. It uses a DFS algorithm, implemented
	 * on the User class.
	 * </p>
	 * @param no parameters are required
	 * @return void, nothing will be returned a message at STDOUT will be 
	 * printed
	 */
	public void printCommunities (){
		ArrayList<User> visited = new ArrayList<User>(users.size());
		ArrayList<User> connectedComp = new ArrayList<User>(users.size());
		if (users.size() == 0){
			//System.out.println("INEXISTENT");
			return;
		}
		for (User user : users){
			if (visited == null || !visited.contains(user))
			{
				visited.add(user);
				connectedComp.add(user);
				user.DFS(visited, connectedComp);
				System.out.print(connectedComp.size()+": ");
				for (User u : connectedComp)
				{
					System.out.print(u.getID()+" ");
				}
				System.out.println();
				connectedComp.clear();
			}
		}
	}
	/**
	 * <p>
	 * A method which will print the "length" of a community, i.e. the maximum
	 * distance between two vertexes (users). This number is found
	 * by applying BFS on evert user of a community.
	 * </p>
	 * @param ID the id of the user, whose community's strength
	 * will be printed
	 * @return void, nothing will be returned, a message at
	 * STDOUT will be printed
	 */
	public void printStrength (int ID){
		boolean found = false;
		User user = null;
		for (User u : users){
			if (u.getID() == ID){
				found = true;
				user = u;
				break;
			}
		}
		if (!found){
			System.out.println("INEXISTENT");
		}
		else if (user != null){
			ArrayList<User> visited = new ArrayList<User>();
			ArrayList<User> community = new ArrayList<User>(); 
			List<User> friends;
			friends = user.getFriends();
			/*
			 * the case when a user does not have any friend
			 */
			if (friends == null){
				System.out.println("0");
				return;
			}
			visited.add(user);
			community.add(user);
			user.DFS(visited, community);
			ArrayList<Integer> distances 
					= new ArrayList<Integer>(community.size());
			ArrayList<Integer> tempDistances 
					= new ArrayList<Integer>(community.size());
			Integer soFarMaximum = -1;
			for (int i = 0; i < community.size(); ++i){
				distances.add(0);
				tempDistances.add(0);
			}
			/*
			 * Apply BFS on every vertex in the community. Afterwards,
			 * calculate the maximum for every array. Finally, calculate
			 * the absolute maximum among all the maximum values.
			 */
			for (int i = 0; i < community.size(); ++i){
				community.get(i).BFS(i, tempDistances, community);
				int maximum = Collections.max(tempDistances);
				if (soFarMaximum < maximum)
					soFarMaximum = maximum;
				for (int j = 0;j < community.size();++j)
					tempDistances.set(j, 0);
			}
			System.out.println((community.size() - soFarMaximum) * 100
						/(community.size() - 1));
		}
	}
}