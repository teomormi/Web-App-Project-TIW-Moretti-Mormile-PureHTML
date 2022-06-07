package it.polimi.tiw.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;


/**
 * Servlet implementation class GetFile
 */
@WebServlet("/GetImage/*")
public class GetImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String folderPath = "";
	private TemplateEngine templateEngine;

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		folderPath = getServletContext().getInitParameter("outputpath");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// thymeleaf
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		String errorpath =  "/WEB-INF/error.html";
		
		String pathInfo = request.getPathInfo();
		// PathInfo: The part of the request path that is not part of the Context Path or the Servlet Path.

		if (pathInfo == null || pathInfo.equals("/")) {
			ctx.setVariable("errorMsg", "Missing file name!");
			templateEngine.process(errorpath, ctx, response.getWriter());
			return;
		}

		// substring(1) useful to remove first "/" in path info because it is not part of the filename
		String filename = URLDecoder.decode(pathInfo.substring(1), "UTF-8");

		File file = new File(folderPath, filename); //folderPath inizialized in init
		System.out.println(filename);

		if (!file.exists() || file.isDirectory()) {
			ctx.setVariable("errorMsg", "File not found");
			templateEngine.process(errorpath, ctx, response.getWriter());
			return;
		}

		// set headers for browser
		response.setHeader("Content-Type", getServletContext().getMimeType(filename));
		response.setHeader("Content-Length", String.valueOf(file.length()));
		
		//TODO: test what happens  if you change inline by  attachment
		response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
																							
		// copy file to output stream
		Files.copy(file.toPath(), response.getOutputStream());
	}
}
