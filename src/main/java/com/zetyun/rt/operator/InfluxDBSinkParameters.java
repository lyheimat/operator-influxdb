package com.zetyun.rt.operator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public class InfluxDBSinkParameters {

    @JsonPropertyDescription("influxDB连接url，格式为：http://host:port")
    @JsonProperty(required = true)
    private String url;

    @JsonPropertyDescription("连接用户名")
    @JsonProperty(required = true)
    private String username;

    @JsonPropertyDescription("连接密码")
    @JsonProperty(required = true)
    private String password;

    @JsonPropertyDescription("数据库名")
    @JsonProperty(required = true)
    private String database;

    @JsonPropertyDescription("数据的保留策略，默认为autogen")
    @JsonProperty(required = false)
    private String retentionPolicy;

    @JsonPropertyDescription("表名")
    @JsonProperty(required = true)
    private String measurement;

    @JsonPropertyDescription("数据中的tags字段")
    @JsonProperty(required = false)
    private List<String> tags;

    @JsonPropertyDescription("数据中的时间戳字段，没有就采用当前系统时间戳，时间戳和数据会一同写入influxDB")
    @JsonProperty(required = false)
    private String timestamp;

    @JsonPropertyDescription("时间戳精度，默认为纳秒")
    @JsonProperty(required = false)
    private TimeType timePrecision;

    @JsonPropertyDescription("是否启用批处理写入")
    @JsonProperty(required = false)
    private boolean enableBatch;

    @JsonPropertyDescription("批处理缓冲数据量")
    @JsonProperty(required = false)
    private int batchActions;

    @JsonPropertyDescription("批处理缓冲时间")
    @JsonProperty(required = false)
    private int flushDuration;

    @Override
    public String toString() {
        return "InfluxDBSinkParameters{" +
                "url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", database='" + database + '\'' +
                ", RP='" + retentionPolicy + '\'' +
                ", measurement='" + measurement + '\'' +
                ", tags=" + tags +
                ", timestamp='" + timestamp + '\'' +
                ", timePrecision=" + timePrecision +
                ", enableBatch=" + enableBatch +
                ", batchActions=" + batchActions +
                ", flushDuration=" + flushDuration +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getRetentionPolicy() {
        return retentionPolicy;
    }

    public void setRetentionPolicy(String retentionPolicy) {
        this.retentionPolicy = retentionPolicy;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public TimeType getTimePrecision() {
        return timePrecision;
    }

    public void setTimePrecision(TimeType timePrecision) {
        this.timePrecision = timePrecision;
    }

    public boolean isEnableBatch() {
        return enableBatch;
    }

    public void setEnableBatch(boolean enableBatch) {
        this.enableBatch = enableBatch;
    }

    public int getBatchActions() {
        return batchActions;
    }

    public void setBatchActions(int batchActions) {
        this.batchActions = batchActions;
    }

    public int getFlushDuration() {
        return flushDuration;
    }

    public void setFlushDuration(int flushDuration) {
        this.flushDuration = flushDuration;
    }
}

enum TimeType {
    NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS
}
