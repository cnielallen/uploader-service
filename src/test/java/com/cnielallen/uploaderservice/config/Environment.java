package com.cnielallen.uploaderservice.config;


import lombok.Data;

@Data
public class Environment {
    public static final String ID = "com.cnielallen.uploaderservice.config.Environment";
    public String sftpHost;
    public int sftpPort;
}
