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
 * Report ������
 * 
 * @author jexa7410
 */
public class ReportTransaction extends Object {
    /**
     * MRSP report ���� ��ʱ (in seconds)
     */
    private final static int TIMEOUT = 3600; // TODO: which value ?

    /**
     * Reported ��С
     */
    private long reportedSize = 0L;

    /**
     * ״̬��
     */
    private int statusCode = -1;

    /**
     * ���� ���յ��ֽڷ�Χ
     */
    private boolean receivedByteRangeHeader;

    /**
     * ���� report�Ƿ�֪ͨ
     */
    private boolean isNotified = false;

    /**
	 * ���캯��
	 */
	public ReportTransaction() {
	}
	
	/**
	 * ֪ͨ report
	 * 
	 * @param ״̬��
	 * @param headers MSRP ͷ
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
	 * �ȴ� report
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
	 * ��ֹ����
	 */
	public void terminate() {
		synchronized(this) {
			// Unblock semaphore
			super.notify();
		}
	}
	
	/**
	 * ��ȡ reported ���ݴ�С
	 * 
	 * @return �ֽڴ�С
	 */
	public long getReportedSize() {
		return reportedSize;
	}

	/**
	 * ��ȡ״̬��
	 * 
	 * @return ״̬��
	 */
	public int getStatusCode() {
		return statusCode;
	}

    /**
     * ��� �����Ƿ����
     * @param totalSize �����С
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
     * ��ȡ״̬��
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
