/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vektorsoft.appbox.server.exception;

/**
 *
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
public class CryptoException extends RuntimeException {

    public CryptoException(Throwable cause) {
	super(cause);
    }
    
    public CryptoException(String msg) {
	super(msg);
    }
}
