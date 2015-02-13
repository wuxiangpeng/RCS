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
 * MSRP ������
 * 
 * @author jexa7410
 */
public class MsrpManager {
	/**
     * ���� MSRP ��ַ
     */
    private String localMsrpAddress;

    /**
     * ���� MSRP �˿�
     */
    private int localMsrpPort;

    /**
     * MSRP �Ự
     */
    private MsrpSession msrpSession = null;
    
    /**
     * �Ự Id
     */
    private long sessionId;
   
    /**
     * ��ȫ����
     */
    private boolean secured = false;
    
    /**
     * The logger
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * ���캯��
     * 
     * @param ip��ַ
     * @param �˿ں� 
     */    
    public MsrpManager(String localMsrpAddress, int localMsrpPort) {
    	this.localMsrpAddress = localMsrpAddress;
    	this.localMsrpPort = localMsrpPort;
    	this.sessionId = System.currentTimeMillis();
    }

	// Changed by Deutsche Telekom
    /**
     * ���캯��
     * 
     * @param ip��ַ
     * @param �˿ں�
     * @param Ims������
     */    
    public MsrpManager(String localMsrpAddress, int localMsrpPort, ImsService service) {
    	this(localMsrpAddress, localMsrpPort);
		if (service.getImsModule().isConnectedToWifiAccess()) {
			this.secured =RcsSettings.getInstance().isSecureMsrpOverWifi();
		}
    }

	/**
	 * ���� �˿ں�
	 * 
	 * @return Port number
	 */
	public int getLocalMsrpPort() {
		return localMsrpPort;
	}
    
    /**
     * ��ȡ��������Э�飨��ȫ���ӣ�������ͨ���ӣ�
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
     * ��ȡMSRP URI
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
     * ��ȡ MSRP Э�����ͣ�����msrp��msrps��
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
	 * ���� MSRP�Ự
	 * 
	 * @return MSRP session
	 */
	public MsrpSession getMsrpSession() {
		return msrpSession;
	}

	/**
	 * �ǰ�ȫ������
	 * 
	 * @return Boolean
	 */
	public boolean isSecured() {
		return secured;
	}
	
	/**
	 * ���ð�ȫ����
	 * 
	 * @param flag Boolean flag
	 */
	public void setSecured(boolean flag) {
		this.secured = flag;
	}

	/**
	 * �� MSRP �Ự
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
	 *������
	 * 
	 * @param ��ʱʱ�� (in seconds)
	 * @throws IOException
	 */
	public void openMsrpSession(int timeout) throws IOException {
		if ((msrpSession == null) || (msrpSession.getConnection() == null)) {
			throw new IOException("Session not yet created");
		}

		msrpSession.getConnection().open(timeout);
	}
	
    /**
     * �������ý�����������ͻ�������
     * Զ�� SDP �ظ�.
     * 
     * @param sdp Զ�� SDP �ظ�
     * @param listener Msrp�¼�������
     * @return Msrp�Ự
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
	 * ����һ��MSRP�ͻ��˻Ự
	 * 
	 * @param remoteHost Զ������ip
	 * @param remotePort Զ�� �����˿ں�
     * @param remoteMsrpPath Զ�� ���� MSRP uri
     * @param listener �¼�������
     * @return Created �Ự
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
	 *����һ�� MSRP �������Ự
	 *
     * @param remoteMsrpPath Զ�� ���� MSRP uri
     * @param listener �¼�������
     * @return Created �Ự
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
     * �������ݿ�
     * 
     * @param inputStream ������
     * @param msgId ��Ϣid
     * @param contentType ���ݱ���
     * @param contentSize ���ݴ�С
     * @param typeMsrpChunk MSRP ��������
     * @throws MsrpException
     */
    public void sendChunks(InputStream inputStream, String msgId, String contentType, long contentSize, TypeMsrpChunk typeMsrpChunk) throws MsrpException {
        if (msrpSession == null) {
        	throw new MsrpException("MSRP session is null");
        }

        msrpSession.sendChunks(inputStream, msgId, contentType, contentSize, typeMsrpChunk);
    }
    
    /**
     * ���ÿյ����ݿ�
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
     * �ر� MSRP �Ự
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
