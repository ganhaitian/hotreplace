/*
 * Copyright 2012, GanHaitian, and individual contributors as indicated
 * by the @authors tag.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.windhot.hotreplace.client;

/**
* @author GanHaitian
*/
public final class ResourceData {
    private final long timestamp;
    private final String relativePath;
    private final ContentSource contentSource;

    public ResourceData(final String relativePath, long time, final ContentSource contentSource) {
        this.relativePath = relativePath;
        this.timestamp = time;
        this.contentSource = contentSource;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public ContentSource getContentSource() {
        return contentSource;
    }
}