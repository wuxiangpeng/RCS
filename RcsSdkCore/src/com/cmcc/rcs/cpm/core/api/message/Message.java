package com.cmcc.rcs.cpm.core.api.message;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ListIterator;

import com.cmcc.rcs.cpm.core.api.SipException;
import com.cmcc.rcs.cpm.core.api.header.ContentDispositionHeader;
import com.cmcc.rcs.cpm.core.api.header.ContentEncodingHeader;
import com.cmcc.rcs.cpm.core.api.header.ContentLanguageHeader;
import com.cmcc.rcs.cpm.core.api.header.ContentLengthHeader;
import com.cmcc.rcs.cpm.core.api.header.ContentTypeHeader;
import com.cmcc.rcs.cpm.core.api.header.ExpiresHeader;
import com.cmcc.rcs.cpm.core.api.header.Header;


public interface Message extends Cloneable, Serializable {
    void addFirst(Header header) throws SipException, NullPointerException;
    void addHeader(Header header);
    void addLast(Header header) throws SipException, NullPointerException;

    Header getHeader(String headerName);
    void setHeader(Header header);

    void removeFirst(String headerName) throws NullPointerException;
    void removeLast(String headerName) throws NullPointerException;
    void removeHeader(String headerName);

    ListIterator getHeaderNames();
    ListIterator getHeaders(String headerName);
    ListIterator getUnrecognizedHeaders();

    Object getApplicationData();
    void setApplicationData(Object applicationData);

    ContentLengthHeader getContentLength();
    void setContentLength(ContentLengthHeader contentLength);

    ContentLanguageHeader getContentLanguage();
    void setContentLanguage(ContentLanguageHeader contentLanguage);

    ContentEncodingHeader getContentEncoding();
    void setContentEncoding(ContentEncodingHeader contentEncoding);

    ContentDispositionHeader getContentDisposition();
    void setContentDisposition(ContentDispositionHeader contentDisposition);

    Object getContent();
    byte[] getRawContent();
    void setContent(Object content, ContentTypeHeader contentTypeHeader)
            throws ParseException;
    void removeContent();


    ExpiresHeader getExpires();
    void setExpires(ExpiresHeader expires);

    String getSIPVersion();
    void setSIPVersion(String version) throws ParseException;

    Object clone();
    boolean equals(Object object);
    int hashCode();
    String toString();
}
