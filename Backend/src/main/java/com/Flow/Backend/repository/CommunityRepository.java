package com.Flow.Backend.repository;

import com.Flow.Backend.model.CommunityModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CommunityRepository extends JpaRepository<CommunityModel, Long> {

    // Find all communities where the user is a member or admin
    @Query("SELECT c FROM CommunityModel c JOIN c.members m WHERE m = :username")
    List<CommunityModel> findAllByMemberUsername(@Param("username") String username);

    @Query("SELECT c FROM CommunityModel c JOIN c.admin a WHERE a = :username")
    List<CommunityModel> findAllByAdminUsername(@Param("username") String username);

    List<CommunityModel> findByMembersContainsOrAdminContains(String member,String admin);
}
