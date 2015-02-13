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

import com.cmcc.rcs.provider.settings.RcsSettings;

/**
 * Request ������
 * 
 * @author jexa7410
 */
public class RequestTransaction extends Object {
    /**
     * MRSP request transaction ���� ��ʱ (in seconds)
     */
    private final static int TIMEOUT = RcsSettings.getInstance().getMsrpTransactionTimeout();
	
    /**
     * ���� response
     */
    private int receivedResponse = -1;
    
    /**
	 * ���캯��
	 */
	public RequestTransaction() {
	}
	
	/**
	 * ֪ͨ response
	 * 
	 * @param code ��Ӧ��
	 * @param headers MSRP ͷ
	 */
	public void notifyResponse(int code, Hashtable<String, String> headers) {
		synchronized(this) {
			// Set response code
			this.receivedResponse = code;
			
			// Unblock semaphore
			super.notify();
		}
	}
	
	/**
	 * �ȴ� response
	 */
	public void waitResponse() {
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
	 * response�Ƿ񱻽���
	 * 
	 * @return Boolean
	 */
	public boolean isResponseReceived() {
		return (receivedResponse != -1);
	}

	/**
	 * ��ȡ ����response
	 * 
	 * @return Code
	 */
	public int getResponse() {
		return receivedResponse;
	}
}
