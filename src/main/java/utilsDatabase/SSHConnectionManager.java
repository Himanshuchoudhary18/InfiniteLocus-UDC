package utilsDatabase;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import utilities.Base;
import utilities.Constants;

import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for managing SSH connections using JSch.
 * This class provides methods to establish and manage SSH sessions in a thread-safe manner.
 */
@Slf4j
public class SSHConnectionManager {

    private static final ThreadLocal<Session> threadLocalSession = ThreadLocal.withInitial(() -> null);
    protected static final AtomicInteger assignedPort = new AtomicInteger();

//    static {
//        // Enable verbose JSch logging
//        JSch.setLogger(new com.jcraft.jsch.Logger() {
//            public boolean isEnabled(int level) { return true; }
//            public void log(int level, String message) { System.out.println("JSch: " + message); }
//        });
//    }

    /**
     * Retrieves the current thread's SSH session.
     *
     * @return the current thread's SSH session, or null if no session is set.
     */
    private static Session getSshSession() {
        return threadLocalSession.get();
    }

    /**
     * Establishes an SSH connection using the properties defined in the Base class.
     * The SSH session is stored in a ThreadLocal variable to ensure thread safety.
     */
    protected static synchronized void connectToCommonSSHServer() {
        if (!Objects.isNull(threadLocalSession.get()) && threadLocalSession.get().isConnected()) {
            Base.logger.info("Tunnel already created");
            return;
        } else if (!Objects.isNull(threadLocalSession.get())) {
            Base.logger.info("Removed Existing tunnel");
            threadLocalSession.remove();
        }
        threadLocalSession.set(connectSSHSession(Base.getProperty().getProperty("sshHost"), Base.getProperty().getProperty("sshUser"), Integer.parseInt(Base.getProperty().getProperty("sshPort")), Base.getProperty().getProperty("privateKeyPath")));
    }

    /**
     * Establishes an SSH connection to the specified host using the provided user credentials and private key.
     *
     * @param host           the SSH server host.
     * @param user           the SSH user.
     * @param port           the SSH port.
     * @param privateKeyPath the path to the private key file.
     * @return the established SSH session, or null if the connection fails.
     */
    private static Session connectSSHSession(String host, String user, int port, String privateKeyPath) {
        Session session = null;
        JSch jsch = new JSch();
        try {
            Base.logger.info("Creating SSH Tunnel to host: {} and user: {} on port {}", host, user, port);
            String pemPath = Paths.get(Constants.USER_DIR, privateKeyPath).toAbsolutePath().toString();
            jsch.addIdentity(pemPath);
            session = jsch.getSession(user, host, port);
            session.setConfig("StrictHostKeyChecking", "no"); // Disable host key checking for simplicity
            session.setConfig("PreferredAuthentications", "publickey");
            session.setConfig("ConnectTimeout", String.valueOf(Base.getProperty().getProperty("CONNECTION_TIMEOUT")));
            session.setConfig("PubkeyAcceptedAlgorithms", "rsa-sha2-512,rsa-sha2-256,ssh-rsa");
            session.setConfig("server_host_key", "rsa-sha2-512,rsa-sha2-256,ssh-rsa");
            session.connect();
            if (session.isConnected()) {
                Base.logger.info("SSH Connection successful");
            } else {
                Base.logger.error("SSH Connection failed!");
            }
        } catch (JSchException e) {
            Base.logger.error("SSH Connection error: ", e);
            Assert.fail("SSH Connection error: ", e);
        }
        return session;
    }

    /**
     * Disconnects the current thread's SSH session.
     * If the session is not connected, logs an error message.
     */
    private static synchronized void disconnectSSHSession() {
        Session session = threadLocalSession.get();
        if (session != null && session.isConnected()) {
            session.disconnect();
            Base.logger.info("SSH Connection closed successfully");
        } else {
            Base.logger.error("SSH Connection is already closed");
        }
        threadLocalSession.remove();
    }

    /**
     * Disconnects the current thread's SSH session and removes port forwarding for the specified port.
     *
     * @param port the port to remove port forwarding for.
     */
    private static synchronized void disconnectSSHSession(int port) {
        deletePortForwarding(port);
        disconnectSSHSession();
    }

    /**
     * Disconnects the current thread's SSH session and removes port forwarding for the assigned port.
     */
    public static synchronized void disconnectSSHSessionDeletePortForwarding() {
        deletePortForwarding(assignedPort.get());
        disconnectSSHSession();
    }

    /**
     * Removes port forwarding for the specified port in the current thread's SSH session.
     * If the session is not connected, logs an error message.
     *
     * @param port the port to remove port forwarding for.
     */
    private static synchronized void deletePortForwarding(int port) {
        Session session = threadLocalSession.get();
        if (session != null && session.isConnected()) {
            try {
                Base.logger.info("Removing port forwarding for lPort: {}", port);
                session.delPortForwardingL(port);
            } catch (JSchException e) {
                if (!e.getMessage().contains("PortForwardingL: local port " + port + " is not registered.")) {
                    Base.logger.error("Error while removing port forwarding as port not registered: {}", e.getMessage());
                }
                Base.logger.error("Error while removing port forwarding: {}", e.getMessage());
            }
        } else {
            Base.logger.warn("SSH Connection is already closed no need to remove port forwarding");
        }
    }

    /**
     * Set port forwarding for the specified port in the current thread's SSH session.
     * If the session is not connected, logs an error message.
     *
     * @param lPort local port.
     * @param host  Host Name
     * @param rPort DB Port.
     */
    protected static synchronized int setPortForwarding(int lPort, String host, int rPort) {
        // Remove existing port forwarding if any
        deletePortForwarding(assignedPort.get());

        Session session = threadLocalSession.get();
        if (!Objects.isNull(session) && session.isConnected()) {
            try {
                Base.logger.info("Setting port forwarding for lPort: {} to host: {} and rPort: {}", lPort, host, rPort);
                assignedPort.set(session.setPortForwardingL(lPort, host, rPort));
                Base.logger.info("Port Forwarding assigned port: {}", assignedPort.get());
            } catch (JSchException e) {
                Base.logger.error("Error while port forwarding", e);
            }
        } else {
            Base.logger.warn("SSH Connection is already closed. Not forwarding port");
        }
        return assignedPort.get();
    }
}