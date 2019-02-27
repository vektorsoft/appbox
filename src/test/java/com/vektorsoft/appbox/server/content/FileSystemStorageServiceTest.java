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
import com.vektorsoft.appbox.server.test.TestUtil;
import java.io.InputStream;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@TestPropertySource(properties = "content.storage.root=target/content")
public class FileSystemStorageServiceTest {

    @Autowired
    @Qualifier("fileSystemStorageService")
    private ContentStorageService storageService;

    @BeforeClass
    public static void setupContent() throws Exception {
	TestUtil.createTestContent();

    }

    @AfterClass
    public static void clearContent() {
	TestUtil.clearTestContent();
    }

    @Test
    public void testGetBinaryData() throws Exception {
	InputStream is = storageService.getBinaryData(TestUtil.MOCK_HASH);
	assertNotNull(is);
    }
}
