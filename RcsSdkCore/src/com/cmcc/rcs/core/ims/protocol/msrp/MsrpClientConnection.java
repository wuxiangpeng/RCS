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
 * MSRP 客户端连接
 * 
 * @author jexa7410
 */
public class MsrpClientConnection extends MsrpConnection {
	/**
	 * 服务器 IP地址 
	 */
	private String remoteAddress;
	
	/**
	 *服务器 TCP连接端口号
	 */
	private int remotePort;
	
    /**
     * 安全连接
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
	 * 构造函数
	 * 
	 * @param MSRP 会话
	 * @param 服务器ip地址
	 * @param 服务器端口号
	 */
	public MsrpClientConnection(MsrpSession session, String remoteAddress, int remotePort) {
		super(session);
		
		this.remoteAddress = remoteAddress;
		this.remotePort = remotePort;
	}
	
	/**
	 * 构造函数
	 * 
	 * @param MSRP 会话
	 * @param 服务器ip地址
	 * @param 服务器端口号
	 * @param 安全连接flag
	 */
	public MsrpClientConnection(MsrpSession session, String remoteAddress, int remotePort, boolean secured) {
		this(session, remoteAddress, remotePort);
		
		this.secured = secured;
	}
	
	// Changed by Deutsche Telekom
	/**
	 * 构造函数
	 * 
	 * @param MSRP 会话
	 * @param 服务器ip地址
	 * @param 服务器端口号
	 * @param 安全连接flag
	 * @param fingerprint fingerprint announced in SDP
	 */
	public MsrpClientConnection(MsrpSession session, String remoteAddress, int remotePort, boolean secured, String fingerprint) {
		this(session, remoteAddress, remotePort);
		
		this.secured = secured;
		this.announcedFingerprint = fingerprint;
	}
	
	/**
	 * 是否安全连接
	 * 
	 * @return Boolean
	 */
	public boolean isSecured() {
		return secured;
	}

	/**
	 * 返回 socket连接
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
