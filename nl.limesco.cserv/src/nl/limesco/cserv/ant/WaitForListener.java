package nl.limesco.cserv.ant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class WaitForListener extends Task {
    
    private static final Pattern INTERNET_PATTERN = Pattern.compile("([a-z0-9]+) +(\\d+) +(\\d+) +([0-9.*]+) +([0-9.*]+) +([A-Z_]+) *");

    private int m_port;
    
    private int m_timeoutSeconds = Integer.MAX_VALUE;
    
    private String m_waitingMessage;
    
    public void setPort(int port) {
        m_port = port;
    }
    
    public void setTimeoutSeconds(int timeoutSeconds) {
        m_timeoutSeconds = timeoutSeconds;
    }
    
    public void setWaitingMessage(String waitingMessage) {
        m_waitingMessage = waitingMessage;
    }

    @Override
    public void execute() throws BuildException {
        int secondsWaiting = 0;
        try {
            while (!isListeningOnPort()) {
                if (secondsWaiting == 0 && m_waitingMessage != null) {
                    log(m_waitingMessage);
                }
                if (++secondsWaiting == m_timeoutSeconds) {
                    throw new BuildException("Port " + m_port + " is still not listening after timeout");
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            throw new BuildException(e);
        }
    }

    private boolean isListeningOnPort() {
        final String expectedLocalAddress = "*." + m_port;
        for (NetstatLine line : netstat()) {
            if ("LISTEN".equals(line.getState()) && expectedLocalAddress.equals(line.getLocalAddress())) {
                return true;
            }
        }
        return false;
    }
    
    private static List<NetstatLine> netstat() {
        InputStream netstatStream = null;
        try {
            final Process process = new ProcessBuilder("netstat", "-a", "-n").start();
            netstatStream = process.getInputStream();
            return processNetstatStream(netstatStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (netstatStream != null) {
                try {
                    netstatStream.close();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static List<NetstatLine> processNetstatStream(InputStream netstatStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(netstatStream));
        final List<NetstatLine> lines = new LinkedList<NetstatLine>();
        try {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                final Matcher matcher = INTERNET_PATTERN.matcher(line);
                if (matcher.matches()) {
                    final NetstatLine newLine = new NetstatLine();
                    newLine.setProtocol(matcher.group(1));
                    newLine.setReceiveQueue(Integer.parseInt(matcher.group(2)));
                    newLine.setSendQueue(Integer.parseInt(matcher.group(3)));
                    newLine.setLocalAddress(matcher.group(4));
                    newLine.setForeignAddress(matcher.group(5));
                    newLine.setState(matcher.group(6));
                    lines.add(newLine);
                }
            }
            return lines;
        } finally {
            reader.close();
        }
    }
    
}
