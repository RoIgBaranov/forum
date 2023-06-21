package telran.java47.security.filter;

import java.io.IOException;
import java.security.Principal;

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
import telran.java47.post.dao.PostRepository;
import telran.java47.post.exceptions.PostNotFoundException;
import telran.java47.post.model.Post;


@Component
@Order(60)
@RequiredArgsConstructor
public class UpdatePostFilter implements Filter {
	
	final PostRepository postRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();
		if (checkEndPoint(request.getMethod(), path)) {
			Principal principal = request.getUserPrincipal();
			String[] arr = path.split("/");
			Post post = postRepository.findById(arr[arr.length-1]).orElseThrow(()-> new PostNotFoundException());
			String author = post.getAuthor();
			if(!author.equalsIgnoreCase(principal.getName())) {
				response.sendError(403);
				return;
			}
		}
		
		chain.doFilter(request, response);
	}
	
	private boolean checkEndPoint(String method, String path) {
		return "PUT".equalsIgnoreCase(method) && path.matches("/forum/post/\\w+/?");
	}

}