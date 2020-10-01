/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.nessus.actions.testing;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Finds currently available server ports.
 */
public final class AvailablePortFinder {

    public static final int MIN_PORT_NUMBER = 1100;
    public static final int MAX_PORT_NUMBER = 65535;

    // Note, this is scoped on the ClassLoader
    private static Set<Integer> alreadyUsed = new HashSet<>();

    public static int getNextAvailable() {
        return getNextAvailable(MIN_PORT_NUMBER);
    }

    public static int getNextAvailable(int fromPort) {
        InetAddress addr;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            throw new IllegalStateException(ex);
        }
        return getNextAvailable(addr, fromPort);
    }

    public static int getNextAvailable(InetAddress addr) {
        return getNextAvailable(addr, MIN_PORT_NUMBER);
    }

    public static int getNextAvailable(InetAddress addr, int fromPort) {
        if (fromPort < MIN_PORT_NUMBER || fromPort > MAX_PORT_NUMBER)
            throw new IllegalArgumentException("Invalid start port: " + fromPort);

        for (int i = fromPort; i <= MAX_PORT_NUMBER; i++) {
            if (available(addr, i)) {
                return i;
            }
        }

        throw new NoSuchElementException("Could not find an available port above " + fromPort);
    }

    public synchronized static boolean available(InetAddress addr, int port) {

        try (ServerSocket ss = new ServerSocket()) {
            ss.setReuseAddress(false);
            ss.bind(new InetSocketAddress(addr, port));
        } catch (IOException e) {
            return false;
        }

        return alreadyUsed.add(port);
    }
}
