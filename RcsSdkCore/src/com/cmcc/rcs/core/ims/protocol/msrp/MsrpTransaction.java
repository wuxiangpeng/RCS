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
 * MSRP ������
 *
 * @author B. JOGUET
 */
public class MsrpTransaction extends Object {
    /**
     * MRSP report transaction ��ʱʱ�� (in seconds)
     */
    private final static int TIMEOUT = 30;

    /**
     * ����������������������Ӧ�� 
     */
    private int waitingCount = 0;

    // Changed by Deutsche Telekom
    /**
     * ���յ��� ��Ӧ200OK����
     */
    private int totalReceivedResponses = 0;

    /**
     * ����������������������Ӧ�� 
     */
    private boolean isWaiting = false;

    /**
     * MSRP �Ự ������־ 
     */
    private boolean isTerminated = false;

    /**
     * ��ʱ��
     */
    private Timer timer = new Timer();

    /**
     * ���캯��
     */
    public MsrpTransaction() {
    }

    /**
     * �ȴ����е� MSRP ��Ӧ
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
     * Handle �µ�����
     */
    public void handleRequest() {
        // Changed by Deutsche Telekom
        // requests and responses are handled in different threads which need to be synchronized
        synchronized(this){
            waitingCount++;
        }
    }

    /**
     * Handle �µ���Ӧ
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
     * �Ƿ��յ����е���Ӧ
     *
     * @return Boolean
     */
    public boolean isAllResponsesReceived() {
        return (waitingCount == 0);
    }

    /**
     *   ��ֹ����
     */
    public synchronized void terminate() {
        isTerminated = true;
        // Unblock semaphore
        super.notify();
        // Stop timer
        stopTimer();
    }

    /** 
     * ��ȡ��ֹ״̬
     *
     * @return true if terminated
     */
    public boolean isTerminated() {
        return isTerminated;
    }

    /**
     * ��ʼ��ʱ��
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
     * ֹͣ��ʱ��
     */
    private void stopTimer() {
        timer.cancel();
    }

    /** 
     * ��ʱ��ִ��
     */
    private synchronized void timerExpire() {
        // Unblock semaphore
        super.notify();
    }

    // Changed by Deutsche Telekom
    /**
     * @return totalReceivedResponses - ���ܱ��������
     */
    public int getNumberReceivedOk() {
        return totalReceivedResponses;
    }
}
