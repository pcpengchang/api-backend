package com.pc.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pc.apicommon.model.entity.InterfaceInfo;
import com.pc.project.common.ErrorCode;
import com.pc.project.exception.BusinessException;
import com.pc.project.mapper.InterfaceInfoMapper;
import com.pc.project.service.InterfaceInfoService;
import com.pc.project.utils.AlgorithmUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.pc.project.common.cache.MemoryCacheManager.BASE_INFO_CACHE;
import static com.pc.project.common.cache.MemoryCacheManager.getInterfaceInfoCacheById;

/**
 *
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = interfaceInfo.getName();
        // 创建时，所有参数必须非空
        if (add) {
            if (StringUtils.isAnyBlank(name)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }
        if (StringUtils.isNotBlank(name) && name.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
    }

    @Override
    public List<String> matchInterfaceInfos(long num, InterfaceInfo interfaceInfo) {
        String description = interfaceInfo.getDescription();
        Gson gson = new Gson();
        List<String> descriptionList = gson.fromJson(description, new TypeToken<List<String>>() {
        }.getType());

        List<InterfaceInfo> interfaceInfoList = Lists.newArrayListWithExpectedSize(BASE_INFO_CACHE.size());
        BASE_INFO_CACHE.keySet().forEach(k -> interfaceInfoList.add(getInterfaceInfoCacheById(k)));

//        // 接口列表的下标 => 相似度
//        List<Pair<InterfaceInfo, Long>> list = new ArrayList<>();

        // 使用排序树(分数 -> 接口列表)
        SortedMap<Integer, List<String>> scoreSortedMap = new TreeMap<>();

        // 依次计算所有接口和当前接口的相似度
        for (InterfaceInfo info : interfaceInfoList) {
            String des = info.getDescription();
            // 无描述 或 为当前接口
            if (StringUtils.isBlank(des) || interfaceInfo.getId().equals(info.getId())) {
                continue;
            }
            List<String> infoDesList = gson.fromJson(des, new TypeToken<List<String>>() {
            }.getType());
            // 计算分数
            int distance = (int)((1 - AlgorithmUtils.cosineSimilarity(descriptionList, infoDesList)) * 100);
//            System.out.println(1 - StrUtil.similar(descriptionList.toString(), infoDesList.toString()));

            List<String> scoreList = scoreSortedMap.values().stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            if (scoreList.size() >= num) {
                int highestScore = scoreSortedMap.lastKey();
                // 如果分数小于则替换最大分数用户
                if (distance < highestScore) {
                    scoreSortedMap.compute(highestScore, (score, infoList) -> {
                        // 分数对应的接口只有一个时，删除分数map
                        if (infoList.size() == 1) {
                            // 表示删除键和值
                            return null;
                        } else {
                            // 对应多个时，删除该分数中的一个用户
                            infoList.remove(0);
                            return infoList;
                        }
                    });
                    addScoreInfo(scoreSortedMap, info, distance);
                }
            }
            else {
                // 容量未达num个
                addScoreInfo(scoreSortedMap, info, distance);
            }
            //list.add(new Pair<>(info, distance));
        }
//
//        if (CollectionUtils.isEmpty(list)) {
//            return Lists.newArrayList();
//        }

        return scoreSortedMap.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * 排序树添加接口
     */
    private static void addScoreInfo(SortedMap<Integer, List<String>> scoreSortedMap,
                                     InterfaceInfo info, int minDistance) {
        scoreSortedMap.computeIfPresent(minDistance, (k, v) -> {
            v.add(info.getName());
            return v;
        });
        scoreSortedMap.computeIfAbsent(minDistance,
                (k) -> scoreSortedMap.put(minDistance, Lists.newArrayList(info.getName())));
    }


}




