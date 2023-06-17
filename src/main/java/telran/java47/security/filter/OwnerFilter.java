package telran.java47.security.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import telran.java47.accounting.dao.UserAccountRepository;
import telran.java47.accounting.model.UserAccount;

@RequiredArgsConstructor
@Component
@Order(30)
public class OwnerFilter implements Filter {
	
	final UserAccountRepository userAccountRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		if (checkEndPoint(request.getMethod(), request.getServletPath()) 
				&& !request.getServletPath().substring(14).equalsIgnoreCase(request.getUserPrincipal().getName())) {
			response.sendError(403, "Changing is forbidden. You are not owner");
			return;
		}
		if(checkEndPointDelete(request.getServletPath(),request.getMethod())) {
			UserAccount userAccount = userAccountRepository.findById(request.getUserPrincipal().getName()).orElse(null);
			if(!request.getServletPath().substring(14).equalsIgnoreCase(request.getUserPrincipal().getName())
					&& !userAccount.getRoles().contains("ADMIN")) {
				response.sendError(403, "Changing is forbidden. You are not owner");
				return;
			}
		}
		
		chain.doFilter(request, response);

	}

	private boolean checkEndPointDelete(String path, String method) {
		return "DELETE".equalsIgnoreCase(method) && path.matches("/account/user/[^/]+");
	}

	private boolean checkEndPoint(String method, String path) {
		return ("PUT".equalsIgnoreCase(method)&& path.matches("/account/user/[^/]+"));
	}

}
