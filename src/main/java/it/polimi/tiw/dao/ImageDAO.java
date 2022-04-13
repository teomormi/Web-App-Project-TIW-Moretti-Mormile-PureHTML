package it.polimi.tiw.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polimi.tiw.beans.Image;

public class ImageDAO {
	private Connection connection;
	
	public ImageDAO(Connection connection) {
		this.connection = connection;
	}
	
	public ArrayList<Image> getImagesFromAlbum(int idAlbum) throws SQLException{
		ArrayList<Image> images = new ArrayList<Image>();
		String query = "SELECT * FROM image WHERE idAlbum = ? ORDER BY date DESC";
		try(PreparedStatement pstatement = connection.prepareStatement(query);){
			pstatement.setInt(1, idAlbum);
			try(ResultSet result = pstatement.executeQuery()){
				while(result.next()) {
					Image img = new Image();
					img.setId(result.getInt("imageID"));
					img.setDescription(result.getString("description"));
					img.setPath(result.getString("path"));
					img.setTitle(result.getString("title"));
					img.setAlbumId(result.getInt("album"));
					img.setDate(result.getDate("date"));
				}
			}
		}
		return images;
	}
	
	public Image getImageByID(int id) throws SQLException{
		Image img = new Image();
		String query = "SELECT * FROM image WHERE imageID = ?";
		try(PreparedStatement pstatement = connection.prepareStatement(query);){
			pstatement.setInt(1, id);
			try(ResultSet result = pstatement.executeQuery()){
				if(!result.isBeforeFirst())
					return null;
				img.setId(result.getInt("imageID"));
				img.setDescription(result.getString("description"));
				img.setPath(result.getString("path"));
				img.setTitle(result.getString("title"));
				img.setAlbumId(result.getInt("album"));
				img.setDate(result.getDate("date"));
			}
		}
		return img;
	}
}
