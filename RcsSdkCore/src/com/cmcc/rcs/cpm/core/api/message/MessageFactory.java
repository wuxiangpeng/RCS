package com.cmcc.rcs.cpm.core.api.message;

import java.text.ParseException;
import java.util.List;

import com.cmcc.rcs.cpm.core.api.address.URI;
import com.cmcc.rcs.cpm.core.api.header.CSeqHeader;
import com.cmcc.rcs.cpm.core.api.header.CallIdHeader;
import com.cmcc.rcs.cpm.core.api.header.ContentTypeHeader;
import com.cmcc.rcs.cpm.core.api.header.FromHeader;
import com.cmcc.rcs.cpm.core.api.header.MaxForwardsHeader;
import com.cmcc.rcs.cpm.core.api.header.ServerHeader;
import com.cmcc.rcs.cpm.core.api.header.ToHeader;
import com.cmcc.rcs.cpm.core.api.header.UserAgentHeader;
import com.cmcc.rcs.cpm.message.Request;
import com.cmcc.rcs.cpm.message.Response;


public interface MessageFactory {
    Request createRequest(URI requestURI, String method, CallIdHeader callId,
            CSeqHeader cSeq, FromHeader from, ToHeader to, List via,
            MaxForwardsHeader maxForwards, ContentTypeHeader contentType,
            Object content) throws ParseException;

    Request createRequest(URI requestURI, String method, CallIdHeader callId,
            CSeqHeader cSeq, FromHeader from, ToHeader to, List via,
            MaxForwardsHeader maxForwards, ContentTypeHeader contentType,
            byte[] content) throws ParseException;

    Request createRequest(URI requestURI, String method, CallIdHeader callId,
            CSeqHeader cSeq, FromHeader from, ToHeader to, List via,
            MaxForwardsHeader maxForwards) throws ParseException;

    Request createRequest(String request) throws ParseException;

    Response createResponse(int statusCode, CallIdHeader callId,
            CSeqHeader cSeq, FromHeader from, ToHeader to, List via,
            MaxForwardsHeader maxForwards, ContentTypeHeader contentType,
            Object content) throws ParseException;

    Response createResponse(int statusCode, CallIdHeader callId,
            CSeqHeader cSeq, FromHeader from, ToHeader to, List via,
            MaxForwardsHeader maxForwards, ContentTypeHeader contentType,
            byte[] content) throws ParseException;

    Response createResponse(int statusCode, CallIdHeader callId,
            CSeqHeader cSeq, FromHeader from, ToHeader to, List via,
            MaxForwardsHeader maxForwards) throws ParseException;

    Response createResponse(int statusCode, Request request,
            ContentTypeHeader contentType, Object content)
            throws ParseException;

    Response createResponse(int statusCode, Request request,
            ContentTypeHeader contentType, byte[] content)
            throws ParseException;

    Response createResponse(int statusCode, Request request)
            throws ParseException;

    Response createResponse(String response) throws ParseException;

    void setDefaultContentEncodingCharset(String defaultContentEncodingCharset)
            throws NullPointerException, IllegalArgumentException;
    void setDefaultServerHeader(ServerHeader defaultServerHeader);
    void setDefaultUserAgentHeader(UserAgentHeader defaultUserAgentHeader);
}

