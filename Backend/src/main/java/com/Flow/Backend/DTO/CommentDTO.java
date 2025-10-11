package com.Flow.Backend.DTO;

public class CommentDTO {
    String reply;
    Long postId;

    public Long getPostId() {
        return postId;
    }

    public void setQueryId(Long queryId) {
        this.postId = postId;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
