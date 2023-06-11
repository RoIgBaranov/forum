package telran.java47.post.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java47.post.dao.PostRepository;
import telran.java47.post.dto.DatePeriodDto;
import telran.java47.post.dto.NewCommentDto;
import telran.java47.post.dto.NewPostDto;
import telran.java47.post.dto.PostDto;
import telran.java47.post.exceptions.PostNotFoundException;
import telran.java47.post.model.Comment;
import telran.java47.post.model.Post;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
	
	final PostRepository postRepository;
	final ModelMapper modelMapper;

	@Override
	public PostDto addNewPost(String author, NewPostDto newPostDto) {
		Post post = modelMapper.map(newPostDto, Post.class);
		post.setAuthor(author);
		postRepository.save(post);
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public PostDto findPostById(String id) {
		return modelMapper.map(postRepository.findById(id).orElseThrow(()-> new PostNotFoundException()), PostDto.class);
	}

	@Override
	public PostDto removePost(String id) {
		Post post = postRepository.findById(id).orElseThrow(()-> new PostNotFoundException());
		postRepository.delete(post);
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public PostDto updatePost(String id, NewPostDto newPostDto) {
		Post post = postRepository.findById(id).orElseThrow(()-> new PostNotFoundException());
		if(newPostDto.getTitle()!=null) {
			post.setTitle(newPostDto.getTitle());
		}
		if(newPostDto.getContent()!= null) {
			post.setContent(newPostDto.getContent());
		}
		if(newPostDto.getTags()!=null) {
			newPostDto.getTags().forEach((tag)-> post.addTag(tag));
		}
		postRepository.save(post);
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public PostDto addComment(String id, String author, NewCommentDto newCommentDto) {
		Post post = postRepository.findById(id).orElseThrow(()-> new PostNotFoundException());
		post.addComment(new Comment(author, modelMapper.map(newCommentDto, String.class)));
		postRepository.save(post);
		return modelMapper.map(post, PostDto.class);
	}

	@Override
	public void addLike(String id) {
		Post post = postRepository.findById(id).orElseThrow(()-> new PostNotFoundException());
		post.addLike();
		postRepository.save(post);
	}

	@Override
	public Iterable<PostDto> findPostsByAuthor(String author) {
		return postRepository.findPostsByAuthorIgnoreCase(author)
					.map(p -> modelMapper.map(p, PostDto.class))
					.toList();
	}

	@Override
	public Iterable<PostDto> findPostsByTags(List<String> tags) {
		return postRepository.findByTagsIn(tags)
							.map(p -> modelMapper.map(p, PostDto.class))
							.toList();
	}

	@Override
	public Iterable<PostDto> findPostsByPeriod(DatePeriodDto datePeriodDto) {
		return postRepository.findByDateCreatedBetween(datePeriodDto.getDateFrom(), datePeriodDto.getDateTo())
							.map(p -> modelMapper.map(p, PostDto.class))
							.toList();
	}

}
