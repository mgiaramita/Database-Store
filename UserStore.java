import java.util.Date;
import java.util.Scanner;
import java.util.ArrayList;
import java.sql.*;

public class UserStore {
	//Email and password of the current session user
	private String email, pass;
	//Connection to the database
	private Connection con;
	private Scanner sc;
	
	public UserStore(Connection con){
		this.con = con;
		sc = new Scanner(System.in);
	}
	
	public void start(String user_email, String user_pass){
		//Customers may create an account, update account information, or delete his/her account
		//Customers may add multiple products to one order and make a payment with the exact amount of the order. 
		//Only registered user can make orders. There may be some discount for certain products 
		//so the order price should be calculated accordingly.
		System.out.println("\nUser Store Successfully loaded!\n");
		
		email = user_email;
		pass = user_pass;
		
		int input = 0;
		while(input != 4){
			System.out.println("============================================================================");
			System.out.println("= 1) Search for products                                                   =");
			System.out.println("= 2) Create new order                                                      =");
			System.out.println("= 3) Edit account information                                              =");
			System.out.println("= 4) Log off                                                               =");
			System.out.println("============================================================================");
			
			//This keeps the program from crashing if a non-integer is read by the scanner
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
				createOrder();
			}
			else if(input == 3){
				boolean b = editAccount();
				if(b){
					//if person deleted account end session
					input = 4;
				}
			}
			else if(input == 4){
				System.out.println("\nThank You For Shopping With Us!");
				System.out.println("Please Come Again.");
			}
			else{
				System.out.println("\nInvaild Selection! Please try again.\n");
			}
		}
	}
	
	public void search(){
		//search through the products by id, name, and category
		int in = 0;
		
		while(in != 4){
			System.out.println("============================================================================");
			System.out.println("= 1) Search by Product ID                                                  ="); 
			System.out.println("= 2) Search by Product Name                                                =");
			System.out.println("= 3) Search by Product Category                                            =");
			System.out.println("= 4) Exit Search                                                           =");
			System.out.println("=                                                                          =");
			System.out.println("= *Please note, product prices do not reflect current discounts.           =");
			System.out.println("= *Discounted prices will be calculated when products are added to cart.   =");
			System.out.println("============================================================================");
			
			if(sc.hasNextInt()){
				in = sc.nextInt();
			}
			else{
				sc.nextLine();
				System.out.println("Please enter and number 1-4");
			}
			
			if(in == 1){
				//while the input given is not an integer keep asking for one
				boolean isInt = false;
				while(!isInt){
					System.out.println("Enter Product ID");
					if(sc.hasNextInt()){
						isInt = true;
						searchByID(sc.nextInt());
					}
					else{
						sc.nextLine();
						System.out.println("Invalid ID");
					}	
				}
			}
			else if(in == 2){
				System.out.println("Enter Product Name");
				//for some reason the scanner does not like to cooperate here so this is the solution
				String str1 = sc.next();
				String str2 = sc.nextLine();
				String str = str1 + str2;
				
				String[] search = str.split(" ");
				
				searchByName(search);
			}
			else if(in == 3){
				System.out.println("Product Categories Are:\n");
				//call method to print out categories and in correct parent child structure
				showCategories();
				System.out.println("\nEnter Product Category: ");
				sc.nextLine();
				searchByCategory(sc.nextLine());
			}
			else if(in == 4){
				System.out.println("Ending Search...\n");
			}
			else{
				System.out.println("Not a vaild selection.");
			}
		}
	}
	
	public void searchByID(int id) {
		System.out.println("\nSearch Results: ");
		System.out.println("ID\tName\t\tPrice\tDescription");
		Statement stmt = null;
	    String query = "SELECT id, description, name, price " +
	                   "FROM project.product " + 
	                   "WHERE active = 'yes' AND id = " + id;
	    try {
	        stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery(query);

	        while (rs.next()) {
	        	int ID = rs.getInt("id");
	            String name = rs.getString("name");
	            String description = rs.getString("description");
	            float price = rs.getFloat("price"); 
	            System.out.println(ID + "\t" + name +
	                               "\t" + price + "\t" + description);
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
	
	public void searchByName(String[] search){
		//want to search products by keywords, find those that contain any of the given words
		//given search string broken down by white space into array
		//only display products that contain words from search
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
	
	public void searchByCategory(String cat){
		System.out.println("\nSearch Results: ");
		System.out.println("ID\tName\t\tPrice\tDescription");
		Statement stmt = null;
		//if the selected category is a parent category
		if(isParent(cat)){
		    ArrayList<Integer> arr = new ArrayList<>();
		    
		    String query = "SELECT id " +
		                   "FROM project.category " + 
		                   "WHERE name <> '" + cat +"' AND parent = '" + cat + "'";
		    try {
		        stmt = con.createStatement();
		        ResultSet rs = stmt.executeQuery(query);

		        while (rs.next()) {        	
		            arr.add(rs.getInt("id"));
		        }
		    } catch (SQLException e ) {
		    	e.printStackTrace();
		    } finally {
		    	if (stmt != null) { 
		        	try { stmt.close();} catch (SQLException e) {}
		        } 
		    }
		    //get list of category_id of the categories that have cat as a parent
		    //now print the product info for each if those products
		    for(int id : arr){
		    	query = "SELECT id, description, name, price " +
		                   "FROM project.product " + 
		                   "WHERE active = 'yes' AND category_id = " + id;
			    try {
			        stmt = con.createStatement();
			        ResultSet rs = stmt.executeQuery(query);
		
			        while (rs.next()) {
			        	int ID = rs.getInt("id");
			            String name = rs.getString("name");
			            String description = rs.getString("description");
			            float price = rs.getFloat("price"); 
			            System.out.println(ID + "\t" + name +
			                               "\t" + price + "\t" + description);
			        }
			    } catch (SQLException e ) {
			    	e.printStackTrace();
			    } finally {
			    	if (stmt != null) { 
			        	try { stmt.close();} catch (SQLException e) {}
			        } 
			    }
		    }
		    System.out.println("");
		}
		//else the cat is not a parent
		else{
			String query = "SELECT id, description, name, price " +
	                   "FROM project.product " + 
	                   "WHERE active = 'yes' AND category_id = (SELECT id FROM project.category " +
	                   										   "WHERE name = '" + cat + "' OR parent = '" + cat + "')";
		    try {
		        stmt = con.createStatement();
		        ResultSet rs = stmt.executeQuery(query);
	
		        while (rs.next()) {
		        	int ID = rs.getInt("id");
		            String name = rs.getString("name");
		            String description = rs.getString("description");
		            float price = rs.getFloat("price"); 
		            System.out.println(ID + "\t" + name +
		                               "\t" + price + "\t" + description);
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
	
	public boolean isParent(String cat){
		Statement stmt = null;
	    String query = "SELECT name " +
	                   "FROM project.category " +
	                   "WHERE name = '" + cat + "' AND parent = '" + cat + "'";
	    try {
	        stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery(query);
	        //If query returns no results than the category is not a parent
	        if(!rs.next()){  
	        	return false;
	        }

	    } catch (SQLException e ) {
	    	e.printStackTrace();
	    } finally {
	        if (stmt != null) { 
	        	try { stmt.close();} catch (SQLException e) {} 
	        }
	    }
	    
		return true;
	}
	
	public void createOrder(){
		//order products by entering the id and quantity
		System.out.println("Enter the number of products that you want to order: ");
		while(!sc.hasNextInt()){
			sc.nextLine();
			System.out.println("Please enter a vaild integer quantity.");
		}
		int num = sc.nextInt();
		//add the desired products to the cart
		int[] idCart = new int[num];
		int[] qCart = new int[num];
		float[] priceCart = new float[num];
		
		for(int i = 0; i < num; i++){
			System.out.println("Enter Product ID Number: ");
			while(!sc.hasNextInt()){
				sc.nextLine();
				System.out.println("Please enter a vaild integer quantity.");
			}
			idCart[i] = sc.nextInt();
			
			System.out.println("Enter Quantity: "); 
			while(!sc.hasNextInt()){                  
				sc.nextLine();                        
				System.out.println("Please enter a vaild integer quantity.");
			}
			qCart[i] = sc.nextInt();
			//calculate the total price with the discounts for each item
			priceCart[i] = priceAfterDiscount(idCart[i]) * qCart[i];
		}
		//sum up prices to get the total
		float totalPrice = 0;
		for(float p : priceCart){
			totalPrice += p;
		}
		//check if order can be fulfilled if not exit order
		boolean valid = isOrderValid(idCart, qCart);
		if(valid){
			//Show user order and finalize if they don't approve cont will be false
			boolean cont = showUserOrder(idCart, qCart, priceCart, totalPrice);
			if(cont){
				float remainder = payOrder(totalPrice);
				if(remainder <= 0){
					//insert cart data to the appropriate tables (order + order_data)
					insertOrder(qCart, idCart, totalPrice, "yes");
				}
				else{
					insertOrder(qCart, idCart, totalPrice, "no");
				}
				
				//update the products to reflect the reduction in stock
				updateStock(idCart, qCart);
			}
		}
	}
	
	public float payOrder(float totalPrice){
		System.out.println("Amount Due: $" + totalPrice);
		System.out.print("Please input the amount you are paying: ");
		float payment = sc.nextFloat();
		
		return totalPrice - payment;
	}
	
	public boolean isOrderValid(int[] id, int[] quantity){
		//check product table to see if the quantity ordered is available for each product
		for(int i = 0; i < id.length; i++){
			int numAvailable = 0;
			Statement stmt = null;
		    String query = "SELECT stock_quantity " +
		                   "FROM project.product " +
		                   "WHERE id = " + id[i];
		    try {
		        stmt = con.createStatement();
		        ResultSet rs = stmt.executeQuery(query);
		        while (rs.next()) {
		        	numAvailable = rs.getInt("stock_quantity");
		        }
		        
		    } catch (SQLException e ) {
		    	e.printStackTrace();
		    } finally {
		        if (stmt != null) { 
		        	try { stmt.close();} catch (SQLException e) {}
		        }
		    }
		    if((numAvailable - quantity[i]) < 0){
		    	return false;
		    }
		}
		return true;
	}
	
	public boolean showUserOrder(int[] id, int[] quantity, float[] price, float totalPrice){
		//print formatted order to customer and ask them to confirm the order or to cancel it
		System.out.println("Product ID\tQuantity\tPrice");
		for(int i = 0; i < id.length; i++){
			System.out.println(id[i] + "\t\t" + quantity[i] + "\t\t" + price[i]);
		}
		System.out.println("Total Price: $" + totalPrice);
		System.out.println("\nWould you like to continue with this order? (y/n)");
		sc.nextLine();
		char c = sc.nextLine().charAt(0);
		if(c == 'y'){
			return true;
		}
		System.out.println("Canceling Order...\n");
		return false;
	}
	
	public void updateStock(int[] id, int[] quantity){
		//reduce the stock in the products table according to how many of each were bought
		for(int i = 0; i < id.length; i++){
			Statement stmt = null;
			String update = "UPDATE project.product SET stock_quantity = stock_quantity - " + quantity[i] + " " +
					 		"WHERE id = '" + id[i] + "'";
		    try {
		        stmt = con.createStatement();
		        stmt.executeUpdate(update);
		       
		    } catch (SQLException e ) {
		    	e.printStackTrace();
		    } finally {
		        if (stmt != null) { 
		        	try { stmt.close();} catch (SQLException e) {} 
		        }
		    }
		}
	}
	
	public void insertOrder(int[] prodID, int[] quantity, float totalPrice, String paid){
		int orderID = getOrderNumber();
		//get the date for the order
		long ms = System.currentTimeMillis();
		Date d1 = new Date(ms);
		String str = d1.toString();
		String[] d = str.split(" ");
		String date = d[1] + " " + d[2] + ", " + d[5];

		Statement stmt = null;
	    String insert = "INSERT INTO project.order_ (id, total_price, date, paid, user_id) " + 
	    				"VALUES ('" + orderID + "', '" + totalPrice + "', '" + date + "', '" + paid + "', '" + getUserID() + "')";
	    try {
	        stmt = con.createStatement();
	        stmt.executeUpdate(insert);
	    } catch (SQLException e ) {
	    	e.printStackTrace();
	    } finally {
	        if (stmt != null) { 
	        	try { stmt.close();} catch (SQLException e) {} 
	        }
	    }
	    
	    for(int i = 0; i < prodID.length; i++){
	    	stmt = null;
		    insert = "INSERT INTO project.orderdata (id, product_id, quantity) " + 
		    		 "VALUES ('" + orderID + "', '" + prodID[i] + "', '" + quantity[i] + "')";
		    try {
		        stmt = con.createStatement();
		        stmt.executeUpdate(insert);
		    } catch (SQLException e ) {
		    	e.printStackTrace();
		    } finally {
		        if (stmt != null) { 
		        	try { stmt.close();} catch (SQLException e) {} 
		        }
		    }
	    }
	}
	
	public int getOrderNumber(){
		//get order number (max orderID + 1)
		Statement stmt = null;
	    String query = "SELECT MAX(id) FROM project.order_ ";
	    int id = 0;
	    try {
	        stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery(query);

	        while (rs.next()) {
	        	id = rs.getInt(1);
	        }
	    } catch (SQLException e ) {
	    	e.printStackTrace();
	    } finally {
	    	if (stmt != null) { 
	        	try { stmt.close();} catch (SQLException e) {}
	        } 
	    }
		
		return id + 1;
	}
	
	public float priceAfterDiscount(int id){
		//first get price, then calculate it after any product discounts
		Statement stmt = null;
	    String query = "SELECT product.price - ((product.price * discount.value)/100) " + 
	    			   "FROM project.product, project.discount " + 
	    			   "WHERE product.id = " + id + " AND discount.product_id = " + id;
	    float price = 0;
	    try {
	        stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery(query);

	        while (rs.next()) {
	        	//price after product discount
	        	price = rs.getFloat(1);
	        }
	    } catch (SQLException e) {
	    	e.printStackTrace();
	    } finally {
	    	if (stmt != null) { 
	        	try { stmt.close();} catch (SQLException e) {}
	        }
	    }
		//next calculate price after any category discounts
	    stmt = null;
	    query = "SELECT discount.value " +  
	    		"FROM project.product, project.discount " + 
	    		"WHERE product.id = " + id + " AND discount.product_id = product.id";
	   int catDiscount = 0;
	    try {
	        stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery(query);

	        while (rs.next()) {
	        	catDiscount = rs.getInt(1);
	        }
	    } catch (SQLException e ) {
	    	e.printStackTrace();
	    } finally {
	    	if (stmt != null) { 
	        	try { stmt.close();} catch (SQLException e) {}
	        } 
	    }
	    price = (price - ((price * catDiscount)/100));
	  //return the total price after the discounts
		return price;
	}
	
	public int getUserID(){
		int id = 0;
		Statement stmt = null;
	    String query = "SELECT id " +
	                   "FROM project.user " + 
	                   "WHERE email = '" + email + "'";
	    try {
	        stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery(query);

	        while (rs.next()) {
	        	id = rs.getInt("id");
	        }
	    } catch (SQLException e ) {
	    	e.printStackTrace();
	    } finally {
	    	if (stmt != null) { 
	        	try { stmt.close();} catch (SQLException e) {}
	        } 
	    }
	    
		return id;
	}
	
	//returns true if account was deleted
	public boolean editAccount(){
		//user can change name, address, email, password
		//account can also be deleted (return true)
		int select = 0;
		while(select != 6){
			System.out.println("============================================================================");
			System.out.println("= 1) Change name                                                           ="); 
			System.out.println("= 2) Change address                                                        =");
			System.out.println("= 3) Change email                                                          =");
			System.out.println("= 4) Change password                                                       =");
			System.out.println("= 5) Delete Account                                                        =");
			System.out.println("= 6) Exit account editor                                                   =");
			System.out.println("============================================================================");
			if(sc.hasNextInt()){
				select = sc.nextInt();
			}
			else{
				sc.nextLine();
				System.out.println("Please enter and number 1-6");
			}
			if(select == 1){
				//call method to edit "name", "address", "email", "password" one method given a string will do all
				editUserInfo("name");
			}
			else if(select == 2){
				editUserInfo("address"); 
			}
			else if(select == 3){
				editUserInfo("email");
			}
			else if(select == 4){
				editUserInfo("password");
			}
			else if(select == 5){
				System.out.println("Are you sere that you want to delete your account? y/n");
				String c = sc.next();
				if(c.equals("y")){
					//call method to delete tuple
					deleteAccount();
					return true;
				}
				else{
					System.out.println("Canceling Action...\n");
				}
			}
			else if(select == 6){
				System.out.println("Exiting..\n");
			}
			else{
				System.out.println("Invalid Selection.\n");
			}
		}
		
		return false;
	}
	
	public void deleteAccount(){
		Statement stmt = null;
	    String update = "DELETE FROM  project.user WHERE email = '" + email + "'";

	    try {
	        stmt = con.createStatement();
	        stmt.executeUpdate(update);
	        
	    } catch (SQLException e ) {
	    	e.printStackTrace();
	    } finally {
	        if (stmt != null) { 
	        	try { stmt.close();} catch (SQLException e) {} 
	        }
	    }
	}
	
	//method to edit "name", "address", "email", "password"
	public void editUserInfo(String info){
		if(info.equals("name")){
			System.out.println("Please enter the new name: ");
		}
		else if(info.equals("address")){
			System.out.println("Please enter the new address: ");
		}
		else if(info.equals("email")){
			System.out.println("Please enter the new email: ");
		}
		else{//info == "password"
			System.out.println("Please enter the new password: ");
		}
		sc.nextLine();
		String newInfo = sc.nextLine();
		
		Statement stmt = null;
	    String update = "UPDATE project.user SET " + info + " = '" + newInfo + "' " +
	    				"WHERE email = '" + email + "'";

	    try {
	        stmt = con.createStatement();
	        stmt.executeUpdate(update);
	        
	        //If email of password is changed the global copies need to be updated
		    if(info.equals("email")){
		    	email = newInfo;
		    }
		    else if(info.equals("password")){
		    	pass = newInfo;
		    }
	    } catch (SQLException e ) {
	    	e.printStackTrace();
	    } finally {
	        if (stmt != null) { 
	        	try { stmt.close();} catch (SQLException e) {} 
	        }
	    }
	}
	
	public void showCategories(){
		//do search for parent categories and put them into list
		//then print parent and print all children, until all parents are printed
		ArrayList<String> arr = new ArrayList<>();
		
		Statement stmt = null;
	    String query = "SELECT name " +
	                   "FROM project.category " + 
	                   "WHERE name = parent";
	    try {
	        stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery(query);

	        while (rs.next()) {        	
	            arr.add(rs.getString("name"));
	        }
	    } catch (SQLException e ) {
	    	e.printStackTrace();
	    } finally {
	    	if (stmt != null) { 
	        	try { stmt.close();} catch (SQLException e) {}
	        } 
	    }
	    //arr contains list of parent categories, for each print it and all its children to screen
	    for(String parent : arr){
	    	System.out.println(parent);
	    	
	    	stmt = null;
		    query = "SELECT name " +
		            "FROM project.category " + 
		            "WHERE parent = '" + parent + "' AND name <> '" + parent + "'";
		    try {
		        stmt = con.createStatement();
		        ResultSet rs = stmt.executeQuery(query);

		        while (rs.next()) {        	
		            String cat = rs.getString("name");
		            System.out.println("   " + cat);
		        }
		    } catch (SQLException e ) {
		    	e.printStackTrace();
		    } finally {
		    	if (stmt != null) { 
		        	try { stmt.close();} catch (SQLException e) {}
		        } 
		    }
	    }
	}
}
