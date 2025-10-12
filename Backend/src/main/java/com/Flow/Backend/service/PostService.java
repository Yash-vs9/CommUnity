package com.Flow.Backend.service;

import com.Flow.Backend.DTO.CommentDTO;
import com.Flow.Backend.DTO.CreatePost;
import com.Flow.Backend.DTO.EditPostDTO;
import com.Flow.Backend.exceptions.AccessDeniedException;
import com.Flow.Backend.exceptions.CommunityNotFoundException;
import com.Flow.Backend.exceptions.PostNotFoundException;
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
                .orElseThrow(() -> new RuntimeException("Community not found with id: " + createPost.getCommunityId()));

        UserModel user = userRepository.findById(createPost.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + createPost.getUserId()));

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

}
