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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Simple client side implementation of the fakereplace protocol
 *
 * @author GanHaitian
 */
public class HotReplaceClient {

    public static void run(final String deploymentName, Map<String, ClassData> classes, final Map<String, ResourceData> resources) throws IOException {
        final Socket socket = new Socket("localhost", 6555);
        try {
            run(socket, deploymentName, classes, resources);
        } finally {
            socket.close();
        }
    }

    public static void run(Socket socket, final String deploymentName, Map<String, ClassData> classes, final Map<String, ResourceData> resources) {
        try {
            final DataInputStream input = new DataInputStream(socket.getInputStream());
            final DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.writeInt(0xCAFEDEAF);
            output.writeInt(deploymentName.length());
            output.write(deploymentName.getBytes());
            output.writeInt(classes.size());
            for (Map.Entry<String, ClassData> entry : classes.entrySet()) {
                output.writeInt(entry.getKey().length());
                output.write(entry.getKey().getBytes());
                output.writeLong(entry.getValue().getTimestamp());
            }
            output.writeInt(resources.size());
            for (Map.Entry<String, ResourceData> entry : resources.entrySet()) {
                final ResourceData data = entry.getValue();
                output.writeInt(data.getRelativePath().length());
                output.write(data.getRelativePath().getBytes());
                output.writeLong(data.getTimestamp());
            }
            output.flush();
            final Set<String> classNames = new HashSet<String>();
            final Set<String> resourceNames = new HashSet<String>();
            readReplacable(input, classNames);
            readReplacable(input, resourceNames);

            if(classNames.isEmpty()) {
                System.out.println("No updated classes found to replace");
            } else {
                System.out.println("Updating " + classNames.size() + " classes");
            }

            output.flush();
            output.writeInt(classNames.size());
            for (String name : classNames) {
                final ClassData data = classes.get(name);
                output.writeInt(name.length());
                output.write(name.getBytes());
                byte[] bytes = data.getContentSource().getData();
                output.writeInt(bytes.length);
                output.write(bytes);
            }

            output.writeInt(resourceNames.size());
            for (final String resource : resourceNames) {
                final ResourceData data = resources.get(resource);
                output.writeInt(resource.length());
                output.write(resource.getBytes());
                byte[] bytes = data.getContentSource().getData();
                output.writeInt(bytes.length);
                output.write(bytes);
            }

            output.flush();

            int result = input.readInt();
            if(result != 0) {
                System.out.println("Replacement failed");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static void readReplacable(final DataInputStream input, final Set<String> resourceNames) throws IOException {
        int noResources = input.readInt();
        for (int i = 0; i < noResources; ++i) {
            final String className = readString(input);
            resourceNames.add(className);
        }
    }

    private static String readString(final DataInputStream input) throws IOException {
        int toread = input.readInt();
        byte [] buf = new byte[toread];
        int read = 0;
        while (toread > 0 && (read = input.read(buf, read, toread)) != -1) {
            toread -= read;
        }
        return new String(buf);
    }
}
