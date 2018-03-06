package com.organon.helda.core.usecases;

public interface RequestHandler<Request, Response> {
    Response handle(Request request);
}
