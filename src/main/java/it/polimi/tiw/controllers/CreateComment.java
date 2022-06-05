package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Image;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AlbumImagesDAO;
import it.polimi.tiw.dao.CommentDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/CreateComment")
@MultipartConfig
public class CreateComment extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;

	public CreateComment() {
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
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		// thymeleaf
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		String errorpath = "/WEB-INF/error.html";
		
		HttpSession session = request.getSession();
		
		Integer imageId = null;
		Integer albumId = null;
		String text = null;
		
		User usr = (User) session.getAttribute("user");	
		Integer usrID = usr.getId();
		
		try {
			imageId = Integer.parseInt(request.getParameter("image")); 
			albumId = Integer.parseInt(request.getParameter("album"));
			text = StringEscapeUtils.escapeJava(request.getParameter("text"));
			
			if(text.equals("") || text==null || albumId == null || imageId == null) {
				ctx.setVariable("errorMsg", "Your comment cannot be empty");
				templateEngine.process(errorpath, ctx, response.getWriter());
				return;
			}
		}
		catch (Exception e) {
			ctx.setVariable("errorMsg", "Incorrect or missing param values");
			templateEngine.process(errorpath, ctx, response.getWriter());
			return;
		}
			
		ImageDAO iDao = new ImageDAO(connection);
		Image img;
		AlbumImagesDAO IaDao = new AlbumImagesDAO(connection);
		CommentDAO cDao = new CommentDAO(connection);
		
		try {
			img = iDao.getImageByID(imageId);
			if(img == null) {
				ctx.setVariable("errorMsg", "Missing image param values");
				templateEngine.process(errorpath, ctx, response.getWriter());
				return;
			}
			if(!IaDao.checkImageInAlbum(imageId, albumId)) {
				ctx.setVariable("errorMsg",  "Mismatching value from album and image, cannot return to album page");
				templateEngine.process(errorpath, ctx, response.getWriter());
				return;
			}
			cDao.createComment(imageId, text , usrID);
		}
		catch (SQLException e) {
			ctx.setVariable("errorMsg","Not possible to create comment");
			templateEngine.process(errorpath, ctx, response.getWriter());
			return;
		}
		
		response.sendRedirect(getServletContext().getContextPath() + "/Album?album=" + albumId + "&image=" + imageId);
	}

}
	
