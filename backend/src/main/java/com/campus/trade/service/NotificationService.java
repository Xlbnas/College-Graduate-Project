package com.campus.trade.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 论文示例中的站内信通知占位实现（演示环境记录日志即可）。
 */
@Slf4j
@Service
public class NotificationService {

    public void sendNotify(Long userId, String message) {
        log.info("[站内信] userId={} message={}", userId, message);
    }
}
