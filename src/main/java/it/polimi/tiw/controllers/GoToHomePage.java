package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/GoToHome")
@MultipartConfig
public class GoToHomePage extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public GoToHomePage() {
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
		String path = "/WEB-INF/home.html";
		String errorpath = "/WEB-INF/error.html";
		
		HttpSession session = request.getSession();
		Integer idUser = null;
		
		
		AlbumDAO aDao = new AlbumDAO(connection);
		ArrayList<Album> albumsOther = null;
		ArrayList<Album> albumsUser = null;
		
		User usr = (User) session.getAttribute("user");
		idUser = usr.getId();
		
		try {
			albumsOther = aDao.getAlbumsByNotUserID(idUser);
			albumsUser = aDao.getAlbumsByUserID(idUser);
		}
		catch (SQLException e) {
			ctx.setVariable("errorMsg","Not possible to recover albums");
			templateEngine.process(errorpath, ctx, response.getWriter());
			return;
		}
		
		
		ctx.setVariable("albumsOther", albumsOther);
		ctx.setVariable("albumsUser", albumsUser);
		
		templateEngine.process(path, ctx, response.getWriter());
	}
}
