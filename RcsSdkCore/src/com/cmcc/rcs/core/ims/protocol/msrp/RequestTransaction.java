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
 * Request 事务类
 * 
 * @author jexa7410
 */
public class RequestTransaction extends Object {
    /**
     * MRSP request transaction 事务 超时 (in seconds)
     */
    private final static int TIMEOUT = RcsSettings.getInstance().getMsrpTransactionTimeout();
	
    /**
     * 接收 response
     */
    private int receivedResponse = -1;
    
    /**
	 * 构造函数
	 */
	public RequestTransaction() {
	}
	
	/**
	 * 通知 response
	 * 
	 * @param code 响应码
	 * @param headers MSRP 头
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
	 * 等待 response
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
	 * 终止事务
	 */
	public void terminate() {
		synchronized(this) {
			// Unblock semaphore
			super.notify();
		}
	}
	
	/**
	 * response是否被接收
	 * 
	 * @return Boolean
	 */
	public boolean isResponseReceived() {
		return (receivedResponse != -1);
	}

	/**
	 * 获取 接收response
	 * 
	 * @return Code
	 */
	public int getResponse() {
		return receivedResponse;
	}
}
