package moe.rtc.transaction.infra.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Snowflake {

    @Value("${application.infra.utils.snowflake.data-center}")
    public void setDataCenter( Long val) {
        dataCenter = val;
    }
    private static Long dataCenter;

    @Value("${application.infra.utils.snowflake.worker}")
    public void setWorker(Long val) {
        worker = val;
    }
    private static Long worker;

    private static cn.hutool.core.lang.Snowflake sf;

    @PostConstruct
    public static void init(){
        sf = new cn.hutool.core.lang.Snowflake(dataCenter, worker);
    }

    public static long nextId() {
        return sf.nextId();
    }
}
