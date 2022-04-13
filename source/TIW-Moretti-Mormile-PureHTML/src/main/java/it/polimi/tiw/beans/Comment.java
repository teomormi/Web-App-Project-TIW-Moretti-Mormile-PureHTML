package it.polimi.tiw.beans;

public class Comment {
	private int id;
	private String text;
	private int userId;
	private int imageId;
	
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }
	
	public int getUserId() { return userId; }
	public void setUserId(int userId) { this.userId = userId; }

	public String getText() { return text; }
	public void setText(String text) { this.text = text; }
	
	public int getImageId() { return imageId; }
	public void setImageId(int image) { this.imageId = image; }
}
