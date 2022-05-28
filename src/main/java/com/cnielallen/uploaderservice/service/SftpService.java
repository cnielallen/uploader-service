package com.cnielallen.uploaderservice.service;

import com.cnielallen.uploaderservice.config.FileProperties;
import com.cnielallen.uploaderservice.config.SftpConfig;
import com.cnielallen.uploaderservice.config.SftpServer;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Comparator;
import java.util.Vector;

@Slf4j
@Component
@RequiredArgsConstructor
public class SftpService {

    private final SftpConfig sftp;
    private final SftpServer sftpServer;
    private ChannelSftp channelSftp;
    private Session session;

    private static final String PREVIOUS_DIR = "..";
    private static final String CURRENT_DIR = ".";

    @SneakyThrows
    public InputStream get(FileProperties.FileTemplate fileTemplate){
        session = sftp.openSession();
        var path = fileTemplate.getFileDirectory();
        var filename = fileTemplate.getFileName();
        var format = fileTemplate.getFileNameFormat();
        var fileType = fileTemplate.getType();
        session.connect();
        try{
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            Vector<ChannelSftp.LsEntry> contents = channelSftp.ls(path);
            var files  = contents.stream().filter(
                    file -> !file.getFilename().matches(PREVIOUS_DIR+"|"+CURRENT_DIR));
            var latestFile = files.max(Comparator.comparing(file-> file.getAttrs().getMTime())).get();
            fileTemplate.setFileName(latestFile.getFilename());
            return channelSftp.get( path + (path.endsWith("/") ? "" : "/") + latestFile.getFilename());
        } catch (final SftpException e) {
            switch (e.id) {
                case ChannelSftp.SSH_FX_NO_SUCH_FILE:
                    log.warn("File for {} not found in {} in SFTP Server", fileType, path);
                default:
                    log.warn("No files in {} SFTP server", path);
                }
            }
            return null;
    }

    public void closeSftp(){
        channelSftp.exit();
        session.disconnect();
        log.info("Channel connected: {} and session connected: {} ", channelSftp.isConnected(), session.isConnected());
    }

}
