package com.pc.project.apistarter.model.vo;

import com.pc.apicommon.model.entity.Picture;
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
