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

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/CreateAlbum")
@MultipartConfig
public class CreateAlbum extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
	
	public CreateAlbum() {
		super();
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
		
		Integer idUser = null;
		String title = null;
		
		User usr = (User) session.getAttribute("user");
		idUser = usr.getId();
		
		try {
			title = StringEscapeUtils.escapeJava(request.getParameter("title"));
			if(title.equals("") || title==null) {
				ctx.setVariable("errorMsg","Your album title cannot be empty");
				templateEngine.process(errorpath, ctx, response.getWriter());
				return;
			}
		}
		catch (Exception e) {
			ctx.setVariable("errorMsg","Incorrect or missing param values");
			templateEngine.process(errorpath, ctx, response.getWriter());
			return;
		}
		
		AlbumDAO aDao = new AlbumDAO(connection);
				
		try {
			aDao.createAlbum(title,idUser);
		}
		catch (SQLException e) {
			ctx.setVariable("errorMsg", "Not possible to create album");
			templateEngine.process(errorpath, ctx, response.getWriter());
			return;
		}
		
		response.sendRedirect(getServletContext().getContextPath() + "/GoToHome");
			
	}

}
