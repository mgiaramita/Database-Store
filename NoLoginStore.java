import java.util.Scanner;
import java.sql.*;

public class NoLoginStore {
	private Connection con;
	private Scanner sc;
	
	public NoLoginStore(Connection con){
		this.con = con;
		sc = new Scanner(System.in);
	}
	
	public void start(){
		//Need to be able to check available product (basic search) 
		//by keywords without sign-in. The resulting product list can be sorted by price.
		System.out.println("\nStore Successfully loaded!\n");
		
		int input = 0;
		while(input != 2){
			System.out.println("============================================================================");
			System.out.println("= 1) Search for products                                                   =");
			System.out.println("= 2) Leave Search                                                          =");
			System.out.println("=                                                                          =");
			System.out.println("= *Please note, product prices do not reflect current discounts.           =");
			System.out.println("= *Discounted prices will be calculated when products are added to cart.   =");
			System.out.println("============================================================================");
			
			if(sc.hasNextInt()){
				input = sc.nextInt();
			}
			else{
				sc.nextLine();
				System.out.println("Please enter and number 1-4");
			}
			
			if(input == 1){
				search();
			}
			else if(input == 2){
				System.out.println("Returning to menu...\n");
			}
			else{
				System.out.println("");
			}
		}
	}
	
	public void search(){
		System.out.println("Please enter product name: ");
		sc.nextLine();
		String input = sc.nextLine();
		String[] search = input.split(" ");
		
		System.out.println("\nSearch Results: ");
		System.out.println("ID\tName\t\tPrice\tDescription");
		
		Statement stmt = null;
	    String query = "SELECT id, description, name, price " +
	                   "FROM project.product " + 
	                   "WHERE active = 'yes'";
	    try {
	        stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery(query);

	        while (rs.next()) {
	        	int ID = rs.getInt("id");
	            String name = rs.getString("name");
	            String description = rs.getString("description");
	            float price = rs.getFloat("price"); 
	            
	            boolean inSearch = false;
	            //for each product check if the name contains a word from search
	            for(String str : search){
	            	if(name.contains(str)){
	            		inSearch = true;
	            	}
	            }
	            //if the product name matches with the search then it is shown in the users search
	            if(inSearch){
	            	System.out.println(ID + "\t" + name + "\t" + price + "\t" + description);
	            }
	        }
	    } catch (SQLException e ) {
	    	e.printStackTrace();
	    } finally {
	    	if (stmt != null) { 
	        	try { stmt.close();} catch (SQLException e) {}
	        } 
	    }
	    System.out.println("");
	}
}
