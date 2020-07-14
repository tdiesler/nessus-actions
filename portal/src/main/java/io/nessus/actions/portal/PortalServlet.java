package io.nessus.actions.portal;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nessus.actions.model.Model;

@SuppressWarnings("serial")
@WebServlet(value = "/action/*")
public class PortalServlet extends HttpServlet {

	static final Logger LOG = LoggerFactory.getLogger(PortalServlet.class);

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

		String pathInfo = req.getPathInfo();
		LOG.warn("pathInfo: {}", pathInfo);

		if (pathInfo.startsWith("/yaml-submit")) {

			String content = req.getParameter("content").replace("\t", "   ");
			LOG.warn("YAML Content ------------ \n{}", content);
			
			Model model = Model.read(content);
			HttpSession session = req.getSession();
			session.setAttribute(Model.class.getName(), model);

			res.sendRedirect("../step2-eap.html");
		}
		else if (pathInfo.startsWith("/step2-eap")) {

			String addr = req.getParameter("addr");
			String username = req.getParameter("username");
			//String password = req.getParameter("password");
			LOG.warn("Address: {}", addr);
			LOG.warn("Username: {}", username);
			
			res.sendRedirect("../success.html");
		}
	}
}
