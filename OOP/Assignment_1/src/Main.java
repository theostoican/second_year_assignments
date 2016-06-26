import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
/**
 * 
 * @author      Theodor Stoican <theo.stoican@gmail.com>
 * @since       28 October 2015 
 *
 */
public class Main {

	/**
	 * <p>
	 * This execution entry point class handles parsing and executing commands from the input
	 * file.
	 * </p>
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: java -cp <classpath> Main <input file>");
			System.exit(1);
		}
		FileParser input = new FileParser(args[0]);
		input.open();
		List<String> list = new ArrayList<String>();
		Network network = Network.getInstance();
		User user;
		list = input.parseNextLine();
		while (list != null)
		{
			//System.out.println(list.get(list.size()-1));
			if (list.contains("add"))
			{
				user = new User(Integer.parseInt(list.get(1)), list.get(2));
				network.add(user);
				//System.out.println(list);
			}
			else if (list.contains("remove")){
				network.remove(Integer.parseInt(list.get(1)));
			}
			else if (list.contains("friend")){
				network.makeFriends(Integer.parseInt(list.get(1)), Integer.parseInt(list.get(2)));
			}
			else if (list.contains("unfriend")){
				network.unfriend(Integer.parseInt(list.get(1)), Integer.parseInt(list.get(2)));
			}
			else if (list.contains("print")){
				if (list.contains("network"))
				{
					network.print();
				}
				else if (list.contains("communities"))
				{
					network.printCommunities();
				}
				else if (list.contains("strength")){
					network.printStrength(Integer.parseInt(list.get(2)));
				}
				else
					network.print(Integer.parseInt(list.get(2)));
			}
			list = input.parseNextLine();
		}
	}

}