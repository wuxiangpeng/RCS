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

import com.cmcc.rcs.core.ims.protocol.msrp.MsrpSession.TypeMsrpChunk;


/**
 * MSRP 事件监听接口
 * 
 * @author jexa7410
 * @author Deutsche Telekom AG
 */
public interface MsrpEventListener {
	/**
	 * 数据被传输
	 * 
	 * @param msgId 信息 ID
	 */
	public void msrpDataTransfered(String msgId);
	
	/**
	 * 数据被接收
	 * 
	 * @param msgId 信息 ID
	 * @param data 接收数据
	 * @param mimeType Data mime-type 
	 */
	public void msrpDataReceived(String msgId, byte[] data, String mimeType);
	
	/**
	 * 数据正在传输
	 * 
	 * @param currentSize 当前传输大小
	 * @param totalSize 总共大小
	 */
	public void msrpTransferProgress(long currentSize, long totalSize);	

    /**
     * 数据正在传输
     *
     * @param currentSize 当前传输大小
     * @param totalSize 总共大小
     * @param data 接收数据块
     * @return true 表示数据已经传输完成 If false,数据保存于
     *         MsrpSession 缓存中 直到 msrpDataReceived 被调用.
     */
    public boolean msrpTransferProgress(long currentSize, long totalSize, byte[] data);

	/**
	 * 数据传输失败
	 */
	public void msrpTransferAborted();

	// Changed by Deutsche Telekom
    /**
     * 数据传输错误
     *
     * @param msgId 信息 ID
     * @param error 错误代码ode
     * @param MSRP数据块类型
     */
    public void msrpTransferError(String msgId, String error, TypeMsrpChunk typeMsrpChunk);
}
