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
import java.io.InputStream;
import java.io.OutputStream;

import com.cmcc.rcs.platform.network.SocketConnection;
import com.cmcc.rcs.utils.logger.Logger;

/**
 * MSRP ���ӳ�����
 * 
 * @author jexa7410
 */
public abstract class MsrpConnection {
	/**
	 * MSRP ����boolean����
	 */
	public static boolean MSRP_TRACE_ENABLED = false;
	
	/**
	 * MSRP �Ự
	 */
	private MsrpSession session;
	
	/**
	 * Socket ����
	 */
	private SocketConnection socket = null;

	/**
	 * Socket �����
	 */
	private OutputStream outputStream = null;

	/**
	 * Socket ������
	 */
	private InputStream inputStream = null;
	
	/**
	 * ���ݿ����
	 */
	private ChunkReceiver receiver;

	/**
	 * ���ݿ鷢��
	 */
	private ChunkSender sender;

	/**
	 * ��־��¼
	 */
	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * ���캯��
	 * 
	 * @param session MSRP �Ự
	 */
	public MsrpConnection(MsrpSession session) {
		this.session = session;
	}

	/**
	 * ���� MSRP �Ự��ϵ MSRP����
	 * 
	 * @return MSRP �Ự
	 */
	public MsrpSession getSession() {
		return session; 
	}
	
	/**
	 * ������
	 * 
	 * @throws IOException
	 */
	public void open() throws IOException {
		// Open socket connection
		socket = getSocketConnection();

		// Open I/O stream
		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();
		
		// Create the chunk receiver
		receiver = new ChunkReceiver(this, inputStream);
		receiver.start();

		// Create the chunk sender
		sender = new ChunkSender(this, outputStream);
		sender.start();

		if (logger.isActivated()) {
			logger.debug("Connection has been openned");
		}
	}
	
	/**
	 * ������
	 * 
	 * @param ��ʱʱ��(��)
	 * @throws IOException
	 */
	public void open(int timeout) throws IOException {
		// Open socket connection
		socket = getSocketConnection();

		// Set SoTimeout
		socket.setSoTimeout(timeout*1000);
		
		// Open I/O stream
		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();
		
		// Create the chunk receiver
		receiver = new ChunkReceiver(this, inputStream);
		receiver.start();

		// Create the chunk sender
		sender = new ChunkSender(this, outputStream);
		sender.start();

		if (logger.isActivated()) {
			logger.debug("Connection has been openned");
		}
	}

	/**
	 *�ر�����
	 */
	public void close() {
		// Terminate chunk sender
		if (sender != null) {
			sender.terminate();
		}
		
		// Terminate chunk receiver
		if (receiver != null) {
			receiver.terminate();
		}

		// Close socket connection
		try {
			if (logger.isActivated()) {
				logger.debug("Close the socket connection");
			}
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (Exception e) {
			if (logger.isActivated()) {
				logger.error("Can't close the socket correctly", e);
			}
		}
		
		if (logger.isActivated()) {
			logger.debug("Connection has been closed");
		}
	}
	
	/**
	 * ����һ���µ����ݿ�
	 * 
	 * @param chunk ���ݿ�
	 * @throws IOException
	 */
	public void sendChunk(byte chunk[]) throws IOException {
		sender.sendChunk(chunk);
	}	

	/**
	 * ���̷���һ���µ����ݿ�
	 * 
	 * @param chunk ���ݿ�
	 * @throws IOException
	 */
	public void sendChunkImmediately(byte chunk[]) throws IOException {
		sender.sendChunkImmediately(chunk);
	}
	
	/**
	 * ����socket ����
	 * 
	 * @return Socket
	 * @throws IOException
	 */
	public abstract SocketConnection getSocketConnection() throws IOException;
}
