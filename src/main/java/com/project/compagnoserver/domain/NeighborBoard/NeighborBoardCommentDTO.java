package com.project.compagnoserver.domain.NeighborBoard;

import com.project.compagnoserver.domain.user.User;
import com.project.compagnoserver.domain.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
@Builder
public class NeighborBoardCommentDTO {

    private int neighborCommentCode;
    private int neighborBoardCode;
    private String neighborCommentContent;
    private Date neighborCommentRegiDate;
    private UserDTO user;
//    private int sitterCommentParentCode;
//    private String sitterCommentStatus;
//    private Date sitterCommentDelDate;

    private List<NeighborBoardCommentDTO> neighborReplies = new ArrayList<>();
}

