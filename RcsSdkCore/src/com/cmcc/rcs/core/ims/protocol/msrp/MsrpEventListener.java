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
 * MSRP �¼������ӿ�
 * 
 * @author jexa7410
 * @author Deutsche Telekom AG
 */
public interface MsrpEventListener {
	/**
	 * ���ݱ�����
	 * 
	 * @param msgId ��Ϣ ID
	 */
	public void msrpDataTransfered(String msgId);
	
	/**
	 * ���ݱ�����
	 * 
	 * @param msgId ��Ϣ ID
	 * @param data ��������
	 * @param mimeType Data mime-type 
	 */
	public void msrpDataReceived(String msgId, byte[] data, String mimeType);
	
	/**
	 * �������ڴ���
	 * 
	 * @param currentSize ��ǰ�����С
	 * @param totalSize �ܹ���С
	 */
	public void msrpTransferProgress(long currentSize, long totalSize);	

    /**
     * �������ڴ���
     *
     * @param currentSize ��ǰ�����С
     * @param totalSize �ܹ���С
     * @param data �������ݿ�
     * @return true ��ʾ�����Ѿ�������� If false,���ݱ�����
     *         MsrpSession ������ ֱ�� msrpDataReceived ������.
     */
    public boolean msrpTransferProgress(long currentSize, long totalSize, byte[] data);

	/**
	 * ���ݴ���ʧ��
	 */
	public void msrpTransferAborted();

	// Changed by Deutsche Telekom
    /**
     * ���ݴ������
     *
     * @param msgId ��Ϣ ID
     * @param error �������ode
     * @param MSRP���ݿ�����
     */
    public void msrpTransferError(String msgId, String error, TypeMsrpChunk typeMsrpChunk);
}
