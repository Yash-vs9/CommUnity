package com.Flow.Backend.service;

import com.Flow.Backend.DTO.*;
import com.Flow.Backend.exceptions.AccessDeniedException;
import com.Flow.Backend.exceptions.CommunityNotFoundException;
import com.Flow.Backend.exceptions.PostNotFoundException;
import com.Flow.Backend.exceptions.UserNotFoundException;
import com.Flow.Backend.model.Comment;
import com.Flow.Backend.model.CommunityModel;
import com.Flow.Backend.model.PostModel;
import com.Flow.Backend.model.UserModel;
import com.Flow.Backend.repository.CommunityRepository;
import com.Flow.Backend.repository.PostRepository;
import com.Flow.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private PostRepository postRepository;

    @Transactional
    public String createPost(CreatePost createPost){
        CommunityModel community = communityRepository.findById(createPost.getCommunityId())
                .orElseThrow(() -> new CommunityNotFoundException("Community not found with id: " + createPost.getCommunityId()));

        UserModel user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + SecurityContextHolder.getContext().getAuthentication().getName()));

        PostModel post = new PostModel();
        post.setTitle(createPost.getTitle());
        post.setDescription(createPost.getDescription());
        post.setImageUrl(createPost.getImageurl());
        post.setCreatedByUser(user.getUsername());
        postRepository.save(post);

        return "Post '" + createPost.getTitle() + "' created successfully!";
    }
    @Transactional
    public String deletePost(Long postId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        PostModel post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        CommunityModel community = post.getCommunity();

        boolean isCreator = post.getUser().getUsername().equals(currentUsername);
        boolean isAdmin = community.getAdmin().contains(currentUsername);

        if (!isCreator && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to delete this post");
        }

        postRepository.delete(post);

        return "Post deleted successfully by " + currentUsername + "!";
    }
    @Transactional
    public String editPost(EditPostDTO body){
        String currentUsername=SecurityContextHolder.getContext().getAuthentication().getName();
        PostModel post = postRepository.findById(body.getId())
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + body.getId()));
        CommunityModel community= post.getCommunity();
        boolean isCreator = post.getCreatedByUser().equals(currentUsername);
        boolean isAdmin = community.getAdmin().contains(currentUsername);

        if (!isCreator && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to edit this post");
        }
        post.setTitle(body.getTitle());
        post.setDescription(body.getDescription());
        postRepository.save(post);
        return "Edited Successfully";
    }
    @Transactional
    public String likeOrDislikePost(Long postId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        PostModel post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        boolean alreadyLiked = user.getLikedPosts()
                .stream()
                .anyMatch(p -> p.getId().equals(postId));

        if (alreadyLiked) {
            user.getLikedPosts().remove(post);
            post.setLikes(post.getLikes() - 1);
            userRepository.save(user);
            postRepository.save(post);
            return "Post unliked successfully";
        } else {
            user.getLikedPosts().add(post);
            post.setLikes(post.getLikes() + 1);
            userRepository.save(user);
            postRepository.save(post);
            return "Post liked successfully";
        }
    }
    @Transactional
    public String  postComment(CommentDTO comment){
        PostModel post=postRepository.findById(comment.getPostId())
                .orElseThrow(()->new PostNotFoundException("Post Not found"));
        Comment commentObj=new Comment();
        commentObj.setPost(post);
        commentObj.setReply(comment.getReply());
        commentObj.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        post.getComments().add(commentObj);
        postRepository.save(post);
        return "The comment has been created seuccessfully";
    }
    @Transactional
    public  String deleteComment(Long postId,Long commentId){
        String currentUsername=SecurityContextHolder.getContext().getAuthentication().getName();
        PostModel post=postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException("Post Not found"));
        Comment comment = post.getComments().stream()
                .filter(c->c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(()-> new CommunityNotFoundException("Comment Not Found"));
        CommunityModel community=post.getCommunity();

        boolean isCommentOwner=comment.getUsername().equals(currentUsername);
        boolean isAdmin=community.getAdmin().contains(currentUsername);
        if(!isCommentOwner&&isAdmin){
            throw new AccessDeniedException("You are not authorized to delete this comment");
        }
        post.getComments().remove(commentId);
        postRepository.save(post);
        return "Comment Deleted Succeessfully by "+ currentUsername;

    }

    @Transactional(readOnly = true)
    public PostWithCommentsDTO getPostWithComments(Long postId) {
        PostModel post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        PostWithCommentsDTO dto = new PostWithCommentsDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setImageUrl(post.getImageUrl());
        dto.setCreatedByUser(post.getCreatedByUser());
        dto.setLikes(post.getLikes());
        dto.setCreatedAt(post.getCreatedAt());

        // Map comments
        List<CommentResponseDTO> commentDTOs = post.getComments().stream()
                .map(c -> {
                    CommentResponseDTO commentDTO = new CommentResponseDTO();
                    commentDTO.setId(c.getId());
                    commentDTO.setUsername(c.getUsername());
                    commentDTO.setReply(c.getReply());
                    commentDTO.setCreatedAt(c.getCreatedAt());
                    return commentDTO;
                })
                .toList();

        dto.setComments(commentDTOs);

        return dto;
    }
    @Transactional(readOnly = true)
    public List<PostWithCommentsDTO> getPostsByLoggedInUser() {
        // Fetch the user by email
        UserModel user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + SecurityContextHolder.getContext().getAuthentication().getName()));

        // Fetch all posts created by this user
        List<PostModel> userPosts = postRepository.findByCreatedByUser(user.getUsername());

        // Convert each post to PostWithCommentsDTO
        return userPosts.stream().map(post -> {
            PostWithCommentsDTO dto = new PostWithCommentsDTO();
            dto.setId(post.getId());
            dto.setTitle(post.getTitle());
            dto.setDescription(post.getDescription());
            dto.setImageUrl(post.getImageUrl());
            dto.setCreatedByUser(post.getCreatedByUser());
            dto.setLikes(post.getLikes());
            dto.setCreatedAt(post.getCreatedAt());

            // Convert comments
            List<CommentResponseDTO> commentDTOs = post.getComments().stream().map(comment -> {
                CommentResponseDTO cDto = new CommentResponseDTO();
                cDto.setId(comment.getId());
                cDto.setUsername(comment.getUsername());
                cDto.setReply(comment.getReply());
                cDto.setCreatedAt(comment.getCreatedAt());
                return cDto;
            }).toList();

            dto.setComments(commentDTOs);
            return dto;
        }).toList();
    }
}
