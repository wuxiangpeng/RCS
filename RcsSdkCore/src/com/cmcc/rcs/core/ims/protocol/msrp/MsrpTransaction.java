/*******************************************************************************
 * Software Name : RCS IMS Stack
 *
 * Copyright (C) 2010 France Telecom S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.cmcc.rcs.core.ims.protocol.msrp;

import java.util.Timer;
import java.util.TimerTask;

/**
 * MSRP 事务类
 *
 * @author B. JOGUET
 */
public class MsrpTransaction extends Object {
    /**
     * MRSP report transaction 超时时间 (in seconds)
     */
    private final static int TIMEOUT = 30;

    /**
     * 发送请求数量（不包括响应） 
     */
    private int waitingCount = 0;

    // Changed by Deutsche Telekom
    /**
     * 接收到的 响应200OK数量
     */
    private int totalReceivedResponses = 0;

    /**
     * 发送请求数量（不包括响应） 
     */
    private boolean isWaiting = false;

    /**
     * MSRP 会话 结束标志 
     */
    private boolean isTerminated = false;

    /**
     * 计时器
     */
    private Timer timer = new Timer();

    /**
     * 构造函数
     */
    public MsrpTransaction() {
    }

    /**
     * 等待所有的 MSRP 响应
     */
    public synchronized void waitAllResponses() {
        if (waitingCount > 0) {
            isWaiting = true;
            try {
                // Start timeout
                startTimer();

                // Wait semaphore
                super.wait();
            } catch(InterruptedException e) {
                // Nothing to do
            }
        }
    }

    /**
     * Handle 新的请求
     */
    public void handleRequest() {
        // Changed by Deutsche Telekom
        // requests and responses are handled in different threads which need to be synchronized
        synchronized(this){
            waitingCount++;
        }
    }

    /**
     * Handle 新的响应
     */
    public synchronized void handleResponse() {
        // Changed by Deutsche Telekom
        // requests and responses are handled in different threads which need to be synchronized
        synchronized(this){
            waitingCount--;
        }
        // Changed by Deutsche Telekom
        totalReceivedResponses++;
        if (isWaiting) {
            if (waitingCount == 0) {
                // Unblock semaphore
                super.notify();
            } else {
                // ReInit timeout
                stopTimer();
                startTimer();
            }
        }
    }

    /**
     * 是否收到所有的响应
     *
     * @return Boolean
     */
    public boolean isAllResponsesReceived() {
        return (waitingCount == 0);
    }

    /**
     *   终止事务
     */
    public synchronized void terminate() {
        isTerminated = true;
        // Unblock semaphore
        super.notify();
        // Stop timer
        stopTimer();
    }

    /** 
     * 获取终止状态
     *
     * @return true if terminated
     */
    public boolean isTerminated() {
        return isTerminated;
    }

    /**
     * 开始计时器
     */
    private void startTimer() {
        timer = new Timer();
        TimerTask timertask = new TimerTask() {
            @Override
            public void run() {
                timerExpire();
            }
        };
        timer.schedule(timertask, TIMEOUT * 1000);
    }

    /**
     * 停止计时器
     */
    private void stopTimer() {
        timer.cancel();
    }

    /** 
     * 计时器执行
     */
    private synchronized void timerExpire() {
        // Unblock semaphore
        super.notify();
    }

    // Changed by Deutsche Telekom
    /**
     * @return totalReceivedResponses - 接受报告的数量
     */
    public int getNumberReceivedOk() {
        return totalReceivedResponses;
    }
}
