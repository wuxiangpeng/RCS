/*
 * Conditions Of Use
 *
 * This software was developed by employees of the National Institute of
 * Standards and Technology (NIST), an agency of the Federal Government.
 * Pursuant to title 15 Untied States Code Section 105, works of NIST
 * employees are not subject to copyright protection in the United States
 * and are considered to be in the public domain.  As a result, a formal
 * license is not needed to use the software.
 *
 * This software is provided by NIST as a service and is expressly
 * provided "AS IS."  NIST MAKES NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED
 * OR STATUTORY, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON-INFRINGEMENT
 * AND DATA ACCURACY.  NIST does not warrant or make any representations
 * regarding the use of the software or the results thereof, including but
 * not limited to the correctness, accuracy, reliability or usefulness of
 * the software.
 *
 * Permission to use this software is contingent upon your acceptance
 * of the terms of this agreement
 *
 * .
 *
 */
/******************************************************************************
 * Product of NIST/ITL Advanced Networking Technologies Division (ANTD)       *
 ******************************************************************************/
package com.cmcc.rcs.cpm.parser;

/*
 *
 * Lamine Brahimi and Yann Duponchel (IBM Zurich) noticed that the parser was
 * blocking so I threw out some cool pipelining which ran fast but only worked
 * when the phase of the moon matched its mood. Now things are serialized and
 * life goes slower but more reliably.
 *
 */



import java.text.ParseException;
import java.io.*;

import com.cmcc.rcs.cpm.message.CPMData;


/**
 * This implements a pipelined message parser suitable for use with a stream -
 * oriented input such as TCP. The client uses this class by instatiating with
 * an input stream from which input is read and fed to a message parser. It
 * keeps reading from the input stream and process messages in a never ending
 * interpreter loop. The message listener interface gets called for processing
 * messages or for processing errors. The payload specified by the
 * content-length header is read directly from the input stream. This can be
 * accessed from the SIPMessage using the getContent and getContentBytes methods
 * provided by the SIPMessage class.
 *
 * 
 *
 * @author GaoXusong
 *
 * @see SIPMessageListener
 */
public final class PipelinedMsgParser implements Runnable {
    /**
     * The message listener that is registered with this parser. (The message
     * listener has methods that can process correct and erroneous messages.)
     */
    protected CPMMessageListener mCpmMessageListener;
    private Thread mythread; // Preprocessor thread
    //private byte[] messageBody;
    //private boolean errorFlag;
    private Pipeline rawInputStream;
    private int maxMessageSize;
    private int sizeCounter;
    //private int messageSize;

    /**
     * default constructor.
     */
    protected PipelinedMsgParser() {
        super();

    }

    private static int uid = 0;

    private static synchronized int getNewUid() {
        return uid++;
    }

    /**
     * Constructor when we are given a message listener and an input stream
     * (could be a TCP connection or a file)
     *
     * @param cpmMessageListener
     *            Message listener which has methods that get called back from
     *            the parser when a parse is complete
     * @param in
     *            Input stream from which to read the input.
     * @param debug
     *            Enable/disable tracing or lexical analyser switch.
     */
    public PipelinedMsgParser(CPMMessageListener cpmMessageListener,
            Pipeline in, boolean debug, int maxMessageSize) {
        this();
        this.mCpmMessageListener = cpmMessageListener;
        rawInputStream = in;
        this.maxMessageSize = maxMessageSize;
        mythread = new Thread(this);
        mythread.setName("PipelineThread-" + getNewUid());

    }

    /**
     * This is the constructor for the pipelined parser.
     *
     * @param mhandler
     *            a CPMMessageListener implementation that provides the message
     *            handlers to handle correctly and incorrectly parsed messages.
     * @param in
     *            An input stream to read messages from.
     */

    public PipelinedMsgParser(CPMMessageListener mhandler, Pipeline in,
            int maxMsgSize) {
        this(mhandler, in, false, maxMsgSize);
    }

    /**
     * This is the constructor for the pipelined parser.
     *
     * @param in -
     *            An input stream to read messages from.
     */

    public PipelinedMsgParser(Pipeline in) {
        this(null, in, false, 0);
    }

    /**
     * Start reading and processing input.
     */
    public void processInput() {
        mythread.start();
    }

    /**
     * Create a new pipelined parser from an existing one.
     *
     * @return A new pipelined parser that reads from the same input stream.
     */
    protected Object clone() {
        PipelinedMsgParser p = new PipelinedMsgParser();

        p.rawInputStream = this.rawInputStream;
        p.mCpmMessageListener = this.mCpmMessageListener;
        Thread mythread = new Thread(p);
        mythread.setName("PipelineThread");
        return p;
    }

    /**
     * Add a class that implements a SIPMessageListener interface whose methods
     * get called * on successful parse and error conditons.
     *
     * @param mlistener
     *            a SIPMessageListener implementation that can react to correct
     *            and incorrect pars.
     */

    public void setMessageListener(CPMMessageListener mlistener) {
        mCpmMessageListener = mlistener;
    }

    /**
     * read a line of input (I cannot use buffered reader because we may need to
     * switch encodings mid-stream!
     */
    private String readLine(InputStream inputStream) throws IOException {
        StringBuffer retval = new StringBuffer("");
        while (true) {
            char ch;
            int i = inputStream.read();
            if (i == -1) {
                throw new IOException("End of stream");
            } else
                ch = (char) i;
            // reduce the available read size by 1 ("size" of a char).
            if (this.maxMessageSize > 0) {
                this.sizeCounter--;
                if (this.sizeCounter <= 0)
                    throw new IOException("Max size exceeded!");
            }
            if (ch != '\r')
                retval.append(ch);
            if (ch == '\n') {
                break;
            }
        }
        return retval.toString();
    }

    /**
     * This is input reading thread for the pipelined parser. You feed it input
     * through the input stream (see the constructor) and it calls back an event
     * listener interface for message processing or error. It cleans up the
     * input - dealing with things like line continuation
     */
    public void run() {

        Pipeline inputStream = this.rawInputStream;
        // inputStream = new MyFilterInputStream(this.rawInputStream);
        // I cannot use buffered reader here because we may need to switch
        // encodings to read the message body.
        try {
            while (true) {
                this.sizeCounter = this.maxMessageSize;
                // this.messageSize = 0;
                StringBuffer inputBuffer = new StringBuffer();

         
                String line1;
                String line2 = null;

                while (true) {
                    try {
                        line1 = readLine(inputStream);
                        // ignore blank lines.
                        if (line1.equals("\n")) {                      
                            continue;
                        } else
                            break;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        this.rawInputStream.stopTimer();
                        return;

                    }
                }

                inputBuffer.append(line1);
                // Guard against bad guys.
                this.rawInputStream.startTimer();
                
                while (true) {
                    try {
                        line2 = readLine(inputStream);
                        inputBuffer.append(line2);
                        if (line2.trim().equals(""))
                            break;
                    } catch (IOException ex) {
                        this.rawInputStream.stopTimer();
                        ex.printStackTrace();
                        return;

                    }
                }

                // Stop the timer that will kill the read.
                this.rawInputStream.stopTimer();
                inputBuffer.append(line2);
                CPMData cpmData = new CPMData();
                String messageBody = null;
          
                
                messageBody = inputBuffer.toString();
                if (messageBody == null) {
                    this.rawInputStream.stopTimer();
                    continue;
                } else {
                    cpmData.setMessageBody(messageBody);
                    //Here to receive the call JNI from TanLinLin
                    //jint JniRecvNetPack(jstring RcvHost, jstring RcvPort, jstring RcvString);
                }
                
         
                int contentLength = cpmData.obtainContentLengthFromJni(messageBody);
                cpmData.obtainCallIDfromJni(messageBody);                
                cpmData.obtainCpmStatusfromJni(messageBody);
                
                // Content length too large - process the message and
                // return error from there.
                if (mCpmMessageListener != null) {
                    try {
                        mCpmMessageListener.processMessage(cpmData);
                    } catch (Exception ex) {
                        // fatal error in processing - close the
                        // connection.
                        break;
                    }
                }
            }
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            this.rawInputStream.close();
        } catch (IOException ex) {
            // Ignore.
        }
    }
}
