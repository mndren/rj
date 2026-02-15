package com.rj.handlers;

import com.rj.constants.RjConstants;
import com.rj.utility.RjUtility;
import io.undertow.server.HttpServerExchange;

public class IndexHandler {

    public void getPage(HttpServerExchange e) {
        String html = RjUtility.getIndex();

        RjUtility.sendHtml(e, RjConstants.RjResponse.Status.OK, html);
    }
}
