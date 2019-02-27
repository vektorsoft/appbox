/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vektorsoft.appbox.server.content;

import com.vektorsoft.appbox.server.test.TestConfig;
import java.net.URI;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import static com.vektorsoft.appbox.server.test.TestUtil.*;

/**
 *
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@TestPropertySource(properties = "content.storage.root=target/content")
public class FileSystemContentLocatorTest {
    

    @Autowired
    @Qualifier(value = "fileSystemLocator")
    private ContentLocator contentLocator;
    
    @BeforeClass
    public static void setupContent() throws Exception {
	createTestContent();
	
    }
    
    @AfterClass
    public static void clearContent() {
	clearTestContent();
    }
    
    @Test
    public void testContentExists() throws Exception {
	boolean result = contentLocator.contentExists(MOCK_HASH);
	assertTrue(result);
    }
    
    @Test
    public void testContentUrl() throws Exception {
	URI uri = contentLocator.getContentLocation(MOCK_HASH);
	assertEquals("file", uri.getScheme());
	assertTrue(uri.toString().endsWith(MOCK_HASH));
    }
}
