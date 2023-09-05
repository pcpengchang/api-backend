package com.pc.project.apicommon.service;

import cn.hutool.core.util.StrUtil;
import com.pc.project.apistarter.strategy.UploadStrategyService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * @author pengchang
 * @date 2023/08/28 00:15
 **/
@SpringBootTest
public class MonitorTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void getRedisMonitorInfo() {
        // 获得 Redis 统计信息
        Properties info = stringRedisTemplate.execute((RedisCallback<Properties>) RedisServerCommands::info);
        Long dbSize = stringRedisTemplate.execute(RedisServerCommands::dbSize);
        Properties commandStats = stringRedisTemplate.execute((
                RedisCallback<Properties>) connection -> connection.info("commandstats"));
        RedisMonitorRespVO respVO = RedisMonitorRespVO.builder().info(info).dbSize(dbSize)
                .commandStats(new ArrayList<>(commandStats.size())).build();
        commandStats.forEach((key, value) -> {
            respVO.getCommandStats().add(RedisMonitorRespVO.CommandStat.builder()
                    .command(StrUtil.subAfter((String) key, "cmdstat_", false))
                    .calls(Long.valueOf(StrUtil.subBetween((String) value, "calls=", ",")))
                    .usec(Long.valueOf(StrUtil.subBetween((String) value, "usec=", ",")))
                    .build());
        });
        System.out.println(respVO);
    }

    @Resource
    private UploadStrategyService uploadStrategyService;

    @Test
    public void uploadMinioFile() throws Exception {
        File file = new File("src/test/example-img.png");
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(), "image/png",
                new FileInputStream(file));
        String url = uploadStrategyService.executeUploadStrategy(multipartFile, "avatar/");
        System.out.println(url);
    }
}
