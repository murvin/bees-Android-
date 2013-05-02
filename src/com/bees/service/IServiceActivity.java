/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees.service;

import java.util.Observer;

/**
 * ServiceActivity.java
 * 
 * @author MURVIN BHANTOOA
 * @date 08 May 2012
 */
public interface IServiceActivity extends Observer {
	void onServiceBinded();
}
