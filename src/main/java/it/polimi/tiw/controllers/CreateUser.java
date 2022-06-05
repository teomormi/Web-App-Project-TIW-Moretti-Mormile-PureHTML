package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/CreateUser")
@MultipartConfig
public class CreateUser extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;

	public CreateUser() {
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
	
	boolean isStringValid(String s) {
		if(s==null || s.equals(""))
			return false;
		return true;
	}
	
	                                                                                                                                                                                                                                                                                                                                                                                                                                                        
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	 
		// thymeleaf
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		String path = "/index.html";
		
		Pattern pattern = Pattern.compile("^[a-zA-Z0-9._-]+@[a-zA-Z0-9_-]+.[a-zA-Z]{2,4}$");
		Matcher matcher;
		String username = null;
		String email = null;
		String password = null;
		String passConfirm = null;
		
		try {
			username = StringEscapeUtils.escapeJava(request.getParameter("username"));
			email = StringEscapeUtils.escapeJava(request.getParameter("email"));
			password = StringEscapeUtils.escapeJava(request.getParameter("password"));
			passConfirm = StringEscapeUtils.escapeJava(request.getParameter("passconfirm"));
			
			if(!isStringValid(username)) {
				ctx.setVariable("errorMsg_signup", "Username cannot be empty");
				templateEngine.process(path, ctx, response.getWriter());
				return;
			}
			if(!isStringValid(email)) {
				ctx.setVariable("errorMsg_signup","Email cannot be empty");
				templateEngine.process(path, ctx, response.getWriter());
				return;
			}
			
			matcher = pattern.matcher(email);
			if(!matcher.matches()) {
				ctx.setVariable("errorMsg_signup", "Bad email format");
				templateEngine.process(path, ctx, response.getWriter());
				return;				
			}
			
			if(!isStringValid(password)) {
				ctx.setVariable("errorMsg_signup", "Password cannot be empty");
				templateEngine.process(path, ctx, response.getWriter());
				return;
			}
			if(!password.equals(passConfirm)) {
				ctx.setVariable("errorMsg_signup", "Passwords don't match");
				templateEngine.process(path, ctx, response.getWriter());
				return;
			}
			
		}
		catch (Exception e) {
			ctx.setVariable("errorMsg_signup", "Incorrect or missing param values");
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		UserDAO uDAO = new UserDAO(connection);
		int uID;
		
		try {
			if(!uDAO.isMailAvailable(email)) {
				ctx.setVariable("errorMsg_signup", "Email isn't available");
				templateEngine.process(path, ctx, response.getWriter());
				return;
			}
			if(!uDAO.isUsernameAvailable(username)) {
				ctx.setVariable("errorMsg_signup", "Username isn't available");
				templateEngine.process(path, ctx, response.getWriter());
				return;
			}
			
			uID = uDAO.registerUser(username, email, password);
			
		} catch (SQLException e) {
			ctx.setVariable("errorMsg_signup", "Not possible to register user");
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		User usr = new User();
		usr.setId(uID);
		usr.setUsername(username);
		request.getSession(true).setAttribute("user", usr);
		
		String path_redirect = getServletContext().getContextPath()+"/GoToHome";
		response.sendRedirect(path_redirect);
	}
	
}
