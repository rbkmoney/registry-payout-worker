package com.rbkmoney.registry.payout.worker.ftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.*;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.impl.DefaultConnectionConfig;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.nio.file.Paths;
import java.util.*;

import static ch.qos.logback.core.CoreConstants.EMPTY_STRING;

@Slf4j
public final class TestFtpServer {

    public static final int FTP_PORT = 20100;
    public static final String FTP_TEST_USER = "test";
    public static final String FTP_TEST_PASSWORD = "test";

    public FtpServer createServer() {
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(FTP_PORT);
        serverFactory.addListener("default", factory.createListener());
        serverFactory.setUserManager(getTestUserManager());
        serverFactory.setFtplets(getTestFtplets());

        ConnectionConfig config = new DefaultConnectionConfig(false,
                200, 2, 2, 2, 0);
        serverFactory.setConnectionConfig(config);
        return serverFactory.createServer();
    }

    private static UserManager getTestUserManager() {
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();

        UserManager um = userManagerFactory.createUserManager();
        try {
            um.save(getTestBaseUser());
        } catch (FtpException ex) {
            log.error("FtpException: {}", ex);
        }
        return um;
    }

    private static Map<String, Ftplet> getTestFtplets() {
        Map<String, Ftplet> m = new HashMap<>();
        m.put("miaFtplet", new DefaultFtplet());
        return m;
    }

    private static BaseUser getTestBaseUser() {
        BaseUser user = new BaseUser();
        user.setName(FTP_TEST_USER);
        user.setPassword(FTP_TEST_PASSWORD);
        user.setHomeDirectory(Paths.get(EMPTY_STRING).toAbsolutePath().toString());
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new WritePermission());
        user.setAuthorities(authorities);
        return user;
    }

    @Slf4j
    private static class DefaultFtplet implements Ftplet {

        @Override
        public void init(FtpletContext ftpletContext) {
        }

        @Override
        public void destroy() {
            log.info("DefaultFtplet: destroy");
        }

        @Override
        public FtpletResult beforeCommand(FtpSession session, FtpRequest request) {
            return FtpletResult.DEFAULT;
        }

        @Override
        public FtpletResult afterCommand(FtpSession session, FtpRequest request, FtpReply reply) {
            return FtpletResult.DEFAULT;
        }

        @Override
        public FtpletResult onConnect(FtpSession session) {
            log.info("DefaultFtplet: connect " + session.getUserArgument() + " : " + session.toString());
            return FtpletResult.DEFAULT;
        }

        @Override
        public FtpletResult onDisconnect(FtpSession session) {
            return FtpletResult.DEFAULT;
        }
    }

}
