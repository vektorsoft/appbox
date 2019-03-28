/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.appbox.server.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of {@link FilterInputStream} which tracks how many bytes were read from the underlying stream.
 * Used when it's needed to eg. calculate size of the file read, while performing other operations on it.
 *
 * @author Vladimir Djurovic
 */
public class SizeAwareInputStream extends FilterInputStream {

	private long size;

	public SizeAwareInputStream(InputStream in) {
		super(in);
		size = 0;
	}

	@Override
	public byte[] readAllBytes() throws IOException {
		byte[] data =  super.readAllBytes();
		size = data.length;
		return data;
	}

	@Override
	public int read() throws IOException {
		int count =  super.read();
		size += count;
		return count;
	}

	@Override
	public int read(byte[] b) throws IOException {
		int count = super.read(b);
		size += count;
		return count;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int count =  super.read(b, off, len);
		size += count;
		return count;
	}

	@Override
	public int readNBytes(byte[] b, int off, int len) throws IOException {
		int count = super.readNBytes(b, off, len);
		size += count;
		return count;
	}

	public long getSize() {
		return size;
	}
}
