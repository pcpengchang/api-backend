package com.pc.project.apidomain.datasource;

import com.pc.project.apistarter.enums.SearchTypeEnum;
import org.springframework.stereotype.Component;
import org.apache.poi.ss.formula.functions.T;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author pengchang
 * @date 2023/03/31 15:30
 **/
@Component
public class DataSourceRegistry {
    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private PostDataSource postDataSource;

    private Map<String, DataSource<T>> typeDataSourceMap;

    /**
     * 注册器
     */
    @PostConstruct
    public void doInit() {
        typeDataSourceMap = new HashMap() {{
            put(SearchTypeEnum.POST.getValue(), postDataSource);
//            put(SearchTypeEnum.USER.getValue(), userDataSource);
            put(SearchTypeEnum.PICTURE.getValue(), pictureDataSource);
        }};
    }

    public DataSource getDataSourceByType(String type) {
        if (typeDataSourceMap == null) {
            return null;
        }
        return typeDataSourceMap.get(type);
    }
}
