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
import java.util.Vector;

import com.cmcc.rcs.core.ims.protocol.msrp.MsrpSession.TypeMsrpChunk;
import com.cmcc.rcs.core.ims.service.ImsService;
import com.cmcc.rcs.provider.settings.RcsSettings;
import com.cmcc.rcs.utils.IpAddressUtils;
import com.cmcc.rcs.utils.logger.Logger;

/**
 * MSRP 管理器
 * 
 * @author jexa7410
 */
public class MsrpManager {
	/**
     * 本地 MSRP 地址
     */
    private String localMsrpAddress;

    /**
     * 本地 MSRP 端口
     */
    private int localMsrpPort;

    /**
     * MSRP 会话
     */
    private MsrpSession msrpSession = null;
    
    /**
     * 会话 Id
     */
    private long sessionId;
   
    /**
     * 安全连接
     */
    private boolean secured = false;
    
    /**
     * The logger
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * 构造函数
     * 
     * @param ip地址
     * @param 端口号 
     */    
    public MsrpManager(String localMsrpAddress, int localMsrpPort) {
    	this.localMsrpAddress = localMsrpAddress;
    	this.localMsrpPort = localMsrpPort;
    	this.sessionId = System.currentTimeMillis();
    }

	// Changed by Deutsche Telekom
    /**
     * 构造函数
     * 
     * @param ip地址
     * @param 端口号
     * @param Ims服务器
     */    
    public MsrpManager(String localMsrpAddress, int localMsrpPort, ImsService service) {
    	this(localMsrpAddress, localMsrpPort);
		if (service.getImsModule().isConnectedToWifiAccess()) {
			this.secured =RcsSettings.getInstance().isSecureMsrpOverWifi();
		}
    }

	/**
	 * 返回 端口号
	 * 
	 * @return Port number
	 */
	public int getLocalMsrpPort() {
		return localMsrpPort;
	}
    
    /**
     * 获取本地连接协议（安全连接，还是普通连接）
     * 
     * @return Protocol
     */
    public String getLocalSocketProtocol() {
    	if (secured) {
    		return MsrpConstants.SOCKET_MSRP_SECURED_PROTOCOL;
    	} else {
    		return MsrpConstants.SOCKET_MSRP_PROTOCOL;
    	}
    }

	/**
     * 获取MSRP URI
     * 
     * @return MSRP path
     */
    public String getLocalMsrpPath() {
        if (IpAddressUtils.isIPv6(localMsrpAddress)) {
            return getMsrpProtocol() + "://[" + localMsrpAddress + "]:" + localMsrpPort + "/" + sessionId + ";tcp";
        } else {
            return getMsrpProtocol() + "://" + localMsrpAddress + ":" + localMsrpPort + "/" + sessionId + ";tcp";
        }
    }
    
    /**
     * 获取 MSRP 协议类型（返回msrp或msrps）
     * 
     * @return MSRP protocol
     */
    public String getMsrpProtocol() {
    	if (secured) {
    		return MsrpConstants.MSRP_SECURED_PROTOCOL;
    	} else {
    		return MsrpConstants.MSRP_PROTOCOL;
    	}
    }
    
    /**
	 * 返回 MSRP会话
	 * 
	 * @return MSRP session
	 */
	public MsrpSession getMsrpSession() {
		return msrpSession;
	}

	/**
	 * 是安全连接吗？
	 * 
	 * @return Boolean
	 */
	public boolean isSecured() {
		return secured;
	}
	
	/**
	 * 设置安全连接
	 * 
	 * @param flag Boolean flag
	 */
	public void setSecured(boolean flag) {
		this.secured = flag;
	}

	/**
	 * 打开 MSRP 会话
	 * 
	 * @throws IOException
	 */
	public void openMsrpSession() throws IOException {
		if ((msrpSession == null) || (msrpSession.getConnection() == null)) {
			throw new IOException("Session not yet created");
		}
		
		msrpSession.getConnection().open();
	}
	
	/**
	 *打开链接
	 * 
	 * @param 超时时间 (in seconds)
	 * @throws IOException
	 */
	public void openMsrpSession(int timeout) throws IOException {
		if ((msrpSession == null) || (msrpSession.getConnection() == null)) {
			throw new IOException("Session not yet created");
		}

		msrpSession.getConnection().open(timeout);
	}
	
    /**
     * 根据设置建立服务器或客户端连接
     * 远程 SDP 回复.
     * 
     * @param sdp 远程 SDP 回复
     * @param listener Msrp事件监听器
     * @return Msrp会话
     * @throws MsrpException
    public MsrpSession createMsrpSession(byte[] sdp, MsrpEventListener listener)
            throws MsrpException {
        SdpParser parser = new SdpParser(sdp);

        Vector<MediaDescription> media = parser.getMediaDescriptions();
        MediaDescription mediaDesc = media.elementAt(0);
        MediaAttribute pathAttribute = mediaDesc.getMediaAttribute("path");
        String remoteMsrpPath = pathAttribute.getValue();

        // Create the MSRP session
        MsrpSession session = null;
        MediaAttribute setupAttribute = mediaDesc.getMediaAttribute("setup");
        String setup = null;
        if (setupAttribute != null) {
            setup = setupAttribute.getValue();
        } else {
            logger.error("Media attribute \"setup\" is missing!");
            logger.warn("media="+mediaDesc.toString());
            if (mediaDesc.mediaAttributes != null)
            for (MediaAttribute attribute :  mediaDesc.mediaAttributes) {
            	 logger.warn("attribute key="+attribute.getName()+" value="+attribute.getValue());
			}
			
        }
        // if remote peer is active this client needs to be passive (i.e. act as server)
        if ("active".equalsIgnoreCase(setup)) {
            session = createMsrpServerSession(remoteMsrpPath, listener);
        } else {
            String remoteHost = SdpUtils.extractRemoteHost(parser.sessionDescription, mediaDesc);
            int remotePort = mediaDesc.port;
            String fingerprint = SdpUtils.extractFingerprint(parser, mediaDesc);
            session = createMsrpClientSession(remoteHost, remotePort, remoteMsrpPath, listener,
                    fingerprint);
        }

        return session;
    }
     */
    
	/**
	 * 创建一个MSRP客户端会话
	 * 
	 * @param remoteHost 远程主机ip
	 * @param remotePort 远程 主机端口号
     * @param remoteMsrpPath 远程 主机 MSRP uri
     * @param listener 事件监听器
     * @return Created 会话
	 * @throws MsrpException
	 */
	public MsrpSession createMsrpClientSession(String remoteHost, int remotePort, String remoteMsrpPath, MsrpEventListener listener, String fingerprint) throws MsrpException {
        try {
	        if (logger.isActivated()) {
				logger.info("Create MSRP client end point at " + remoteHost + ":" + remotePort);
			}
	
			// Create a new MSRP session
			msrpSession = new MsrpSession();
			msrpSession.setFrom(getLocalMsrpPath());
			msrpSession.setTo(remoteMsrpPath);

			// Create a MSRP client connection
			// Changed by Deutsche Telekom
			MsrpConnection connection = new MsrpClientConnection(msrpSession, remoteHost, remotePort, secured, fingerprint);

			// Associate the connection to the session
			msrpSession.setConnection(connection);
			
			// Add event listener
			msrpSession.addMsrpEventListener(listener);
			
        	// Return the created session
        	return msrpSession;
		} catch(Exception e) {
			if (logger.isActivated()) {
				logger.error("Can't create the MSRP client session", e);
			}
			throw new MsrpException("Create MSRP client session has failed");
		}
	}
	
	/**
	 *创建一个 MSRP 服务器会话
	 *
     * @param remoteMsrpPath 远程 主机 MSRP uri
     * @param listener 事件监听器
     * @return Created 会话
	 * @throws MsrpException
	 */
	public MsrpSession createMsrpServerSession(String remoteMsrpPath, MsrpEventListener listener) throws MsrpException {
		if (logger.isActivated()) {
			logger.info("Create MSRP server end point at " + localMsrpPort);
		}

		// Create a MSRP session
		msrpSession = new MsrpSession();
		msrpSession.setFrom(getLocalMsrpPath());
		msrpSession.setTo(remoteMsrpPath);

		// Create a MSRP server connection
		MsrpConnection connection = new MsrpServerConnection(msrpSession, localMsrpPort);

		// Associate the connection to the session
		msrpSession.setConnection(connection);
		
		// Add event listener
		msrpSession.addMsrpEventListener(listener);

    	// Return the created session
    	return msrpSession;
	}

	// Changed by Deutsche Telekom
	/**
     * 设置数据块
     * 
     * @param inputStream 输入流
     * @param msgId 消息id
     * @param contentType 数据编码
     * @param contentSize 数据大小
     * @param typeMsrpChunk MSRP 数据类型
     * @throws MsrpException
     */
    public void sendChunks(InputStream inputStream, String msgId, String contentType, long contentSize, TypeMsrpChunk typeMsrpChunk) throws MsrpException {
        if (msrpSession == null) {
        	throw new MsrpException("MSRP session is null");
        }

        msrpSession.sendChunks(inputStream, msgId, contentType, contentSize, typeMsrpChunk);
    }
    
    /**
     * 设置空的数据块
     * 
     * @throws MsrpException
     */
    public void sendEmptyChunk() throws MsrpException {
        if (msrpSession == null) {
        	throw new MsrpException("MSRP session is null");
        }

		msrpSession.sendEmptyChunk();
    }
    
    /**
     * 关闭 MSRP 会话
     */
    public synchronized void closeSession() {
        if (msrpSession != null) {
        	if (logger.isActivated()) {
        		logger.info("Close the MSRP session");
        	}
        	try {
	        	msrpSession.close();
        	} catch(Exception e) {
                // Intentionally blank
        	}
            msrpSession = null;
    	}
    }
}
