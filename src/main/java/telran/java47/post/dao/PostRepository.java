package telran.java47.post.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import telran.java47.post.model.Post;

public interface PostRepository extends MongoRepository<Post, String> {
	
	Stream<Post> findPostsByAuthorIgnoreCase(String author);
	
	Stream<Post> findByTagsIn(List<String> tags);
	
	Stream<Post> findByDateCreatedBetween(LocalDate dateFrom, LocalDate dateTo);

}
