package com.tarasov.service.impl;

import com.tarasov.model.Post;
import com.tarasov.model.dto.posts.PostCreateRequest;
import com.tarasov.model.dto.posts.PostResponse;
import com.tarasov.repository.PostsRepository;
import com.tarasov.service.PostsService;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class PostsServiceImpl implements PostsService {

    private final PostsRepository postsRepository;

    public PostsServiceImpl(PostsRepository postsRepository) {
        this.postsRepository = postsRepository;
    }

    @Override
    public Optional<PostResponse> getPostById(long id) {
        Optional<Post> post = postsRepository.findPost(id);
        return post.map(PostResponse::from);
    }

    @Override
    public PostResponse createPost(PostCreateRequest request) {
        Post post = postsRepository.createPost(request.title(), request.text());
        request.tags().forEach(tag -> postsRepository.createTag(post.getId(), tag));
        post.setTags(request.tags());
        return PostResponse.from(post);
    }
}
