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

import java.util.Hashtable;

/**
 * Report 事务类
 * 
 * @author jexa7410
 */
public class ReportTransaction extends Object {
    /**
     * MRSP report 事务 超时 (in seconds)
     */
    private final static int TIMEOUT = 3600; // TODO: which value ?

    /**
     * Reported 大小
     */
    private long reportedSize = 0L;

    /**
     * 状态码
     */
    private int statusCode = -1;

    /**
     * 表明 接收到字节范围
     */
    private boolean receivedByteRangeHeader;

    /**
     * 表明 report是否通知
     */
    private boolean isNotified = false;

    /**
	 * 构造函数
	 */
	public ReportTransaction() {
	}
	
	/**
	 * 通知 report
	 * 
	 * @param 状态码
	 * @param headers MSRP 头
	 */
	public void notifyReport(int status, Hashtable<String, String> headers) {
		synchronized(this) {
            receivedByteRangeHeader = false;
            isNotified = true;
            statusCode = status;

			// Get reported size
			String byteRange = headers.get(MsrpConstants.HEADER_BYTE_RANGE);
			if (byteRange != null) {
				reportedSize = MsrpUtils.getChunkSize(byteRange);
                receivedByteRangeHeader = true;
			}

			// Unblock semaphore
			super.notify();
		}
	}
	
	/**
	 * 等待 report
	 */
	public void waitReport() {
		synchronized(this) {
			try {
				// Wait semaphore
				super.wait(TIMEOUT * 1000);
			} catch(InterruptedException e) {
			    // Nothing to do
			}
		}
	}
	
	/**
	 * 终止事务
	 */
	public void terminate() {
		synchronized(this) {
			// Unblock semaphore
			super.notify();
		}
	}
	
	/**
	 * 获取 reported 数据大小
	 * 
	 * @return 字节大小
	 */
	public long getReportedSize() {
		return reportedSize;
	}

	/**
	 * 获取状态码
	 * 
	 * @return 状态码
	 */
	public int getStatusCode() {
		return statusCode;
	}

    /**
     * 检查 事务是否完成
     * @param totalSize 事务大小
     * @return <code>True</code> if transaction is finished, <code>false</code> otherwise.
     */
    public boolean isTransactionFinished(long totalSize) {
        if (isNotified) {
            if (!receivedByteRangeHeader) {
                return true;
            }
            if (reportedSize == totalSize) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取状态码
     *
     * @param headers
     * @return 
     */
    public static int parseStatusCode(Hashtable<String, String> headers) {
        int statusCode = -1;
        String status = headers.get(MsrpConstants.HEADER_STATUS);
        if ((status != null) && (status.startsWith("000 "))) {
            String[] parts = status.split(" "); 
            if (parts.length > 0) {
                try {
                    statusCode = Integer.parseInt(parts[1]);
                } catch(NumberFormatException e) {
                    // Nothing to do
                }
            }
        }
        return statusCode;
    }
}
