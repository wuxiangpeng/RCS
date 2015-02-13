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

import java.io.IOException;
import java.io.OutputStream;

import com.cmcc.rcs.core.ims.protocol.msrp.MsrpSession.TypeMsrpChunk;
import com.cmcc.rcs.utils.logger.Logger;


/**
 * 数据发送器
 * 
 * @author jexa7410
 */
public class ChunkSender extends Thread {
	/**
	 * MSRP 连接
	 */
	private MsrpConnection connection;

	/**
	 * MSRP 输出流
	 */
	private OutputStream stream;
	
	/**
	 * 数据块缓冲区
	 */
	private FifoBuffer buffer = new FifoBuffer();

	/**
	 * 结束标志
	 */
	private boolean terminated = false;

	/**
	 * 日志记录
	 */
	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * 构造函数
	 * 
	 * @param connection MSRP 连接
	 * @param stream TCP 输出流
	 */
	public ChunkSender(MsrpConnection connection, OutputStream stream) {
		this.connection = connection;
		this.stream = stream;
	}	
	
	/**
	 * 获取 MSRP 连接
	 * 
	 * @return MSRP 连接
	 */
	public MsrpConnection getConnection() {
		return connection;
	}
	
	/**
	 * 终止发送数据
	 */
	public void terminate() {
		terminated = true; 
		buffer.unblockRead();
		try {
			interrupt();
		} catch(Exception e) {}
		if (logger.isActivated()) {
			logger.debug("Sender is terminated");
		}
	}
	
	/**
	 * run方法
	 */
	@Override
	public void run() {
		try {
			if (logger.isActivated()) {
				logger.debug("Sender is started");
			}

			// Read chunk to be sent
			byte chunk[] = null;
			while ((chunk = (byte[])buffer.getMessage()) != null) {
				// Write chunk to the output stream
				if (MsrpConnection.MSRP_TRACE_ENABLED) {
					System.out.println(">>> Send MSRP message:\n" + new String(chunk));
				}
				writeData(chunk);
			}
		} catch (Exception e) {
			if (terminated) { 
				if (logger.isActivated()) {
					logger.debug("Chunk sender thread terminated");
				}
			} else {
				if (logger.isActivated()) {
					logger.error("Chunk sender has failed", e);
				}
				
				// Notify the msrp session listener that an error has occured
				// Changed by Deutsche Telekom
				connection.getSession().getMsrpEventListener().msrpTransferError(null, e.getMessage(), TypeMsrpChunk.Unknown);
			}
		}
	}
	
	/**
	 * 发送一个数据块
	 * 
	 * @param chunk 新数据块
	 * @throws IOException
	 */
	public void sendChunk(byte chunk[]) throws IOException {
        // Changed by Deutsche Telekom
        //CpuManager.setTempLock();
        try {
    		if (connection.getSession().isFailureReportRequested()) {
    			buffer.putMessage(chunk);
    		} else {
    			sendChunkImmediately(chunk);
    		}
        } finally {
            // Changed by Deutsche Telekom
            //CpuManager.releaseTempLock();
        }
	}	

	/**
	 * 立即发送一个数据块
	 * 
	 * @param chunk 数据块
	 * @throws IOException
	 */
	public void sendChunkImmediately(byte chunk[]) throws IOException {
		// Changed by Deutsche Telekom
	    //CpuManager.setTempLock();
	    try {
    		if (MsrpConnection.MSRP_TRACE_ENABLED) {
    			System.out.println(">>> Send MSRP message:\n" + new String(chunk));
    		}
    		writeData(chunk);
	    } finally {
	    	// Changed by Deutsche Telekom
	        //CpuManager.releaseTempLock();
	    }
	}
	
	/**
	 * 将数据写到流中
	 * 
	 * @param chunk 数据块
	 * @throws IOException
	 */
	private synchronized void writeData(byte chunk[]) throws IOException {
		// Changed by Deutsche Telekom
	    //CpuManager.setTempLock();
	    try {
	        stream.write(chunk);
	        stream.flush();
	    } finally {
	    	// Changed by Deutsche Telekom
	        //CpuManager.releaseTempLock();
	    }
	}
}
