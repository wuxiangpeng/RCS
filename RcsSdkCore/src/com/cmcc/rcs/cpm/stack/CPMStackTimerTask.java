/*
 * @author:     Brett Buckingham
 * @author:     Last modified by: $Author: emcho $
 * @version:    $Date: 2009/07/17 18:58:14 $ $Revision: 1.3 $
 *
 * This source code has been contributed to the public domain.
 */

package com.cmcc.rcs.cpm.stack;

import java.util.TimerTask;

import android.util.Log;

/**
 * A subclass of TimerTask which runs TimerTask code within a try/catch block to
 * avoid killing the SIPTransactionStack timer thread. Note: subclasses MUST not
 * override run(); instead they should override runTask().
 *
 * @author GaoXusong
 *
 */
public abstract class CPMStackTimerTask extends TimerTask {
    private static final String TAG = "CPMStackTimerTask";
    // / Implements code to be run when the SIPStackTimerTask is executed.
    protected abstract void runTask();

    // / The run() method is final to ensure that all subclasses inherit the
    // exception handling.
    public final void run() {
        try {
            runTask();
        } catch (Throwable e) {
            Log.e(TAG, "SIP stack timer task failed due to exception:", e);            
            e.printStackTrace();
        }
    }
}
