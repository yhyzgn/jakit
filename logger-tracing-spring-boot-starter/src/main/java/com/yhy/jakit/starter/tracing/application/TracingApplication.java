package com.yhy.jakit.starter.tracing.application;

import com.yhy.jakit.starter.tracing.adapter.TtlMDCAdapter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.lang.reflect.Field;

/**
 * 反射替换 MDCAdapter
 * <p>
 * Created on 2021-08-06 9:42
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class TracingApplication {
    private static final TtlMDCAdapter MDC_ADAPTER = new TtlMDCAdapter();

    static {
        try {
            Field mdcAdapter = MDC.class.getDeclaredField("mdcAdapter");
            mdcAdapter.setAccessible(true);
            Object adapter = mdcAdapter.get(null);
            if (!(adapter instanceof TtlMDCAdapter)) {
                log.info("Injecting {} to MDC...", TtlMDCAdapter.class);
                mdcAdapter.set(null, MDC_ADAPTER);
                log.info("{} injected to MDC.", TtlMDCAdapter.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
