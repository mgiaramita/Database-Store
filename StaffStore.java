import java.util.Scanner;
import java.util.ArrayList;
import java.sql.*;

public class StaffStore {
	//Email and password of the current session user
	private String email, pass;
	//Connection to the database
	private Connection con;
	private Scanner sc;
	
	public StaffStore(Connection con){
		this.con = con;
		sc = new Scanner(System.in);
	}
	
	public void start(String user_email, String user_pass){
		//Staff can add/update/delete any user/products/order/categories/discount. 
		//You should only allow the staff to delete a product if no one is interested in buying it in the past month. 
		//Staff can get the total sales in all orders for products provided by each supplier.
		//For each order, staff can get a list of products with their shelf locations for fast packaging
		//No over selling is allowed. When a product in low on stock, staff should be able to get an alert to get more from the supplier.
		System.out.println("\nStaff Store Successfully loaded!\n");
		
		email = user_email;
		pass = user_pass;
		
		int input = 0;
		while(input != 6){
			System.out.println("============================================================================");
			System.out.println("= 1) Add a user/product/category/discount.                                 =");//also add product shelf/supplier info...
			System.out.println("= 2) Update a user/product/category/discount.                              =");
			System.out.println("= 3) Delete a user/product/category/discount.                              =");
			System.out.println("= 4) Get order product locations.                                          =");
			System.out.println("= 5) View low product alerts.                                              =");//triggerS add - delete from table on update
			System.out.println("= 6) Log off.                                                              =");
			System.out.println("============================================================================");
			
			if(sc.hasNextInt()){
				input = sc.nextInt();
			}
			else{
				sc.nextLine();
				System.out.println("Please enter and number 1-6");
			}
			
			if(input == 1){
				add();
			}
			else if(input == 2){
				update();
			}
			else if(input == 3){
				delete();
			}
			else if(input == 4){
				getLocation();
			}
			else if(input == 5){
				
			}
			else if(input == 6){
				System.out.println("Logging off...\n");
			}
			else{
				System.out.println("Invalid Selection.\n");
			}
		}
	}
	
	public void add(){
		//Add a user/product/category/discount
		int input = 0;
		while(input != 5){
			System.out.println("============================================================================");
			System.out.println("= 1) Add a user                                                            ="); 
			System.out.println("= 2) Add a product                                                         =");
			System.out.println("= 3) Add a category                                                        =");
			System.out.println("= 4) Add a discount                                                        =");
			System.out.println("= 5) Exit add                                                              =");
			System.out.println("============================================================================");
			
			if(sc.hasNextInt()){
				input = sc.nextInt();
			}
			else{
				sc.nextLine();
				System.out.println("Please enter and number 1-5");
			}
			String insert = "";
			if(input == 1){//add user
				sc.nextLine();
				System.out.println("Enter user info.");
				System.out.print("Email: ");
				String em = sc.nextLine();
				System.out.print("Password: ");
				String pw = sc.nextLine();
				System.out.print("Name: ");
				String nm = sc.nextLine();
				System.out.print("Address: ");
				String ad = sc.nextLine();
				System.out.print("Is this user staff? (y/n): ");
				String is = sc.nextLine();
				insert = "INSERT INTO project.user (address, name, password, email, is_staff) " + 
	    		   		 "VALUES ('" + ad + "', '" + nm + "', '" + pw + "', '" + em + "', '" + is + "')";
				//one method that takes in string insert that contains the SQL statement
				add(insert);
			}
			else if(input == 2){//add product
				sc.nextLine();
				System.out.println("Enter product info.");
				System.out.print("Active (yes/no): ");
				String ac = sc.nextLine();
				System.out.print("Description: ");
				String de = sc.nextLine();
				System.out.print("Name: ");
				String nm = sc.nextLine();
				System.out.print("price: ");
				String pr = sc.nextLine();
				System.out.print("Stock Quantity: ");
				String sq = sc.nextLine();
				System.out.print("Category ID: ");
				String id = sc.nextLine();
				insert = "INSERT INTO project.product (active, description, name, price, stock_quantity, category_id) " + 
	    		   		 "VALUES ('" + ac + "', '" + de + "', '" + nm + "', '" + pr + "', '" + sq + "', '" + id + "')";
				add(insert);
			}
			else if(input == 3){//add category
				sc.nextLine();
				System.out.println("Enter category info.");
				System.out.print("Name: ");
				String nm = sc.nextLine();
				System.out.print("Parent: ");
				String pa = sc.nextLine();
				System.out.print("Description: ");
				String de = sc.nextLine();
				insert = "INSERT INTO project.category (name, parent, description) " + 
	    		   		 "VALUES ('" + nm + "', '" + pa + "', '" + de + "')";
				add(insert);
			}
			else if(input == 4){//add discount
				sc.nextLine();
				System.out.println("Enter discount info.");
				System.out.print("Value (%): ");
				String va = sc.nextLine();
				System.out.print("Name: ");
				String nm = sc.nextLine();
				System.out.print("Product ID (enter null if category discount): ");
				String pi = sc.nextLine();
				System.out.print("Category ID (enter null if product discount): ");
				String ci = sc.nextLine();
				if(ci == "null"){
					insert = "INSERT INTO project.category (value, name, product_id) " + 
		    		   		 "VALUES ('" + va + "', '" + nm + "', '" + pi + "')";
				}
				else{
					insert = "INSERT INTO project.category (value, name, category_id) " + 
		    		   		 "VALUES ('" + va + "', '" + nm + "', '" + ci + "')";
				}
				add(insert);
			}
			else if(input == 5){//exit
				System.out.println("Returning...");
			}
			else{
				System.out.println("Invalid Selection.");
			}
		}
	}
	
	public void add(String insert){
		Statement stmt = null;

	    try {
	        stmt = con.createStatement();
	        stmt.executeUpdate(insert);
	        //if successful no error is thrown
	    } catch (SQLException e ) {
	    	e.printStackTrace();
	    } finally {
	        if (stmt != null) { 
	        	try { stmt.close();} catch (SQLException e) {} 
	        }
	    }
	}
	
	public void delete(){
		//Delete a user/product/category/discount
		int input = 0;
		while(input != 5){
			System.out.println("============================================================================");
			System.out.println("= 1) Delete a user                                                         ="); 
			System.out.println("= 2) Delete a product                                                      =");
			System.out.println("= 3) Delete a category                                                     =");
			System.out.println("= 4) Delete a discount                                                     =");
			System.out.println("= 5) Exit Delete                                                           =");
			System.out.println("============================================================================");
			
			if(sc.hasNextInt()){
				input = sc.nextInt();
			}
			else{
				sc.nextLine();
				System.out.println("Please enter and number 1-5");
			}
			if(input == 1){//delete user
				//call one delete function that takes in the table name and the tuple id to delete
				System.out.print("Enter id of the user to be deleted: ");
				int id = sc.nextInt();
				delete("user", id);
			}
			else if(input == 2){//delete product
				System.out.print("Enter id of the product to be deleted: ");
				int id = sc.nextInt();
				delete("product", id);
			}
			else if(input == 3){//delete category
				System.out.print("Enter id of the category to be deleted: ");
				int id = sc.nextInt();
				delete("category", id);
			}
			else if(input == 4){//delete discount
				System.out.print("Enter id of the discount to be deleted: ");
				int id = sc.nextInt();
				delete("discount", id);
			}
			else if(input == 5){//exit
				System.out.println("Returning...");
			}
			else{
				System.out.println("Invalid Selection.");
			}
		}
	}
	
	public void delete(String table, int id){
		//delete function that takes in the table name and the tuple id to delete
		Statement stmt = null;
	    String update = "DELETE FROM  project." + table + " WHERE id = '" + id + "'";

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
	
	public void getLocation(){
		System.out.print("Enter the order ID that you want the shelf loactions for: ");
		int id = sc.nextInt();
		//get the product IDs of the order
		ArrayList<Integer> intArr = new ArrayList<>();
		Statement stmt = null;
	    String query = "SELECT product_id " +
	                   "FROM project.orderdata " +
	                   "WHERE id = '" + id + "'";
	    try {
	        stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery(query);
	        while (rs.next()) {
	        	intArr.add(rs.getInt("product_id"));
	        }
	        
	    } catch (SQLException e ) {
	    	e.printStackTrace();
	    } finally {
	        if (stmt != null) { 
	        	try { stmt.close();} catch (SQLException e) {}
	        }
	    }
	    //now get the locations of those products and the names of the products
	    System.out.println("Location\tProduct Name");
	    for(int pid : intArr){
	    	stmt = null;
		    query = "SELECT shelf.location, product.name " +
		            "FROM project.shelf, project.product " +
		            "WHERE shelf.product_id = '" + pid + "' AND product.id = '" + pid + "'";
		    try {
		        stmt = con.createStatement();
		        ResultSet rs = stmt.executeQuery(query);
		        while (rs.next()) {
		        	System.out.print(rs.getString("shelf.location") + "\t\t");
		        	System.out.println(rs.getString("product.name"));
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
	
	public void update(){
		int input = 0;
		while(input != 5){
			System.out.println("============================================================================");
			System.out.println("= 1) Update a user                                                         ="); 
			System.out.println("= 2) Update a product                                                      =");
			System.out.println("= 3) Update a category                                                     =");
			System.out.println("= 4) Update a discount                                                     =");
			System.out.println("= 5) Exit Update                                                           =");
			System.out.println("============================================================================");
			
			if(sc.hasNextInt()){
				input = sc.nextInt();
			}
			else{
				sc.nextLine();
				System.out.println("Please enter and number 1-5");
			}
			String attribute = "";
			String update = "";
			String newInfo = "";
			if(input == 1){//Update user
				//ask for the attribute name and the value to create the update string, then call single update method
				System.out.println("Enter the user ID you want to update.");
				int id = sc.nextInt();
				sc.nextLine();
				//loop until a correct attribute name has been given
				boolean loop = true;
				while(loop){
					System.out.println("What do you want to update: 'address', 'name', 'password', 'email', 'is_staff'");
					attribute = sc.nextLine();
					if(attribute.equals("name") || attribute.equals("address") || attribute.equals("password") || attribute.equals("email") || attribute.equals("is_staff")){
						loop = false;
					}
				}
				System.out.println("Enter the value you want to change the attribute to.");
				newInfo = sc.nextLine();
				update = "UPDATE project.user SET " + attribute + " = '" + newInfo + "' " +
	    				 "WHERE id = '" + id + "'";
				update(update);
			}
			else if(input == 2){//Update product
				System.out.println("Enter the product ID you want to update.");
				int id = sc.nextInt();
				sc.nextLine();
				//loop until a correct attribute name has been given
				boolean loop = true;
				while(loop){
					System.out.println("What do you want to update: 'active', 'description', 'name', 'price', 'stock_quantity', 'category_id'");
					attribute = sc.nextLine();
					if(attribute.equals("active") || attribute.equals("description") || attribute.equals("name") || attribute.equals("price") || attribute.equals("stock_quantity") || attribute.equals("category_id")){
						loop = false;
					}
				}
				System.out.println("Enter the value you want to change the attribute to.");
				newInfo = sc.nextLine();
				update = "UPDATE project.product SET " + attribute + " = '" + newInfo + "' " +
	    				 "WHERE id = '" + id + "'";
				update(update);
			}
			else if(input == 3){//Update category
				System.out.println("Enter the category ID you want to update.");
				int id = sc.nextInt();
				sc.nextLine();
				//loop until a correct attribute name has been given
				boolean loop = true;
				while(loop){
					System.out.println("What do you want to update: 'name', 'parent', 'description'");
					attribute = sc.nextLine();
					if(attribute.equals("name") || attribute.equals("parent") || attribute.equals("description")){
						loop = false;
					}
				}
				System.out.println("Enter the value you want to change the attribute to.");
				newInfo = sc.nextLine();
				update = "UPDATE project.category SET " + attribute + " = '" + newInfo + "' " +
	    				 "WHERE id = '" + id + "'";
				update(update);
			}
			else if(input == 4){//Update discount
				System.out.println("Enter the discount ID you want to update.");
				int id = sc.nextInt();
				sc.nextLine();
				//loop until a correct attribute name has been given
				boolean loop = true;
				while(loop){
					System.out.println("What do you want to update: 'value', 'name', 'product_id', 'category_id'");
					attribute = sc.nextLine();
					if(attribute.equals("name") || attribute.equals("value") || attribute.equals("product_id") || attribute.equals("category_id")){
						loop = false;
					}
				}
				System.out.println("Enter the value you want to change the attribute to.");
				newInfo = sc.nextLine();
				update = "UPDATE project.discount SET " + attribute + " = '" + newInfo + "' " +
	    				 "WHERE id = '" + id + "'";
				update(update);
			}
			else if(input == 5){//exit
				System.out.println("Returning...");
			}
			else{
				System.out.println("Invalid Selection.");
			}
		}
	}
	
	public void update(String update){
		Statement stmt = null;
	    
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
