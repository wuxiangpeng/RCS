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
 * ���ݷ�����
 * 
 * @author jexa7410
 */
public class ChunkSender extends Thread {
	/**
	 * MSRP ����
	 */
	private MsrpConnection connection;

	/**
	 * MSRP �����
	 */
	private OutputStream stream;
	
	/**
	 * ���ݿ黺����
	 */
	private FifoBuffer buffer = new FifoBuffer();

	/**
	 * ������־
	 */
	private boolean terminated = false;

	/**
	 * ��־��¼
	 */
	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * ���캯��
	 * 
	 * @param connection MSRP ����
	 * @param stream TCP �����
	 */
	public ChunkSender(MsrpConnection connection, OutputStream stream) {
		this.connection = connection;
		this.stream = stream;
	}	
	
	/**
	 * ��ȡ MSRP ����
	 * 
	 * @return MSRP ����
	 */
	public MsrpConnection getConnection() {
		return connection;
	}
	
	/**
	 * ��ֹ��������
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
	 * run����
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
	 * ����һ�����ݿ�
	 * 
	 * @param chunk �����ݿ�
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
	 * ��������һ�����ݿ�
	 * 
	 * @param chunk ���ݿ�
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
	 * ������д������
	 * 
	 * @param chunk ���ݿ�
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
