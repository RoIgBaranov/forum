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


@Component
@RequiredArgsConstructor
@Order(20)
public class AdminFilter implements Filter {
	
	final UserAccountRepository userAccountRepository;
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		if (checkEndPointByPath(request.getServletPath())) {
			UserAccount userAccount = userAccountRepository.findById(request.getUserPrincipal().getName()).orElse(null);
			if(!userAccount.getRoles().contains("ADMIN")) {
				response.sendError(403, "Changing is forbidden. Check your rights");
				return;
			}
		}
		if(checkEndPointByMethod(request.getMethod())) {
			
		}
		chain.doFilter(request, response);
		
	}

	private boolean checkEndPointByMethod(String method) {
		return "DELETE".equalsIgnoreCase(method) ;
	}

	private boolean checkEndPointByPath(String servletPath) {
		return servletPath.matches("/account/user/[^/]+/role/[^/]+");
	}

}
