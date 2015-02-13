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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * ���ݿ�ʵ����
 *
 * @author jexa7410
 */
public class DataChunks {
    /**
     * ���ݿ��С
     */
    private int currentSize = 0;
    
    /**
	 * ���ݿ黺����
	 */
	private ByteArrayOutputStream cache = new ByteArrayOutputStream();	

	/**
	 * ���캯��
	 */
	public DataChunks() {
	}
	
	/**
	 * �������
	 *
	 * @param data ���ݿ�
	 */
	public void addChunk(byte[] data) throws IOException, MsrpException {
        try {
		cache.write(data, 0, data.length);
        } catch (OutOfMemoryError e) {
            throw new MsrpException("Not enough memory to save data");
        }
		currentSize += data.length;
	}

	/**
     * ��ȡ���յ�������
     *
     * @return Byte array
     */
    public byte[] getReceivedData() throws IOException, MsrpException {
    	byte[] result=null;
    	try {
			result = cache.toByteArray();
		} catch (OutOfMemoryError e) {
            throw new MsrpException("Not enough memory to copy data");
		}
        return result;
    }

	/**
     * ���û���
     */
    public void resetCache() {
    	cache.reset();
    }

    /**
	 * ��ȡ���յ����ݿ��С
	 *
	 * @return Size in bytes
	 */
	public int getCurrentSize() {
		return currentSize;
	}
}
