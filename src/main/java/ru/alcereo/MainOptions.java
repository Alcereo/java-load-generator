package ru.alcereo;

import com.beust.jcommander.Parameter;

/**
 * Created by alcereo on 23.07.17.
 */
public class MainOptions {

    @Parameter(names = "--help", help = true, hidden = true)
    private boolean help;

    @Parameter(
            names = {
                    "-p",
                    "--threads"
            },
            description = "Thread pool worker number"
    )
    private int threadNumber = 10;

    @Parameter(
            names = {
                    "-l",
                    "-throughput"
            },
            description = "Requests throughput limit"
    )
    private int throughputLimit = 1000;

    @Parameter(
            names = {
                    "-t",
                    "--time"
            },
            description = "Time limit in seconds"
    )
    private int timeLimitSeconds = 20;

    @Parameter(
            names = {
                    "-u",
                    "--url"
            },
            description = "Url for requests"
    )
    private String host = "http://localhost:8080/";

    @Parameter(
            names = {
                    "--influx"
            },
            description = "Turn on influxdb metrics reporter"
    )
    private boolean influxMetricReporter = false;

    @Parameter(
            names = {
                    "--metric-host"
            },
            description = "Influxdb host to send metrics"
    )
    private String influxdbHost = "172.55.0.5";

    @Parameter(
            names = {
                    "--metric-port"
            },
            description = "Influxdb port to send metrics"
    )
    private int influxDBPort = 8094;

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    public int getThreadNumber() {
        return threadNumber;
    }

    public void setThreadNumber(int threadNumber) {
        this.threadNumber = threadNumber;
    }

    public int getThroughputLimit() {
        return throughputLimit;
    }

    public void setThroughputLimit(int throughputLimit) {
        this.throughputLimit = throughputLimit;
    }

    public int getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public void setTimeLimitSeconds(int timeLimitSeconds) {
        this.timeLimitSeconds = timeLimitSeconds;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isInfluxMetricReporter() {
        return influxMetricReporter;
    }

    public void setInfluxMetricReporter(boolean influxMetricReporter) {
        this.influxMetricReporter = influxMetricReporter;
    }

    public String getInfluxdbHost() {
        return influxdbHost;
    }

    public void setInfluxdbHost(String influxdbHost) {
        this.influxdbHost = influxdbHost;
    }

    public int getInfluxDBPort() {
        return influxDBPort;
    }

    public void setInfluxDBPort(int influxDBPort) {
        this.influxDBPort = influxDBPort;
    }
}
