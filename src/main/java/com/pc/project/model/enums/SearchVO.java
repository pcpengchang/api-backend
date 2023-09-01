package com.pc.project.model.enums;

import com.pc.project.model.entity.Picture;
import com.pc.project.model.vo.PostVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 聚合搜索
 *
 * @author pengchang
 *  
 */
@Data
public class SearchVO implements Serializable {

//    private List<UserVO> userList;
//
    private List<PostVO> postList;

    private List<Picture> pictureList;

    private List<?> dataList;

    private static final long serialVersionUID = 1L;

}
