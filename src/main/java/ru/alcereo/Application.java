package ru.alcereo;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import metrics_influxdb.InfluxdbReporter;
import metrics_influxdb.UdpInfluxdbProtocol;
import metrics_influxdb.api.measurements.CategoriesMetricMeasurementTransformer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by alcereo on 13.06.17.
 */
public class Application {

    private static AtomicInteger throughputCounter = new AtomicInteger();

    private static Counter counter ;
    private static Counter failCounter;
    private static Timer timer;

    private static boolean finish = false;

    public static void main(String[] args) throws InterruptedException {

        JCommander.Builder builder = JCommander.newBuilder();

        MainOptions opts = new MainOptions();
        builder.addObject(opts);

        JCommander parser = builder.build();
        parser.setProgramName("alc-load-gen");

        try {
            parser.parse(args);

            if (opts.isHelp())
                parser.usage();

        }catch (ParameterException e){
            e.usage();
        }

        ExecutorService pool = Executors.newFixedThreadPool(opts.getThreadNumber());

        System.out.println("Start");

        MetricRegistry registry = new MetricRegistry();

        if (opts.isInfluxMetricReporter()) {
            InfluxdbReporter.forRegistry(registry)
                    .protocol(new UdpInfluxdbProtocol(opts.getInfluxdbHost(), opts.getInfluxDBPort()))
                    .convertRatesTo(TimeUnit.MILLISECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .transformer(new CategoriesMetricMeasurementTransformer())
                    .build().start(1, TimeUnit.SECONDS);
        }

//        ConsoleReporter.forRegistry(registry)
//                .build().start(1, TimeUnit.SECONDS);

        counter = registry.counter("count");
        failCounter = registry.counter("fails");
        timer = registry.timer("execution");

        long currentCount = 0;
        long throughput = 0;
        int secondCounter = 0;

        for (int i = 0; i < opts.getThreadNumber(); i++) {
            pool.submit(() -> SendRequest(opts.getHost(), opts.getThroughputLimit()));
        }

        pool.shutdown();

        while (secondCounter<opts.getTimeLimitSeconds()){
            secondCounter++;

            Thread.sleep(1000);
            throughput = counter.getCount() - currentCount;
            currentCount = counter.getCount();

            System.out.print("\r");
            System.out.print("Time: "+secondCounter+"s - Count: "+counter.getCount()+". Fails:"+failCounter.getCount()+". Threshold: "+throughput);

            if (secondCounter%10 == 0){
                System.out.println();
            }

            throughputCounter.set(0);

        }

        finish = true;
        System.out.println("\nFinish");

    }

    private static boolean SendRequest(String url, int throughputLimit) {

        RestTemplate template = new RestTemplate();

        try {

            while (!finish) {

                if (throughputCounter.incrementAndGet() <= throughputLimit) {

                    Timer.Context time = timer.time();

                    try {

                        ResponseEntity<String> forEntity = template.getForEntity(url, String.class);

                        if (forEntity.getStatusCodeValue() != 200) {
                            System.out.println("REQUEST FAIL!");
                            failCounter.inc();
                        } else {
                            counter.inc();
                        }
                    } catch (RestClientException e) {
                        failCounter.inc();
                    }finally {
                        time.stop();
                    }

                }
            }

        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
        }

        return false;
    }

}
