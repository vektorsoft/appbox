/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
