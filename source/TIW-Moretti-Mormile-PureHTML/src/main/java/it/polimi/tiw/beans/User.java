package it.polimi.tiw.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class User {
	private int id;
	private String username;
	private String email;
	private String password;
	private List<Integer> albums_order;
	
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	
	public List<Integer> getOrder() { return albums_order; }
	public void setOrder(List<Integer> order) { this.albums_order = order; }
	
	public String getOrderCSV() {
		return this.albums_order.stream().map(num -> num.toString()).collect(Collectors.joining(","));
	}
	public void setOrder(String orderCSV) {
        List<Integer> orderList = new ArrayList<>();
        Integer albumId = null;
        for(String albumString : orderCSV.split(",")) { // split return an array of string
            try {
        		albumId = Integer.parseInt(albumString);
        		orderList.add(albumId);
        	} catch (NumberFormatException e) {
        		// should notify the parsing error
         	}
        }
        this.albums_order = orderList;
	}
} 
