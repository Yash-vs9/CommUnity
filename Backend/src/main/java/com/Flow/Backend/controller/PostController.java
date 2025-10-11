package com.Flow.Backend.controller;

import com.Flow.Backend.DTO.CreatePost;
import com.Flow.Backend.DTO.EditPostDTO;
import com.Flow.Backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @PostMapping("/createPost")
    public String createPost(@RequestBody CreatePost createPost){
        return postService.createPost(createPost);
    }

    @DeleteMapping("/{id}/deletePost")
    public String deletePost(@PathVariable Long id){
        return postService.deletePost(id);

    }
    @PostMapping("/editPost")
    public String editPost(@RequestBody EditPostDTO editPostDTO){
        return postService.editPost(editPostDTO);
    }
}
