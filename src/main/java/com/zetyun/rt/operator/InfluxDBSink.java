package com.zetyun.rt.operator;

import com.zetyun.rt.meta.annotation.ActionMeta;
import com.zetyun.rt.sdk.action.SinkAction;
import com.zetyun.rt.sdk.model.RtEvent;
import com.zetyun.rt.sdk.operator.OperatorContext;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;


import java.util.*;
import java.util.concurrent.TimeUnit;

@ActionMeta(
        id = "InfluxDBSink",
        tags = "sink",
        name = "InfluxDBSink",
        description = "指定数据库和保留策略等信息，将数据写入influxDB",
        parameterClass = InfluxDBSinkParameters.class
)
public class InfluxDBSink extends SinkAction {

    private InfluxDBSinkParameters parameters;
    private InfluxDB influxDB;
    private String host;
    private String port;
    private String url;
    private String username;
    private String password;
    private String database;
    private String retentionPolicy;
    private String measurement;
    private List<String> tagList;
    private String timestamp;
    private TimeType timeType;
    private boolean enableBatch;
    private int batchActions;
    private int flushDuration;

    @Override
    public void init(OperatorContext context) {
        super.init(context);
        parameters = getContext().getParameters();
        url = parameters.getUrl();
        username = parameters.getUsername();
        password = parameters.getPassword();
        database = parameters.getDatabase();
        String rp = parameters.getRetentionPolicy();
        retentionPolicy = rp == null || rp.equals("") ? "autogen" : rp;
        measurement = parameters.getMeasurement();
        tagList = parameters.getTags();
        timestamp = parameters.getTimestamp();
        TimeType type = parameters.getTimePrecision();
        timeType = type == null ? TimeType.NANOSECONDS : type;
        enableBatch = parameters.isEnableBatch();

        //获取连接
        influxDB = InfluxDBFactory.connect(url, username, password);
        influxDB.setDatabase(database);
        influxDB.setRetentionPolicy(retentionPolicy);

        //是否启用批处理
        if (enableBatch){
            batchActions = parameters.getBatchActions();
            flushDuration = parameters.getFlushDuration();
            if (batchActions != 0 && flushDuration != 0){
                //自定义批处理数据量及时间间隔
                influxDB.enableBatch(batchActions, flushDuration, TimeUnit.MILLISECONDS);
            } else {
                //influxDB默认批处理设置
                influxDB.enableBatch(BatchOptions.DEFAULTS);
            }
        }
    }

    public void apply(RtEvent value) throws Exception {

        //判断数据是否含有事件时间
        long ts = 0;
        if (timestamp != null && !timestamp.equals("")){
            ts = Long.parseLong(value.getField(timestamp).toString());
            value.removeField(timestamp);
        }

        Map<String, Object> fields = value.getFields();
        HashMap<String, String> tags = new HashMap<String, String>();

        //准备tags数据和fields数据
        Iterator<Map.Entry<String, Object>> iterator = fields.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Object> item = iterator.next();
            String key = item.getKey();
            if (tagList.contains(key)){
                tags.put(key, (String) item.getValue());
                iterator.remove();
            }
        }

        if (ts == 0){
            insert(measurement, tags, fields);
        } else {
            insertWithTs(measurement, tags, fields, ts);
        }

    }

    /**
     * 单条插入，采用系统时间戳
     * @param measurement
     * @param tags
     * @param fields
     */
    public void insert(String measurement, Map<String, String> tags, Map<String, Object> fields){
        Point.Builder builder = Point.measurement(measurement);
        switch (timeType) {
            case MICROSECONDS:
                builder.time(System.currentTimeMillis() * 1000, TimeUnit.MICROSECONDS);
                break;
            case MILLISECONDS:
                builder.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                break;
            case SECONDS:
                builder.time(System.currentTimeMillis() / 1000, TimeUnit.SECONDS);
                break;
            case MINUTES:
                builder.time(System.currentTimeMillis() / 1000 / 60, TimeUnit.MINUTES);
                break;
            case HOURS:
                builder.time(System.currentTimeMillis() / 1000 / 3600, TimeUnit.HOURS);
                break;
            case NANOSECONDS:
        }
        builder.tag(tags);
        builder.fields(fields);
        influxDB.write(builder.build());
    }

    /**
     * 单条插入，用数据中的时间字段作为时间戳
     * @param measurement
     * @param tags
     * @param fields
     */
    public void insertWithTs(String measurement, Map<String, String> tags, Map<String, Object> fields, long ts){
        Point.Builder builder = Point.measurement(measurement);
        switch (timeType) {
            case MICROSECONDS:
                builder.time(ts, TimeUnit.MICROSECONDS);
                break;
            case MILLISECONDS:
                builder.time(ts, TimeUnit.MILLISECONDS);
                break;
            case SECONDS:
                builder.time(ts, TimeUnit.SECONDS);
                break;
            case MINUTES:
                builder.time(ts, TimeUnit.MINUTES);
                break;
            case HOURS:
                builder.time(ts, TimeUnit.HOURS);
                break;
            case NANOSECONDS:
        }
        builder.tag(tags);
        builder.fields(fields);
        influxDB.write(builder.build());
    }

    @Override
    public void close() throws Exception {
        super.close();
        influxDB.close();
    }
}
