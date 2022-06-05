package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
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
@MultipartConfig
public class GetImagesByAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public GetImagesByAlbum() {
		super();
	}
	
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
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		// thymeleaf
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		String path = "/WEB-INF/album_images.html";
		String errorpath = "/WEB-INF/error.html";
		
		Integer IdAlbum, IdImg;
		int pageSize = 5;
		
		try {
			IdAlbum = Integer.parseInt(request.getParameter("album"));
			if((StringEscapeUtils.escapeJava(request.getParameter("image")))!=null)
				IdImg = Integer.parseInt(request.getParameter("image"));
			else
				IdImg = null;
				
		}
		catch (Exception e) {
			ctx.setVariable("errorMsg","Incorrect or missing param values");
			templateEngine.process(errorpath, ctx, response.getWriter());
			return;
		}
		
		AlbumDAO aDAO = new AlbumDAO(connection);
		ImageDAO iDao = new ImageDAO(connection);
		
		
		Album album = null;
		List<Image> listImages = null;
		
		try {
			album = aDAO.getAlbumByID(IdAlbum);
			listImages = iDao.getImagesFromAlbum(IdAlbum);
		} 
		catch (SQLException e) {
			ctx.setVariable("errorMsg","Not possible to recover album");
			templateEngine.process(errorpath, ctx, response.getWriter());
			return;
		}
		
		if(album == null) { // invalid album id
			ctx.setVariable("errorMsg","Invalid id for album");
			templateEngine.process(errorpath, ctx, response.getWriter());
			return;
		}
		
		List<Image> pageImages = null;
		Image prevImage = null;
		Image nextImage = null;	
		Image activeImage = null;
		List<Comment> comments = null;
		
		if(listImages.size()>0) {
			if(IdImg!=null){
				// search idImg in listImages
				activeImage = listImages.stream()
						.filter(image -> (image.getId() == IdImg))
						.findFirst().orElse(null);
				if(activeImage == null) {
					ctx.setVariable("errorMsg","The selected image is not in this album");
					templateEngine.process(errorpath, ctx, response.getWriter());
					return;
				}
			}else
				activeImage = listImages.get(0);
			
			//Get comment of active image
			CommentDAO cDAO = new CommentDAO(connection);
			try {
				comments = cDAO.getCommentsFromImages(activeImage.getId());
			} catch (SQLException e) {
				ctx.setVariable("errorMsg","Not possible to recover comments");
				templateEngine.process(errorpath, ctx, response.getWriter());
				return;
			}
			
			int imageIndex = listImages.indexOf(activeImage);
			
			System.out.println(imageIndex);
			
			int minCurrentIndex = (int)Math.floorDiv(imageIndex, pageSize) * pageSize; //indice prima immagine a sinistra nel menu della scelta. floorDiv return intero approssimato per difetto del risultato tra la divisione dei due terimini
			int maxCurrentIndex = (int)Math.min(listImages.size(),minCurrentIndex + pageSize);
			//System.out.println(minCurrentIndex);
			//System.out.println(maxCurrentIndex);
			pageImages = listImages.subList(minCurrentIndex, maxCurrentIndex);
			
			//Per i click di next e prev
			if(minCurrentIndex>0)
				prevImage = listImages.get(minCurrentIndex - 1);
			if(maxCurrentIndex < listImages.size())
				nextImage = listImages.get(maxCurrentIndex);

		}
		
		ctx.setVariable("album", album);
		ctx.setVariable("images", pageImages);
		ctx.setVariable("prev", prevImage);	
		ctx.setVariable("next", nextImage);			
		ctx.setVariable("active", activeImage);	
		ctx.setVariable("comments", comments);	
		templateEngine.process(path, ctx, response.getWriter());
			
	}
}
