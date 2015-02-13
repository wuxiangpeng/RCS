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
import com.cmcc.rcs.platform.network.SocketServerConnection;
import com.cmcc.rcs.utils.logger.Logger;

/**
 * MSRP ����������
 * 
 * @author jexa7410
 */
public class MsrpServerConnection extends MsrpConnection {
	/**
	 * ���� TCP �˿ں�
	 */
	private int localPort; 

    /**
     * ������Socket ����
     */
    private SocketServerConnection socketServer = null;

	/**
	 * ��־��¼
	 */
	private Logger logger = Logger.getLogger(this.getClass().getName());

	/**
	 * ���캯��
	 *
	 * @param session MSRP �Ự
	 * @param localPort �������˿ں�
	 */
	public MsrpServerConnection(MsrpSession session, int localPort) {
		super(session);
		this.localPort = localPort;
	}

	/**
	 * ��ȡsocket����
	 *
	 * @return Socket
	 * @throws IOException
	 */
	@Override
	public SocketConnection getSocketConnection() throws IOException {
		if (logger.isActivated()) {
			logger.debug("Open server socket at " + localPort);
		}
        socketServer = NetworkFactory.getFactory().createSocketServerConnection();
		socketServer.open(localPort);

		if (logger.isActivated()) {
			logger.debug("Wait client connection");
		}

		SocketConnection socket = socketServer.acceptConnection();
		if (logger.isActivated()) {
			logger.debug("Socket connected to " + socket.getRemoteAddress() + ":" + socket.getRemotePort());
		}
		return socket;
	}

    /**
     * �ر�����
     */
    @Override
	public void close() {
        super.close();
        
        try {
            if (socketServer != null) {
                socketServer.close();
            }
        } catch (IOException e) {
            // Nothing to do
        }
    }
}
