package com.tarasov.service.impl;

import com.tarasov.model.Post;
import com.tarasov.model.PostSearchCondition;
import com.tarasov.model.dto.posts.PostCreateRequest;
import com.tarasov.model.dto.posts.PostListResponse;
import com.tarasov.model.dto.posts.PostResponse;
import com.tarasov.model.dto.posts.PostSearchResult;
import com.tarasov.repository.PostsRepository;
import com.tarasov.service.PostsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class PostsServiceImpl implements PostsService {

    private final PostsRepository postsRepository;
    private final int MAX_TEXT_LENGTH;

    public PostsServiceImpl(PostsRepository postsRepository,
                            @Value("${posts.search.max-text-length}") int MAX_TEXT_LENGTH) {
        this.postsRepository = postsRepository;
        this.MAX_TEXT_LENGTH = MAX_TEXT_LENGTH;
    }

    @Override
    public PostListResponse searchPosts(String search, int pageSize, int pageNumber) {
        var condition = combineSearchCondition(search, pageSize, pageNumber);
        PostSearchResult postSearchResult = postsRepository.searchPosts(condition);
        int pageCount = (postSearchResult.totalCount() / pageSize) + (postSearchResult.totalCount() % pageSize > 0 ? 1 : 0);
        if (pageCount == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Posts not found");
        } else if (pageNumber > pageCount) {
            throw new IllegalArgumentException("Incorrect page, last page: " + pageCount);
        }
        return new PostListResponse(
                postSearchResult.posts().stream()
                        .peek(post -> {
                            if (post.getText().length() > MAX_TEXT_LENGTH) {
                                post.setText(post.getText().substring(0, MAX_TEXT_LENGTH) + "...");
                            }
                        })
                        .map(PostResponse::from)
                        .toList(),
                pageNumber > 1,
                pageNumber < pageCount,
                pageCount
        );
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

    private PostSearchCondition combineSearchCondition(String search, int pageSize, int pageNumber) {
        var searchItems = search.split(" ");
        var partitionedByHash = Arrays.stream(searchItems)
                .filter(s -> !s.trim().isEmpty())
                .collect(Collectors.partitioningBy(str -> str.charAt(0) == '#'));
        List<String> tagFilters = partitionedByHash.get(true)
                .stream().map(str -> str.substring(1))
                .filter(s -> !s.trim().isEmpty())
                .toList();
        String title = String.join(" ", partitionedByHash.get(false));
        return new PostSearchCondition(title, tagFilters, pageSize, pageNumber);
    }
}
