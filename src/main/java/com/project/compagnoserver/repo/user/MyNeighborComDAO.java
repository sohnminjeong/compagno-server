package com.project.compagnoserver.repo.user;

import com.project.compagnoserver.domain.NeighborBoard.NeighborBoardComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface MyNeighborComDAO extends JpaRepository<NeighborBoardComment, Integer>, QuerydslPredicateExecutor<NeighborBoardComment> {
}
