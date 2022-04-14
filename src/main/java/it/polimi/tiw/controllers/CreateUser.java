package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.exceptions.BadUserException;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/CreateUser")
@MultipartConfig
public class CreateUser extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
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
	
	                                                                                                                                                                                                                                                                                                                                                                                                                                                        
		
		//check regex
		Pattern pattern = Pattern.compile("^[a-zA-Z0-9._-]+@[a-zA-Z0-9_-]+.[a-zA-Z]{2,4}$");
		Matcher matcher;
		String nickname = null;
		String email = null;
		String password = null;
		String passConfirm = null;
		try {
			nickname = StringEscapeUtils.escapeJava(request.getParameter("nickname"));
			email = StringEscapeUtils.escapeJava(request.getParameter("email"));
			password = StringEscapeUtils.escapeJava(request.getParameter("password"));
			passConfirm = StringEscapeUtils.escapeJava(request.getParameter("passconfirm"));
			
			if(!isStringValid(nickname)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Nickname cannot be empty");
				return;
			}
			if(!isStringValid(email)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Email cannot be empty");
				return;
			}
			if(!isStringValid(password)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Password cannot be empty");
				return;
			}
			if(!password.equals(passConfirm)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Passwords don't match");
				return;
			}
		}
		catch (NumberFormatException | NullPointerException e) {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.getWriter().println("Incorrect or missing param values");
		return;
		}
		
		UserDAO uDAO = new UserDAO(connection);
		try {
			if(!uDAO.isMailAvailable(email)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("email isn't available");
				return;
			}
			if(!uDAO.isNicknameAvailable(nickname)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("email isn't available");
				return;
			}
			uDAO.registerUser(nickname, email, password, passConfirm);
			
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to register user");
			return;
		} catch (BadUserException e ) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect param values");
			return;
		}
		//utente in sessione e vado in checkLogin
		request.getSession().setAttribute("user", uDAO);
		String path = getServletContext().getContextPath()+"/CheckLogin";
		response.sendRedirect(path);
	}
	
}
