/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vektorsoft.appbox.server.test;

import com.vektorsoft.appbox.server.content.FileSystemContentLocatorTest;
import com.vektorsoft.xapps.deployer.client.HashCalculator;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 *
 * @author Vladimir Djurovic <vdjurovic@vektorsoft.com>
 */
public final class TestUtil {
    
    public static final String MOCK_HASH = "904b904fa8a71384391921581964fdf829c5535046c948f1e101b00083bfd99c";

    public static void createTestContent() throws Exception {
	File contentDir = new File("target","content");
	File dataDir = new File(contentDir, "content");
	dataDir.mkdirs();
	String hash = HashCalculator.fileHash(new File(FileSystemContentLocatorTest.class.getResource("/content/mock_content").toURI()));
	String[] parts = new String[]{
	    hash.substring(0, 2),
	    hash.substring(2,4),
	    hash.substring(4,6)
	};
	Path fileDir = Path.of("target", "content","content", parts[0], parts[1], parts[2]);
	fileDir.toFile().mkdirs();
	Path contentPath = Path.of("target", "content","content", parts[0], parts[1], parts[2], hash);
	Files.copy(FileSystemContentLocatorTest.class.getResourceAsStream("/content/mock_content"), contentPath, StandardCopyOption.REPLACE_EXISTING);
    }
    
    public static void clearTestContent() {
	File contentDir = new File("target", "content");
	contentDir.delete();
    }
}
