package io.github.s0cks.jmp.server;

public final class PublicationException
extends RuntimeException{
    public PublicationException(String msg, Throwable t){
        super(msg, t);
    }
}