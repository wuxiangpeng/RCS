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

import com.cmcc.rcs.platform.network.NetworkFactory;
import com.cmcc.rcs.platform.network.SocketConnection;
import com.cmcc.rcs.utils.logger.Logger;

/**
 * MSRP �ͻ�������
 * 
 * @author jexa7410
 */
public class MsrpClientConnection extends MsrpConnection {
	/**
	 * ������ IP��ַ 
	 */
	private String remoteAddress;
	
	/**
	 *������ TCP���Ӷ˿ں�
	 */
	private int remotePort;
	
    /**
     * ��ȫ����
     */
    private boolean secured = false;

    // Changed by Deutsche Telekom
    /**
     * Secured connection
     */
    private String announcedFingerprint = null;

	/**
	 * The logger
	 */
	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * ���캯��
	 * 
	 * @param MSRP �Ự
	 * @param ������ip��ַ
	 * @param �������˿ں�
	 */
	public MsrpClientConnection(MsrpSession session, String remoteAddress, int remotePort) {
		super(session);
		
		this.remoteAddress = remoteAddress;
		this.remotePort = remotePort;
	}
	
	/**
	 * ���캯��
	 * 
	 * @param MSRP �Ự
	 * @param ������ip��ַ
	 * @param �������˿ں�
	 * @param ��ȫ����flag
	 */
	public MsrpClientConnection(MsrpSession session, String remoteAddress, int remotePort, boolean secured) {
		this(session, remoteAddress, remotePort);
		
		this.secured = secured;
	}
	
	// Changed by Deutsche Telekom
	/**
	 * ���캯��
	 * 
	 * @param MSRP �Ự
	 * @param ������ip��ַ
	 * @param �������˿ں�
	 * @param ��ȫ����flag
	 * @param fingerprint fingerprint announced in SDP
	 */
	public MsrpClientConnection(MsrpSession session, String remoteAddress, int remotePort, boolean secured, String fingerprint) {
		this(session, remoteAddress, remotePort);
		
		this.secured = secured;
		this.announcedFingerprint = fingerprint;
	}
	
	/**
	 * �Ƿ�ȫ����
	 * 
	 * @return Boolean
	 */
	public boolean isSecured() {
		return secured;
	}

	/**
	 * ���� socket����
	 * 
	 * @return Socket
	 * @throws IOException
	 */
	@Override
	public SocketConnection getSocketConnection() throws IOException {
		if (logger.isActivated()) {
			logger.debug("Open client socket to " + remoteAddress + ":" + remotePort);
		}
		SocketConnection socket;
		if (secured) {
			// Changed by Deutsche Telekom
			if (this.announcedFingerprint != null) {
				// follow RFC 4572
				// use self-signed certificates
				socket = NetworkFactory.getFactory().createSimpleSecureSocketClientConnection(this.announcedFingerprint);
			} else {
				socket = NetworkFactory.getFactory().createSecureSocketClientConnection();
			}
		} else {
			socket = NetworkFactory.getFactory().createSocketClientConnection();
		}
		socket.open(remoteAddress, remotePort);
		if (logger.isActivated()) {
			logger.debug("Socket connected to " + socket.getRemoteAddress() + ":" + socket.getRemotePort());
		}
		return socket;
	}
}
