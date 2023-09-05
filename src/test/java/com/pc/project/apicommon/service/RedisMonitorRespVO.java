package com.pc.project.apicommon.service;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Properties;

@Schema(description = "管理后台 - Redis 监控信息 Response VO")
@Data
@Builder
@AllArgsConstructor
public class RedisMonitorRespVO {

    private Properties info;

    private Long dbSize;

    private List<CommandStat> commandStats;

    @Data
    @Builder
    @AllArgsConstructor
    public static class CommandStat {

        private String command;

        private Long calls;

        private Long usec;

    }

}
