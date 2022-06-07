package it.polimi.tiw.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AlbumImagesDAO;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.InputValidator;

@WebServlet("/CreateImage")
@MultipartConfig
public class CreateImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	String folderPath = "";
	private TemplateEngine templateEngine;

	public CreateImage() {
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
		folderPath = getServletContext().getInitParameter("outputpath");
	}
	
	public void destroy() {		
		try {
			ConnectionHandler.closeConnection(connection);
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// thymeleaf
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		String errorpath = "/WEB-INF/error.html";
		
		HttpSession session = request.getSession();
		
		Integer idUser = null;
		String title = null;
		String description = null;
		String[] checkedIds = null;
		List<Integer> listIds = new ArrayList<Integer>();
		
		Part filePart = request.getPart("file"); 
		User usr = (User) session.getAttribute("user");
		idUser = usr.getId();
		
		try {
			title = StringEscapeUtils.escapeJava(request.getParameter("title"));
			description = StringEscapeUtils.escapeJava(request.getParameter("description"));
			checkedIds = request.getParameterValues("albums");
			
			if(!InputValidator.isStringValid(title))  {
				ctx.setVariable("errorMsg", "Your title cannot be empty");
				templateEngine.process(errorpath, ctx, response.getWriter());
				return;
			}
			if(!InputValidator.isStringValid(description))  {
				ctx.setVariable("errorMsg", "Your description cannot be empty");
				templateEngine.process(errorpath, ctx, response.getWriter());
				return;
			}
			if (checkedIds == null) {
				ctx.setVariable("errorMsg", "Image should have an album");
				templateEngine.process(errorpath, ctx, response.getWriter());
				return;
			}
			
			// Check that the ids sent are valid == correspond to albums of actual user
			AlbumDAO aDao = new AlbumDAO(connection);
			
			for (String s : checkedIds) {
				Integer id = Integer.parseInt(s);
				if(aDao.getAlbumByID(id).getUserId()!=idUser) {
					ctx.setVariable("errorMsg", "Violated access to album!");
					templateEngine.process(errorpath, ctx, response.getWriter());
					return;
				}
				listIds.add(id);
			}
			if (listIds.size() == 0) {
				ctx.setVariable("errorMsg","At least one album must be selected");
				templateEngine.process(errorpath, ctx, response.getWriter());
				return;
			}
			
			//check the file			
			if (filePart == null || filePart.getSize() <= 0) {
				ctx.setVariable("errorMsg","Missing file in request!");
				templateEngine.process(errorpath, ctx, response.getWriter());
				return;
			}
			
			// Then we check the parameter is valid (in this case right format img)
			String contentType = filePart.getContentType();
			
			if (!contentType.startsWith("image")) {
				ctx.setVariable("errorMsg","File format not permitted");
				templateEngine.process(errorpath, ctx, response.getWriter());
				return;
			}
		}
		catch (Exception e) {
			ctx.setVariable("errorMsg", "Incorrect or missing param values");
			templateEngine.process(errorpath, ctx, response.getWriter());
			return;
		}
		
		
		String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
		// folderPath is saved in web.xml
		
		File folder = new File(folderPath);
		if (!folder.exists()) {
			System.out.println("Create folder for images");
			folder.mkdirs();
		}
		
		String outputPath = folderPath + File.separator + fileName;
		System.out.println("Output path: " + outputPath);
		

		File file = new File(outputPath);
		ImageDAO iDao = new ImageDAO(connection);
		AlbumImagesDAO aiDao = new AlbumImagesDAO(connection);
		Integer newImgId = null;

		try{
			// check if file with same path already exist
			if(file.exists()) {
				ctx.setVariable("errorMsg", "File name already exist");
				templateEngine.process(errorpath, ctx, response.getWriter());
				return;
			}
			
			InputStream fileContent = filePart.getInputStream(); // get file content
			Files.copy(fileContent, file.toPath()); // copy file
			
			// create image and referenced to album
			connection.setAutoCommit(false);
			newImgId = iDao.createImage(fileName, description, title, idUser.intValue());
			
			for(Integer id : listIds) {
				aiDao.addImageToAlbum(newImgId, id);
			}
			connection.commit();
			
			System.out.println("File saved correctly!");
			response.sendRedirect(getServletContext().getContextPath() + "/GoToHome");
			
		} catch (Exception e) {
			try {
					connection.rollback();
			} catch (SQLException errorSQL) { errorSQL.printStackTrace();}
			ctx.setVariable("errorMsg", "Error while saving file");
			templateEngine.process(errorpath, ctx, response.getWriter());
		}		

	}

}

