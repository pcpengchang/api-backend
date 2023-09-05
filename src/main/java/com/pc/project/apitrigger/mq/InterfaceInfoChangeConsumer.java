package com.pc.project.apitrigger.mq;

import com.google.gson.Gson;
import com.pc.apicommon.model.entity.InterfaceInfo;
import com.pc.project.apiinfrastructure.mapper.InterfaceInfoMapper;
import com.pc.project.apistarter.constant.BinLogTypeConstant;
import com.pc.project.apistarter.model.BaseBinlogModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.pc.project.apistarter.cache.MemoryCacheManager.updateBaseInfo;

/**
 * @author pengchang
 * @date 2023/09/02 12:49
 **/
@Slf4j
@Component
public class InterfaceInfoChangeConsumer {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @KafkaListener(topics = "example", groupId = "example")
    public void message(ConsumerRecord<?, String> msgBody, Acknowledgment ack) throws Exception {

        if (StringUtils.isBlank(msgBody.value())) {
            return;
        }

        BaseBinlogModel baseBinlogModel;
        try {
            baseBinlogModel = new Gson().fromJson(msgBody.value(), BaseBinlogModel.class);
        } catch (Exception e) {
            log.error("Parse msgBody to BaseBinlogModel error, msgBody ={}", msgBody, e);
            ack.acknowledge();
            return;
        }

        // todo 删除的case 缓存一致性
        if (baseBinlogModel.getIsDdl()
                || !Objects.equals("interface_info", baseBinlogModel.getTable())
                || CollectionUtils.isEmpty(baseBinlogModel.getOld())
                || Objects.equals(BinLogTypeConstant.DELETE, baseBinlogModel.getType())) {
            ack.acknowledge();
            return;
        }

        List<Map<String, Object>> messageDataList = baseBinlogModel.getData().stream()
                .filter(each -> each.get("id") != null)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(messageDataList)) {
            ack.acknowledge();
            return;
        }

        for (Map<String, Object> each : messageDataList) {
            Long id = Long.valueOf(each.get("id").toString());
//            InterfaceInfo info = BeanUtil.convert(each, InterfaceInfo.class);
            InterfaceInfo info = interfaceInfoMapper.selectById(id);
            updateBaseInfo(info.getId(), info);
            log.info("消费MQ消息：{}", info);
        }

        ack.acknowledge();
    }
}