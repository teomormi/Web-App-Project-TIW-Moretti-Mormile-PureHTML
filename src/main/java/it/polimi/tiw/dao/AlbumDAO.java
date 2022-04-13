package it.polimi.tiw.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.polimi.tiw.beans.Album;


public class AlbumDAO {
	private Connection connection;
	
	public AlbumDAO(Connection connection) {
		this.connection=connection;
	}
	
	public ArrayList<Album> getAlbums() throws SQLException{
		ArrayList<Album> albums = new ArrayList<Album>();
		String query = "SELECT * FROM album ORDER BY date DESC";
		try(PreparedStatement pstatement = connection.prepareStatement(query)) {
			try(ResultSet result = pstatement.executeQuery()) {
				while(result.next()) {
					Album album = new Album();
					album.setId(result.getInt("albumID"));
					album.setTitle(result.getString("title"));
					album.setDate(result.getDate("date"));
					albums.add(album);
				}
			}
		}
		return albums;
	}
	
	public Album getAlbumByID(int id) throws SQLException{
		String query = "SELECT * FROM album ORDER BY date DESC";
		Album album = new Album();
		try(PreparedStatement pstatement = connection.prepareStatement(query)) {
			try(ResultSet result = pstatement.executeQuery()) {
				if(!result.isBeforeFirst())
					return null;
				result.next();
				album.setId(result.getInt("albumID"));
				album.setTitle(result.getString("title"));
				album.setDate(result.getDate("date"));
				
			}
		}
		return album;
	}
}
