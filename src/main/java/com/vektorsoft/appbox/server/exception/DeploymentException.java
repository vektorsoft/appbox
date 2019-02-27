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
public class DeploymentException extends Exception {

    public DeploymentException(Throwable th) {
	super(th);
    }
    
    public DeploymentException(String msg) {
	super(msg);
    }
}
