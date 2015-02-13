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
 * 数据块实体类
 *
 * @author jexa7410
 */
public class DataChunks {
    /**
     * 数据块大小
     */
    private int currentSize = 0;
    
    /**
	 * 数据块缓冲区
	 */
	private ByteArrayOutputStream cache = new ByteArrayOutputStream();	

	/**
	 * 构造函数
	 */
	public DataChunks() {
	}
	
	/**
	 * 添加数据
	 *
	 * @param data 数据块
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
     * 获取接收到的数据
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
     * 重置缓存
     */
    public void resetCache() {
    	cache.reset();
    }

    /**
	 * 获取接收到数据块大小
	 *
	 * @return Size in bytes
	 */
	public int getCurrentSize() {
		return currentSize;
	}
}
