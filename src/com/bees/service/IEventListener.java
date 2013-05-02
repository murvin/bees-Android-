/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.service;

/**
 * IEventListener.java
 * 
 * @author MURVIN BHANTOOA
 * @date 25/04/2012
 */
public interface IEventListener {
    void onEvent(int command, String uri, int status, Object response);
}
