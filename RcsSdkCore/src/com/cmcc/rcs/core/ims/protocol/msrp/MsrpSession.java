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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.cmcc.rcs.utils.CloseableUtils;
import com.cmcc.rcs.utils.IdGenerator;
import com.cmcc.rcs.utils.logger.Logger;

/**
 * MSRP �Ự
 * 
 * @author jexa7410
 */
public class MsrpSession {
    
    // Changed by Deutsche Telekom
    /**
     * ��ʱʱ��
     */
    private static final int TRANSACTION_INFO_EXPIRY_PERIOD = 30;

    // Changed by Deutsche Telekom
    /**
     * MSRP ��������
     */
    public enum TypeMsrpChunk {
        TextMessage,
        IsComposing,
        MessageDisplayedReport,
        MessageDeliveredReport,
        OtherMessageDeliveredReportStatus,
        FileSharing,
        HttpFileSharing,
        ImageTransfer,
        EmptyChunk,
        GeoLocation,
        StatusReport,
        Unknown
    }
    
    // Changed by Deutsche Telekom
    /**
     * MSRP �������
     */
    private class MsrpTransactionInfo {
        public String transactionId = null;
        public String msrpMsgId = null;
        public String cpimMsgId = null;
        public TypeMsrpChunk typeMsrpChunk = TypeMsrpChunk.Unknown;
        private long timestamp = System.currentTimeMillis();
        
        /**
         * MSRP ��������췽��
         * 
         * @param transactionId MSRP ����id
         * @param msrpMsgId MSRP ��Ϣ ID
         * @param cpimMsgId CPIM ��Ϣ ID
         * @param typeMsrpChunk MSRP ��������(see {@link TypeMsrpChunk})
         */
        public MsrpTransactionInfo(String transactionId, String msrpMsgId, String cpimMsgId, TypeMsrpChunk typeMsrpChunk) {
            this.transactionId = transactionId;
            this.msrpMsgId = msrpMsgId;
            this.cpimMsgId = cpimMsgId;
            this.typeMsrpChunk = typeMsrpChunk;
            this.timestamp = System.currentTimeMillis();
        }
        
        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("[MsrpTransactionInfo - ");
            sb.append("transactionId = ").append(transactionId).append(", ");
            sb.append("msrpMsgId = ").append(msrpMsgId).append(", ");
            sb.append("cpimMsgId = ").append(cpimMsgId).append(", ");
            sb.append("typeMsrpChunk = ").append(typeMsrpChunk).append(", ");
            sb.append("timestamp = ").append(timestamp);
            sb.append("]");
            return sb.toString();
        }
    }
    
	/**
	 * ���󱨸�ѡ��
	 */
	private boolean failureReportOption = false;

	/**
	 * �ɹ� ����ѡ��
	 */
	private boolean successReportOption = false;

	/**
	 * MSRP����
	 */
	private MsrpConnection connection = null;
	
	/**
	 * ԭ��ַ
	 */
	private String from = null;
	
	/**
	 * Ŀ���ַ
	 */
	private String to = null;
	
	/**
	 * ȡ������ flag
	 */
	private boolean cancelTransfer = false;

	/**
	 * ��������
	 */
	private RequestTransaction requestTransaction = null;

	/**
	 * ��������
	 */
	private DataChunks receivedChunks = new DataChunks();	
	
    /**
     * MSRP�¼�����
     */
    private MsrpEventListener msrpEventListener = null;

    /**
     * �����������
     */
	private static Random random = new Random(System.currentTimeMillis());

	/**
	 * ��������
	 */
	private ReportTransaction reportTransaction = null;

    /**
     * MSRP����
     */
    private MsrpTransaction msrpTransaction = null;

    /**
     * �ļ����������
     */
    private Vector<Long> progress = new Vector<Long>();

    /**
     * �ļ��������
     */
    private long totalSize;
    
    /**
	 * ��־
	 */
	private Logger logger = Logger.getLogger(this.getClass().getName());

    // Changed by Deutsche Telekom
    /**
     * ����˵�� ���
     */
    private ConcurrentHashMap<String, MsrpTransactionInfo> mTransactionInfoMap = null;

    // Changed by Deutsche Telekom
    /**
     * Mapping of messages to transactions 
     */
    private ConcurrentHashMap<String, String> mMessageTransactionMap = null;

    // Changed by Deutsche Telekom
    /**
     * Transaction info table locking object
     */
    private Object mTransactionMsgIdMapLock = new Object();
    
    // Changed by Deutsche Telekom
    /**
     * Controls if is to map the msgId from transactionId if not present on received MSRP messages
     */
    private boolean mMapMsgIdFromTransationId = false;
    
	/**
	 * ���캯��
	 */
	public MsrpSession() {
	    // Changed by Deutsche Telekom
	    setMapMsgIdFromTransationId(true);
	}
	
	// Changed by Deutsche Telekom
    /**
     * ����ʵ��
     */
    @Override
    protected void finalize() throws Throwable {
        setMapMsgIdFromTransationId(false);

        super.finalize();
    }
	
	/**
	 * ��������id
	 * 
	 * @return ID
	 */
	private static synchronized String generateTransactionId() {
		return Long.toHexString(random.nextLong());		
	}
	
	/**
	 *�ж������Ƿ�ʧ��
	 * 
	 * @return Boolean
	 */
	public boolean isFailureReportRequested() {
		return failureReportOption;
	}

	/**
	 * ����ʧ�ܱ���ѡ��
	 * 
	 * @param failureReportOption Boolean flag
	 */
	public void setFailureReportOption(boolean failureReportOption) {
		this.failureReportOption = failureReportOption;
	}

	/**
	 * �ж������Ƿ�ɹ�
	 * 
	 * @return Boolean
	 */
	public boolean isSuccessReportRequested() {
		return successReportOption;
	}

	/**
	 * ���óɹ�����ѡ��
	 * 
	 * @param successReportOption Boolean flag
	 */
	public void setSuccessReportOption(boolean successReportOption) {
		this.successReportOption = successReportOption;
	}	

	/**
	 * ����MSRP ����
	 * 
	 * @param connection MSRP ����
	 */
	public void setConnection(MsrpConnection connection) {
		this.connection = connection;
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
	 * ��ȡ MSRP �¼�������
	 * 
	 * @return Listener 
	 */
	public MsrpEventListener getMsrpEventListener() {
		return msrpEventListener;
	}
	
	/**
	 * ���MSRP�¼�����
	 * 
	 * @param listener Listener 
	 */
	public void addMsrpEventListener(MsrpEventListener listener) {
		this.msrpEventListener = listener;		
	}
	
	/**
	 * ��ȡ������uri
	 * 
	 * @return From path
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * ���÷�����uri
	 * 
	 * @param from From path
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * ��ȡ������uri
	 *  
	 * @return To path
	 */
	public String getTo() {
		return to;
	}

	/**
	 * ���ý�����uri
	 * 
	 * @param to To path
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 *�رջỰ
	 */
	public void close() {
		if (logger.isActivated()) {
			logger.debug("Close session");
		}

		// Cancel transfer
		cancelTransfer = true;

		// Close the connection
		if (connection != null) {
			connection.close();
		}
		
		// Unblock request transaction
		if (requestTransaction != null) {
			requestTransaction.terminate();
		}

		// Unblock report transaction
		if (reportTransaction != null) {
			reportTransaction.terminate();
		}

        // Unblock MSRP transaction
        if (msrpTransaction != null) {
            msrpTransaction.terminate();
        }
	}

	// Changed by Deutsche Telekom
	/**
	 * ��������
	 * 
	 * @param inputStream ������
	 * @param msgId ��Ϣid
	 * @param contentType ������������
	 * @param totalSize ���ݴ�С
	 * @param typeMsrpChunk MSRP ��������
	 * @throws MsrpException
	 */
	public void sendChunks(InputStream inputStream, String msgId, String contentType, final long totalSize, TypeMsrpChunk typeMsrpChunk) throws MsrpException {
		if (logger.isActivated()) {
			logger.info("Send content (" + contentType + " - MSRP chunk type: " + typeMsrpChunk + ")");
		}

		if (from == null) {
			throw new MsrpException("From not set");
		}
		
		if (to == null) {
			throw new MsrpException("To not set");
		}
		
		if (connection == null) {
			throw new MsrpException("No connection set");
		}
		
		this.totalSize = totalSize;
		
		// Changed by Deutsche Telekom
        //CpuManager.setTempLock();
		// Send content over MSRP 
		try {
			byte data[] = new byte[MsrpConstants.CHUNK_MAX_SIZE];
			long firstByte = 1;
			long lastByte = 0;
			cancelTransfer = false;
			if (successReportOption) {
				reportTransaction = new ReportTransaction();
			} else {
				reportTransaction = null;
			}
            if (failureReportOption) {
                msrpTransaction = new MsrpTransaction();
            } else {
                msrpTransaction = null;
            }

            // Changed by Deutsche Telekom
            // Calculate number of needed chunks
            final int totalChunks = (int) Math.ceil(totalSize / (double) MsrpConstants.CHUNK_MAX_SIZE);
            
            Thread updater = new Thread(new Runnable() {

                @Override
                public void run() {

                    // Changed by Deutsche Telekom
                    //CpuManager.setTempLock();
                    try {
                        if (msrpTransaction != null) {
                            while ((totalChunks - msrpTransaction.getNumberReceivedOk()) > 0 && !cancelTransfer) {
                                msrpEventListener.msrpTransferProgress(msrpTransaction.getNumberReceivedOk()
                                        * MsrpConstants.CHUNK_MAX_SIZE, totalSize);
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } finally {
                        // Changed by Deutsche Telekom
                        //CpuManager.releaseTempLock();
                    }
                }

            });
            updater.start();
            
            // Changed by Deutsche Telekom
            String newTransactionId = null;

            // Changed by Deutsche Telekom
            // RFC4975, section 7.1.1.  Sending SEND Requests
            // When an endpoint has a message to deliver, it first generates a new
            // Message-ID.  The value MUST be highly unlikely to be repeated by
            // another endpoint instance, or by the same instance in the future.
            // Message-ID value follows the definition in RFC4975, section 9
    		String msrpMsgId = IdGenerator.generateMessageID();

            // Send data chunk by chunk
			for (int i = inputStream.read(data); (!cancelTransfer) & (i>-1); i=inputStream.read(data)) {
				// Update upper byte range
				lastByte += i;

				// Changed by Deutsche Telekom
				newTransactionId = generateTransactionId();
				addMsrpTransactionInfo(newTransactionId, msrpMsgId, msgId, typeMsrpChunk);
				
				// Send a chunk
				// Changed by Deutsche Telekom
				sendMsrpSendRequest(newTransactionId, to, from, msrpMsgId, contentType, i, data, firstByte, lastByte, totalSize);

				// Update lower byte range
				firstByte += i;

				// Progress management
                if (failureReportOption) {
                    // Add value in progress vector 
                    progress.add(lastByte);
                } else {
                    // Direct notification
                    if (!cancelTransfer) {
                        msrpEventListener.msrpTransferProgress(lastByte, totalSize);
                    }
                }
			}
			
			if (cancelTransfer) {
				// Transfer has been aborted
				return;
			}

            // Waiting msrpTransaction
            if (msrpTransaction != null) {
                // Wait until all data have been reported
                msrpTransaction.waitAllResponses();

                // Notify event listener
                if (msrpTransaction.isAllResponsesReceived()) {
                    msrpEventListener.msrpDataTransfered(msgId);
                } else {
                    if (!msrpTransaction.isTerminated()) {
                        // Changed by Deutsche Telekom
                        msrpEventListener.msrpTransferError(msgId, "response timeout 408", typeMsrpChunk);
                    }
                }
            }

            // Waiting reportTransaction
            if (reportTransaction != null) {
                // Wait until all data have been reported
                while(!reportTransaction.isTransactionFinished(totalSize)) {
                    reportTransaction.waitReport();
                    if (reportTransaction.getStatusCode() != 200) {
                        // Error
                        break;
                    }
                }

                // Notify event listener
                if (reportTransaction.getStatusCode() == 200) {
                    msrpEventListener.msrpDataTransfered(msgId);
                } else {
                    // Changed by Deutsche Telekom
                    msrpEventListener.msrpTransferError(msgId, "error report " + reportTransaction.getStatusCode(), typeMsrpChunk);
                }
            }

            // No transaction
            if (msrpTransaction == null && reportTransaction == null) {
                // Notify event listener
                msrpEventListener.msrpDataTransfered(msgId);
			}
		} catch(Exception e) {
			if (logger.isActivated()) {
				logger.error("Send chunk failed", e);
			}
			throw new MsrpException(e.getMessage());
		} finally {
			CloseableUtils.close(inputStream);
            // Changed by Deutsche Telekom
	        //CpuManager.releaseTempLock();
		}
	}

	/**
	 * ���Ϳ�����
	 * 
	 * @throws MsrpException
	 */
	public void sendEmptyChunk() throws MsrpException {
		if (logger.isActivated()) {
			logger.info("Send an empty chunk");
		}
		
		if (from == null) {
			throw new MsrpException("From not set");
		}
		
		if (to == null) {
			throw new MsrpException("To not set");
		}
		
		if (connection == null) {
			throw new MsrpException("No connection set");
		}
		
		// Changed by Deutsche Telekom
		//CpuManager.setTempLock();
		// Send an empty chunk
		try {
            // Changed by Deutsche Telekom
            String newTransactionId = generateTransactionId();
            String newMsgId = generateTransactionId();
            addMsrpTransactionInfo(newTransactionId, newMsgId, null, TypeMsrpChunk.EmptyChunk);
			sendEmptyMsrpSendRequest(newTransactionId, to, from, newMsgId);
		} catch(MsrpException e) {
			throw e;
		} catch(Exception e) {
			throw new MsrpException(e.getMessage());
		} finally {
			// Changed by Deutsche Telekom
		    //CpuManager.releaseTempLock();
		}
	}

	/**
	 * Send MSRP ��������
	 * 
	 * @param txId ���� ID
	 * @param to ������
	 * @param from ������
	 * @param msrpMsgId MSRP ��Ϣ ID
	 * @param contentType �������� 
	 * @param dataSize ���ݴ�С
	 * @param data ����
	 * @param firstByte ���ݿ����ֽ�һ��Ϊ1
	 * @param lastByte ���ݿ�ĩ�ֽ�
	 * @param totalSize ���������ܹ���С
	 * @throws IOException 
	 * @throws MsrpException
	 */
    // Changed by Deutsche Telekom
	private void sendMsrpSendRequest(String txId, String to, String from, String msrpMsgId, String contentType,
			int dataSize, byte data[], long firstByte, long lastByte, long totalSize) throws MsrpException, IOException {
		// Changed by Deutsche Telekom
        //CpuManager.setTempLock();
        try {
    		boolean isLastChunk = (lastByte == totalSize);
    		
    		// Create request
    		ByteArrayOutputStream buffer = new ByteArrayOutputStream(4000);
    		buffer.reset();
    		buffer.write(MsrpConstants.MSRP_HEADER.getBytes());
    		buffer.write(MsrpConstants.CHAR_SP);
    		buffer.write(txId.getBytes());
    		buffer.write((" " + MsrpConstants.METHOD_SEND).getBytes());
    		buffer.write(MsrpConstants.NEW_LINE.getBytes());
    
    		String toHeader = MsrpConstants.HEADER_TO_PATH + ": " + to + MsrpConstants.NEW_LINE;
    		buffer.write(toHeader.getBytes());
    		String fromHeader = MsrpConstants.HEADER_FROM_PATH + ": " + from + MsrpConstants.NEW_LINE;
    		buffer.write(fromHeader.getBytes());
            // Changed by Deutsche Telekom
    		String msgIdHeader = MsrpConstants.HEADER_MESSAGE_ID + ": " + msrpMsgId + MsrpConstants.NEW_LINE;
    		buffer.write(msgIdHeader.getBytes());
    		
    		// Write byte range
    		String byteRange = MsrpConstants.HEADER_BYTE_RANGE + ": " + firstByte + "-" + lastByte + "/" + totalSize + MsrpConstants.NEW_LINE;
    		buffer.write(byteRange.getBytes());
    		
    		// Write optional headers
            // Changed by Deutsche Telekom
            // According with GSMA guidelines
            if (failureReportOption) {
                String header = MsrpConstants.HEADER_FAILURE_REPORT + ": yes" + MsrpConstants.NEW_LINE;
                buffer.write(header.getBytes());
            }
    		if (successReportOption) {
    			String header = MsrpConstants.HEADER_SUCCESS_REPORT + ": yes" + MsrpConstants.NEW_LINE;
    			buffer.write(header.getBytes());
    		}
    
    		// Write content type
    		if (contentType != null) {
    			String content = MsrpConstants.HEADER_CONTENT_TYPE + ": " + contentType + MsrpConstants.NEW_LINE; 
    			buffer.write(content.getBytes());
    		}		
    
    		// Write data
    		if (data != null) {
    			buffer.write(MsrpConstants.NEW_LINE.getBytes());
    			buffer.write(data, 0, dataSize);
    			buffer.write(MsrpConstants.NEW_LINE.getBytes());
    		}
    		
    		// Write end of request
    		buffer.write(MsrpConstants.END_MSRP_MSG.getBytes());
    		buffer.write(txId.getBytes());
    		if (isLastChunk) {
    			// '$' -> last chunk
    			buffer.write(MsrpConstants.FLAG_LAST_CHUNK);
    		} else {
    			// '+' -> more chunk
    			buffer.write(MsrpConstants.FLAG_MORE_CHUNK);
    		}
    		buffer.write(MsrpConstants.NEW_LINE.getBytes());
    		
    		// Send chunk
    		if (failureReportOption) {
                if (msrpTransaction != null) {
                    msrpTransaction.handleRequest();
                    requestTransaction = null;
                } else {
                    requestTransaction = new RequestTransaction();
                }
    			connection.sendChunk(buffer.toByteArray());
    			buffer.close();
                if (requestTransaction != null) {
                    requestTransaction.waitResponse();
                    if (!requestTransaction.isResponseReceived()) {
                        throw new MsrpException("timeout");
                    }
                }
    		} else {
    			connection.sendChunk(buffer.toByteArray());
    			buffer.close();
                if (msrpTransaction != null) {
                    msrpTransaction.handleRequest();
                }
    		}
        } finally {
        	// Changed by Deutsche Telekom
            //CpuManager.releaseTempLock();
        }
	}
	
	/**
	 *���Ϳյ�MSRP SEND ����
	 * 
	 * @param txId ���� ID
	 * @param to ������
	 * @param from ������
	 * @param msrpMsgId ��Ϣ ID header
	 * @throws MsrpException
	 * @throws IOException
	 */
    // Changed by Deutsche Telekom
	private void sendEmptyMsrpSendRequest(String txId, String to, String from, String msrpMsgId) throws MsrpException, IOException {
	    // Changed by Deutsche Telekom
	    //CpuManager.setTempLock();
	    try {
    		// Create request
    		ByteArrayOutputStream buffer = new ByteArrayOutputStream(4000);
    		buffer.reset();
    		buffer.write(MsrpConstants.MSRP_HEADER.getBytes());
    		buffer.write(MsrpConstants.CHAR_SP);
    		buffer.write(txId.getBytes());
    		buffer.write((" " + MsrpConstants.METHOD_SEND).getBytes());
    		buffer.write(MsrpConstants.NEW_LINE.getBytes());
    
    		String toHeader = MsrpConstants.HEADER_TO_PATH + ": " + to + MsrpConstants.NEW_LINE;
    		buffer.write(toHeader.getBytes());
    		String fromHeader = MsrpConstants.HEADER_FROM_PATH + ": " + from + MsrpConstants.NEW_LINE;
    		buffer.write(fromHeader.getBytes());
            // Changed by Deutsche Telekom
    		String msgIdHeader = MsrpConstants.HEADER_MESSAGE_ID + ": " + msrpMsgId + MsrpConstants.NEW_LINE;
    		buffer.write(msgIdHeader.getBytes());
    		
    		// Write end of request
    		buffer.write(MsrpConstants.END_MSRP_MSG.getBytes());
    		buffer.write(txId.getBytes());
    		// '$' -> last chunk
    		buffer.write(MsrpConstants.FLAG_LAST_CHUNK);
    		buffer.write(MsrpConstants.NEW_LINE.getBytes());
    		
    		// Send chunk
    		requestTransaction = new RequestTransaction();
    		connection.sendChunkImmediately(buffer.toByteArray());
    		buffer.close();
    		requestTransaction.waitResponse();
    		if (!requestTransaction.isResponseReceived()) {
    			throw new MsrpException("timeout");
    		}
	    } finally {
	    	// Changed by Deutsche Telekom
	        //CpuManager.releaseTempLock();
	    }
	}

	/**
	 * Send MSRP ��Ӧ
	 * 
	 * @param code ��Ӧ��
	 * @param txId ���� ID
	 * @param headers MSRP ͷ
	 * @throws IOException
	 */
	private void sendMsrpResponse(String code, String txId, Hashtable<String, String> headers) throws IOException {
	    // Changed by Deutsche Telekom
	    //CpuManager.setTempLock();
	    try {
    		ByteArrayOutputStream buffer = new ByteArrayOutputStream(4000);
    		buffer.write(MsrpConstants.MSRP_HEADER .getBytes());
    		buffer.write(MsrpConstants.CHAR_SP);
    		buffer.write(txId.getBytes());
    		buffer.write(MsrpConstants.CHAR_SP);
    		buffer.write(code.getBytes());
    		buffer.write(MsrpConstants.NEW_LINE.getBytes());
    		
    		buffer.write(MsrpConstants.HEADER_TO_PATH.getBytes());
    		buffer.write(MsrpConstants.CHAR_DOUBLE_POINT);
    		buffer.write(MsrpConstants.CHAR_SP);
    		buffer.write((headers.get(MsrpConstants.HEADER_FROM_PATH)).getBytes());
    		buffer.write(MsrpConstants.NEW_LINE.getBytes());
    
    		buffer.write(MsrpConstants.HEADER_FROM_PATH.getBytes());
    		buffer.write(MsrpConstants.CHAR_DOUBLE_POINT);
    		buffer.write(MsrpConstants.CHAR_SP);
    		buffer.write((headers.get(MsrpConstants.HEADER_TO_PATH)).getBytes());
    		buffer.write(MsrpConstants.NEW_LINE.getBytes());
    			
    		buffer.write(MsrpConstants.END_MSRP_MSG.getBytes());
    		buffer.write(txId.getBytes());
    		buffer.write(MsrpConstants.FLAG_LAST_CHUNK);
    		buffer.write(MsrpConstants.NEW_LINE.getBytes());
    		
    		connection.sendChunk(buffer.toByteArray());
    		buffer.close();
	    } finally {
	    	// Changed by Deutsche Telekom
	        //CpuManager.releaseTempLock();
	    }
	}

	/**
	 * Send MSRP REPORT ����
	 * 
	 * @param txId ���� ID
	 * @param headers MSRP ͷ
	 * @throws MsrpException
	 * @throws IOException
	 */
	private void sendMsrpReportRequest(String txId, Hashtable<String, String> headers,
			long lastByte, long totalSize) throws MsrpException, IOException {
	    // Changed by Deutsche Telekom
	    //CpuManager.setTempLock();
	    try {
    		// Create request
    		ByteArrayOutputStream buffer = new ByteArrayOutputStream(4000);
    		buffer.reset();
    		buffer.write(MsrpConstants.MSRP_HEADER.getBytes());
    		buffer.write(MsrpConstants.CHAR_SP);
    		buffer.write(txId.getBytes());
    		buffer.write((" " + MsrpConstants.METHOD_REPORT).getBytes());
    		buffer.write(MsrpConstants.NEW_LINE.getBytes());
    
    		buffer.write(MsrpConstants.HEADER_TO_PATH.getBytes());
    		buffer.write(MsrpConstants.CHAR_DOUBLE_POINT);
    		buffer.write(MsrpConstants.CHAR_SP);
    		buffer.write((headers.get(MsrpConstants.HEADER_FROM_PATH)).getBytes());
    		buffer.write(MsrpConstants.NEW_LINE.getBytes());
    
    		buffer.write(MsrpConstants.HEADER_FROM_PATH.getBytes());
    		buffer.write(MsrpConstants.CHAR_DOUBLE_POINT);
    		buffer.write(MsrpConstants.CHAR_SP);
    		buffer.write((headers.get(MsrpConstants.HEADER_TO_PATH)).getBytes());
    		buffer.write(MsrpConstants.NEW_LINE.getBytes());
    		
    		buffer.write(MsrpConstants.HEADER_MESSAGE_ID.getBytes());
    		buffer.write(MsrpConstants.CHAR_DOUBLE_POINT);
    		buffer.write(MsrpConstants.CHAR_SP);
    		buffer.write((headers.get(MsrpConstants.HEADER_MESSAGE_ID)).getBytes());
    		buffer.write(MsrpConstants.NEW_LINE.getBytes());
    
    		buffer.write(MsrpConstants.HEADER_BYTE_RANGE.getBytes());
    		buffer.write(MsrpConstants.CHAR_DOUBLE_POINT);
    		buffer.write(MsrpConstants.CHAR_SP);
    		String byteRange = "1-" + lastByte + "/" + totalSize;
    		buffer.write(byteRange.getBytes());
    		buffer.write(MsrpConstants.NEW_LINE.getBytes());
    		
    		buffer.write(MsrpConstants.HEADER_STATUS.getBytes());
    		buffer.write(MsrpConstants.CHAR_DOUBLE_POINT);
    		buffer.write(MsrpConstants.CHAR_SP);
    		String status = "000 200 OK";
    		buffer.write(status.getBytes());
    		buffer.write(MsrpConstants.NEW_LINE.getBytes());
    
    		buffer.write(MsrpConstants.END_MSRP_MSG.getBytes());
    		buffer.write(txId.getBytes());
    		buffer.write(MsrpConstants.FLAG_LAST_CHUNK);
    		buffer.write(MsrpConstants.NEW_LINE.getBytes());
    
    		// Send request
    		requestTransaction = new RequestTransaction();
    		connection.sendChunk(buffer.toByteArray());
    		buffer.close();
	    } finally {
	    	// Changed by Deutsche Telekom
	        //CpuManager.releaseTempLock();
	    }
	}
	
	/**
	 * ���� MSRP SEND ����
	 * 
	 * @param txId ���� ID
	 * @param headers ���� ͷ
	 * @param flag Continuation flag
	 * @param data ���� ����
	 * @param totalSize T ���ݴ�С
	 * @throws IOException
	 */
	public void receiveMsrpSend(String txId, Hashtable<String, String> headers, int flag, byte[] data, long totalSize) throws IOException, MsrpException {
	    // Changed by Deutsche Telekom
	    //CpuManager.setTempLock();
	    try {
    		// Receive a SEND request
    		if (logger.isActivated()) {
			logger.debug("SEND request received (flag=" + flag + ", transaction=" + txId + ", totalSize="+totalSize+")");
    		}
    
    		// Read message-ID
    		String msgId = headers.get(MsrpConstants.HEADER_MESSAGE_ID);
    		
    		// Test if a failure report is needed
    		boolean failureReportNeeded = true;
    		String failureHeader = headers.get(MsrpConstants.HEADER_FAILURE_REPORT);
    		if ((failureHeader != null) && failureHeader.equalsIgnoreCase("no")) {
    			failureReportNeeded = false;
    		}
    		
    		// Send MSRP response if requested
    		if (failureReportNeeded) {
    			sendMsrpResponse(MsrpConstants.RESPONSE_OK + " " + MsrpConstants.COMMENT_OK, txId, headers);
    		}
    		
    		// Test if it's an empty chunk
    		if (data == null) { 
    			if (logger.isActivated()) {
    				logger.debug("Empty chunk");
    			}
    			return;
    		}
    		
    		// Save received data chunk if there is some
    		receivedChunks.addChunk(data);
    		
    		// Check the continuation flag
    		if (flag == MsrpConstants.FLAG_LAST_CHUNK) {
    			// Transfer terminated
    			if (logger.isActivated()) {
    				logger.info("Transfer terminated");
    			}
    
    			// Read the received content
    			byte[] dataContent = receivedChunks.getReceivedData();
    			receivedChunks.resetCache();
    
    			// Notify event listener
    			String contentTypeHeader = headers.get(MsrpConstants.HEADER_CONTENT_TYPE);
    			msrpEventListener.msrpDataReceived(msgId, dataContent, contentTypeHeader);
    
    			// Test if a success report is needed
    			boolean successReportNeeded = false;
    			String reportHeader = headers.get(MsrpConstants.HEADER_SUCCESS_REPORT);
    			if ((reportHeader != null) && reportHeader.equalsIgnoreCase("yes")) {
    				successReportNeeded = true;
    			}
    			
    			// Send MSRP report if requested
    			if (successReportNeeded) {
    				try {
    					sendMsrpReportRequest(txId, headers, dataContent.length, totalSize);
    				} catch(MsrpException e) {
    					// Report failed
    					if (logger.isActivated()) {
    						logger.error("Can't send report", e);
    					}
    					
    					// Notify event listener
    					// Changed by Deutsche Telekom
    					msrpEventListener.msrpTransferError(msgId, e.getMessage(), TypeMsrpChunk.StatusReport);
    				}
    			}
    		} else
    		if (flag == MsrpConstants.FLAG_ABORT_CHUNK) {
    			// Transfer aborted
    			if (logger.isActivated()) {
    				logger.info("Transfer aborted");
    			}
    
    			// Notify event listener
    			msrpEventListener.msrpTransferAborted();			
    		} else
    		if (flag == MsrpConstants.FLAG_MORE_CHUNK) {
    			// Transfer in progress
    			if (logger.isActivated()) {
    				logger.debug("Transfer in progress...");
    			}
                byte[] dataContent = receivedChunks.getReceivedData();
    
                // Notify event listener
                boolean resetCache = msrpEventListener.msrpTransferProgress(receivedChunks.getCurrentSize(), totalSize,
                        dataContent);
    
                // Data are only consumed chunk by chunk in file transfer & image share.
                // In a chat session only the whole message is consumed after receiving the last chunk.
                if (resetCache) {
                    receivedChunks.resetCache();
                }
    		}
	    } finally {
	    	// Changed by Deutsche Telekom
	        //CpuManager.releaseTempLock();
	    }
	}

	/**
	 * ���� MSRP ��Ӧ
	 * 
	 * @param code ��Ӧ ��
	 * @param txId ���� ID
	 * @param headers MSRP ͷ
	 */
	public void receiveMsrpResponse(int code, String txId, Hashtable<String, String> headers) {
	    // Changed by Deutsche Telekom
	    //CpuManager.setTempLock();
	    try {
    		if (logger.isActivated()) {
    			logger.info("Response received (code=" + code + ", transaction=" + txId + ")");
    		}
    		
            if (failureReportOption) {
                // Notify progress
                if (!cancelTransfer && progress.size() > 0) {
                    msrpEventListener.msrpTransferProgress(progress.elementAt(0), totalSize);
                    progress.removeElementAt(0);
                }
            }
            
    		// Notify request transaction
    		if (requestTransaction != null) {
    			requestTransaction.notifyResponse(code, headers);
    		}
    
            // Notify MSRP transaction
            if (msrpTransaction != null) {
                msrpTransaction.handleResponse();
            }
    
            // Notify event listener
    		if (code != 200) {
    			// Changed by Deutsche Telekom
    		    String cpimMsgId = null;
                TypeMsrpChunk typeMsrpChunk = TypeMsrpChunk.Unknown;
    		    MsrpTransactionInfo msrpTransactionInfo = getMsrpTransactionInfo(txId);
    		    if (msrpTransactionInfo != null) {
                    cpimMsgId = msrpTransactionInfo.cpimMsgId;
    		        typeMsrpChunk = msrpTransactionInfo.typeMsrpChunk;
    		    }
    			msrpEventListener.msrpTransferError(cpimMsgId, "error response " + code, typeMsrpChunk);

    			// Changed by Deutsche Telekom
    			// If an error is received it couldn't get any better nor worse; transaction has reached final state
                removeMsrpTransactionInfo(txId);
    		}
    		
            // Don't remove transaction info in general from list as this could be a preliminary answer 
	    } finally {
	    	// Changed by Deutsche Telekom
	        //CpuManager.releaseTempLock();
	    }
	}
	
	/**
	 * ���� MSRP REPORT ����
	 * 
	 * @param txId ���� ID
	 * @param headers MSRP ͷ
	 * @throws IOException
	 */

	public void receiveMsrpReport(String txId, Hashtable<String, String> headers) throws IOException {
		// Changed by Deutsche Telekom
	    // Example of an MSRP REPORT request:
        // MSRP b276bb5b0adb22f6 SEND
        // To-Path: msrp://10.108.25.89:19494/n02s00i2t0+519;tcp
        // From-Path: msrp://10.102.192.68:20000/1375944013409;tcp
        // Message-ID: MID-3BCqcBUXKA
        // Byte-Range: 1-305/305
        // Content-Type: message/cpim
        //
        // From: <sip:anonymous@anonymous.invalid>
        // To: <sip:anonymous@anonymous.invalid>
        // NS: imdn <urn:ietf:params:imdn>
        // imdn.Message-ID: Msg2BCqcBUWKA
        // DateTime: 2013-08-08T06:40:56.000Z
        // imdn.Disposition-Notification: positive-delivery, display
        //
        // Content-type: text/plain; charset=utf-8
        // Content-length: 1
        //
        // F
        // -------b276bb5b0adb22f6$
        //
        // MSRP b276bb5b0adb22f6 200 OK
        // To-Path: msrp://10.102.192.68:20000/1375944013409;tcp
        // From-Path: msrp://10.108.25.89:19494/n02s00i2t0+519;tcp
        // -------b276bb5b0adb22f6$
        //
        // MSRP n02s00i2t0+1937 REPORT
        // To-Path: msrp://10.102.192.68:20000/1375944013409;tcp
        // From-Path: msrp://10.108.25.89:19494/n02s00i2t0+519;tcp
        // Status: 000 413 413
        // Message-ID: MID-3BCqcBUXKA
        // Byte-Range: 1-305/305
        // -------n02s00i2t0+1937$
	    
	    //CpuManager.setTempLock();
	    try {
    		if (logger.isActivated()) {
    			logger.info("REPORT request received (transaction=" + txId + ")");
    		}
    		
            // Changed by Deutsche Telekom
            String msrpMsgId = headers.get(MsrpConstants.HEADER_MESSAGE_ID);
            String cpimMsgId = null;

            String originalTransactionId = null;
            TypeMsrpChunk typeMsrpChunk = TypeMsrpChunk.Unknown;
            MsrpTransactionInfo msrpTransactionInfo = getMsrpTransactionInfoByMessageId(msrpMsgId);
            if (msrpTransactionInfo != null) {
                typeMsrpChunk = msrpTransactionInfo.typeMsrpChunk;
                originalTransactionId = msrpTransactionInfo.transactionId;
                cpimMsgId = msrpTransactionInfo.cpimMsgId;
                if (logger.isActivated()) {
                    logger.debug("REPORT request details; originalTransactionId="
                            + originalTransactionId + "; cpimMsgId=" + cpimMsgId + "; typeMsrpChunk="
                            + typeMsrpChunk);
                }
            }

            // Changed by Deutsche Telekom
            // Test if a failure report is needed
            boolean failureReportNeeded = true;
            String failureHeader = headers.get(MsrpConstants.HEADER_FAILURE_REPORT);
            if ((failureHeader != null) && failureHeader.equalsIgnoreCase("no")) {
                failureReportNeeded = false;
            }
            
            // Send MSRP response if requested
            if (failureReportNeeded) {
                sendMsrpResponse(MsrpConstants.RESPONSE_OK + " " + MsrpConstants.COMMENT_OK, txId, headers);
            }
            
            // Check status code
            int statusCode = ReportTransaction.parseStatusCode(headers);
            if (statusCode != 200) {
                // Changed by Deutsche Telekom
                msrpEventListener.msrpTransferError(cpimMsgId, "error report " + statusCode, typeMsrpChunk);
            }
    
    		// Notify report transaction
    		if (reportTransaction != null) {
    			reportTransaction.notifyReport(statusCode, headers);
    		}
    		
            // Changed by Deutsche Telekom
            // Remove transaction info from list as transaction has reached a final state
            removeMsrpTransactionInfo(originalTransactionId);
	    } finally {
	    	// Changed by Deutsche Telekom
	        //CpuManager.releaseTempLock();
	    }
	}
	
    // Changed by Deutsche Telekom
    /**
     * Set the control if is to map the msgId from transactionId if not present on received MSRP messages
     */
	public void setMapMsgIdFromTransationId(boolean mapMsgIdFromTransationId) {
        if (mMapMsgIdFromTransationId != mapMsgIdFromTransationId) {
            synchronized (mTransactionMsgIdMapLock) {
                if (mapMsgIdFromTransationId) {
                    mTransactionInfoMap = new ConcurrentHashMap<String, MsrpSession.MsrpTransactionInfo>();
                    mMessageTransactionMap = new ConcurrentHashMap<String, String>();
                } else {
                    if (mTransactionInfoMap != null) {
                        mTransactionInfoMap.clear();
                        mTransactionInfoMap = null;
                    }
                    if (mMessageTransactionMap != null) {                    
                        mMessageTransactionMap.clear();
                        mMessageTransactionMap = null;
                    }
                }
            }
            mMapMsgIdFromTransationId = mapMsgIdFromTransationId;
        }
    }
	
    // Changed by Deutsche Telekom
    /**
     * �������˵�����б�
     * 
     * @param transactionId MSRP ����
     * @param msrpMsgId MSRP ��Ϣ ID
     * @param cpimMsgId CPIM ��Ϣ ID
     * @param typeMsrpChunk MSRP �������� (see {@link TypeMsrpChunk})
     */
    private void addMsrpTransactionInfo(String transactionId, String msrpMsgId, String cpimMsgId, TypeMsrpChunk typeMsrpChunk) {
        if (mTransactionInfoMap != null && transactionId != null) {
            synchronized (mTransactionMsgIdMapLock) {
                mTransactionInfoMap.put(transactionId, new MsrpTransactionInfo(transactionId, msrpMsgId, cpimMsgId, typeMsrpChunk));
                if (mMessageTransactionMap != null && msrpMsgId != null) {
                    mMessageTransactionMap.put(msrpMsgId, transactionId);
                }
            }
        }
    }
    
    // Changed by Deutsche Telekom
    /**
     * ���б���ɾ������˵��
     */
    public void removeMsrpTransactionInfo(String transactionId) {
        if (mTransactionInfoMap != null && transactionId != null) {
            synchronized (mTransactionMsgIdMapLock) {
                if (mMessageTransactionMap != null) {
                    MsrpTransactionInfo transactionInfo = getMsrpTransactionInfo(transactionId);
                    if (transactionInfo != null && transactionInfo.msrpMsgId != null){
                        mMessageTransactionMap.remove(transactionInfo.msrpMsgId);
                    }
                }
                mTransactionInfoMap.remove(transactionId);
            }
        }
    }
    
    // Changed by Deutsche Telekom
    /**
     * ��ȡ ����˵��
     */
    private MsrpTransactionInfo getMsrpTransactionInfo(String transactionId) {
        if (mTransactionInfoMap != null && transactionId != null) {
            synchronized (mTransactionMsgIdMapLock) {
                return mTransactionInfoMap.get(transactionId);
            }
        }
        
        return null;
    }
    
    // Changed by Deutsche Telekom
    /**
//     * ��ȡָ���� MSRP ��Ϣ ID����˵��
     * 
     * @param msrpMsgId MSRP ��Ϣ ID
     */
    private MsrpTransactionInfo getMsrpTransactionInfoByMessageId(String msrpMsgId) {
        if (mMessageTransactionMap != null && mTransactionInfoMap != null && msrpMsgId != null) {
            synchronized (mTransactionMsgIdMapLock) {
                String transactionId = mMessageTransactionMap.get(msrpMsgId);
                if (transactionId != null) {
                    return mTransactionInfoMap.get(transactionId);
                } 
            }
        }
        
        return null;
    }

    // Changed by Deutsche Telekom
	/**
	 * ��� ����˵���Ƿ����
	 */
    public void checkMsrpTransactionInfo() {
        if (mTransactionInfoMap != null) {
            Thread checkThread = new Thread(new Runnable() {

                @Override
                public void run() {

                    // Changed by Deutsche Telekom
                    //CpuManager.setTempLock();
                    try {
                        List<MsrpTransactionInfo> msrpTransactionInfos = null;
                        synchronized (mTransactionMsgIdMapLock) {
                            // Copy the transaction info items to accelerate the locking while doing expiring process
                            msrpTransactionInfos = new ArrayList<MsrpTransactionInfo>(mTransactionInfoMap.values());
                        }
                        for (MsrpTransactionInfo msrpTransactionInfo : msrpTransactionInfos) {
                            long delta = (System.currentTimeMillis() - msrpTransactionInfo.timestamp) / 1000;
                            if ((delta >= TRANSACTION_INFO_EXPIRY_PERIOD) || (delta < 0)) {
                                if (logger.isActivated()) {
                                    logger.debug("Transaction info have expired (transactionId: "
                                            + msrpTransactionInfo.transactionId + ", msgId: " + msrpTransactionInfo.msrpMsgId + ")");
                                }
                                mTransactionInfoMap.remove(msrpTransactionInfo.transactionId);
                                if (mMessageTransactionMap != null) {
                                    mMessageTransactionMap.remove(msrpTransactionInfo.msrpMsgId);
                                }
                            }
                        }
                    } finally {
                        // Changed by Deutsche Telekom
                        //CpuManager.releaseTempLock();                        
                    }
                }

            });
            checkThread.start();            

        }
    }
}
