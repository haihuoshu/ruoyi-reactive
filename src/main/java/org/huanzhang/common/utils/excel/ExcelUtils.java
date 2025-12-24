package org.huanzhang.common.utils.excel;

import cn.idev.excel.FastExcelFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

/**
 * Excel工具类
 *
 * @author haihuoshu
 * @version 2025-12-24
 */
public class ExcelUtils {

    private static final String FILENAME_TEMPLATE = "%s.xlsx";

    /**
     * 响应式导出 Excel 文件
     */
    public static Mono<Void> export(ServerHttpResponse response, Class<?> head, Collection<?> data, String filename) {
        // 设置响应头
        response.getHeaders().setContentDisposition(
                ContentDisposition.attachment()
                        .filename(String.format(FILENAME_TEMPLATE, filename), StandardCharsets.UTF_8)
                        .build()
        );
        response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);

        // 响应式生成Excel字节数组
        return Mono.fromCallable(() -> {
                    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                        // 使用FastExcel写入数据
                        FastExcelFactory.write(out, head)
                                .sheet()
                                .doWrite(data);

                        // 刷新并返回字节数组
                        out.flush();
                        return out.toByteArray();
                    } catch (Exception e) {
                        throw new IOException("生成Excel字节数组失败", e);
                    }
                })
                // 使用弹性线程池处理
                .subscribeOn(Schedulers.boundedElastic())
                // 将字节数组转为DataBuffer
                .map(bytes -> {
                    DataBuffer buffer = response.bufferFactory().allocateBuffer(bytes.length);
                    buffer.write(bytes);
                    return buffer;
                })
                // 确保DataBuffer资源释放，避免内存泄漏
                .doOnDiscard(DataBuffer.class, DataBufferUtils::release)
                // 写入响应流
                .flatMapMany(Flux::just).as(response::writeWith)
                // 异常统一处理
                .onErrorMap(e -> new RuntimeException("Excel导出失败：" + e.getMessage(), e));
    }

}