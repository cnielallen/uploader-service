package com.cnielallen.uploaderservice.config;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class SftpConfig {

    private final SftpServer sftpServer;

    public Session openSession() throws JSchException {
        var jsch = new JSch();
        var config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        //NOTE: Password should be encrypted
        jsch.addIdentity(sftpServer.getSshKey(), sftpServer.getPassphrase());
        if(sftpServer.getEnableCompression()){
            config.put("compression.s2c", "zlib@openssh.com,zlib,none");
            config.put("compression.c2s", "zlib@openssh.com,zlib,none");
            config.put("compression_level", "9");
        }
        var session = jsch.getSession(sftpServer.getUsername(), sftpServer.getHost(), sftpServer.getPort());
        session.setConfig(config);
        return session;
    }

}
