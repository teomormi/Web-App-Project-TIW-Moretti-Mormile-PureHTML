package it.polimi.tiw.controllers;
// TODO after the home page

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.CommentDAO;
import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Comment;
import it.polimi.tiw.beans.Image;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/Album")
public class GetImagesByAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	private int pageSize = 5;
	
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
	
	public void destroy() {		
		try {
			ConnectionHandler.closeConnection(connection);
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	Image getActiveImage(int id, ArrayList<Image> arrayImgs) {
		for(Image i : arrayImgs)
			if(i.getId() == id)
				return i;
		return null;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
	
		ImageDAO iDao = new ImageDAO(connection);
		Integer IdAlbum, IdImg;
		int pageSize = 5;
		
		try {
			IdAlbum = Integer.parseInt(request.getParameter("album"));
			if((StringEscapeUtils.escapeJava(request.getParameter("image")))!=null)
				IdImg = Integer.parseInt(request.getParameter("image"));
			else
				IdImg = null;
				
		}
		catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}
		
		AlbumDAO aDAO = new AlbumDAO(connection);
		ImageDAO iDAO = new ImageDAO(connection);
		
		Album album = null;
		ArrayList<Image> images = null;
		
		try {
			album = aDAO.getAlbumByID(IdAlbum);
			images = iDao.getImagesFromAlbum(IdAlbum);
		} 
		catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover album");
			return;
		}
		
		ArrayList<Image> pageImages = null;
		Image prevImage = null;
		Image nextImage = null;	
		Image activeImage = null;
		ArrayList<Comment> comments = null;
		
		if(images.size()>0) {
			
			//Chose the active image
			
			if(IdImg!=null)
				activeImage = getActiveImage(IdImg, images);
			else
				activeImage = images.get(0);
			
			//Get comment of active image
			
			CommentDAO cDAO = new CommentDAO(connection);
			try {
				comments = cDAO.getCommentsFromImages(activeImage.getId());
			} catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover comments");
				return;
			}
			
			int imageIndex = images.indexOf(activeImage);
			int minCurrentIndex = (int)Math.floorDiv(imageIndex, pageSize) * pageSize; //indice prima immagine a sinistra nel menu della scelta. floorDiv return intero approssimato per difetto del risultato tra la divisione dei due terimini
			int maxCurrentIndex = (int)Math.min(images.size(),minCurrentIndex + pageSize);
			pageImages = (ArrayList<Image>) images.subList(minCurrentIndex, maxCurrentIndex);
			
			//Per i click di next e prev
			if(minCurrentIndex>0)
				prevImage = images.get(imageIndex-pageSize);
			if(maxCurrentIndex>images.size())
				nextImage = images.get(Math.min(images.size()-1,imageIndex+pageSize));
			
		}
		
		String path = "/WEB-INF/album.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("album", album);
		ctx.setVariable("images", pageImages);
		ctx.setVariable("prev", prevImage);	
		ctx.setVariable("next", nextImage);			
		ctx.setVariable("active", activeImage);	
		ctx.setVariable("comments", comments);	
		templateEngine.process(path, ctx, response.getWriter());
		
		
		
	}
}
