package org.huanzhang.project.common;

import com.google.code.kaptcha.Producer;
import jakarta.annotation.Resource;
import org.huanzhang.common.constant.CacheConstants;
import org.huanzhang.common.constant.Constants;
import org.huanzhang.common.utils.sign.Base64;
import org.huanzhang.common.utils.uuid.IdUtils;
import org.huanzhang.framework.redis.ReactiveRedisUtils;
import org.huanzhang.framework.web.domain.AjaxResult;
import org.huanzhang.project.system.service.SysConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

/**
 * 验证码操作处理
 *
 * @author ruoyi
 */
@RestController
public class CaptchaController {
    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Resource
    private ReactiveRedisUtils<String> reactiveRedisUtils;

    // 验证码类型
    @Value("${ruoyi.captchaType}")
    private String captchaType;

    @Resource
    private SysConfigService configService;

    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    public Mono<AjaxResult> getCode() throws IOException {
        AjaxResult ajax = AjaxResult.success();
        return configService.selectCaptchaEnabled()
                .flatMap(captchaEnabled -> {
                    ajax.put("captchaEnabled", captchaEnabled);
                    if (!captchaEnabled) {
                        return Mono.just(ajax);
                    }

                    // 保存验证码信息
                    String uuid = IdUtils.simpleUUID();
                    String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;

                    String capStr, code = null;
                    BufferedImage image = null;

                    // 生成验证码
                    if ("math".equals(captchaType)) {
                        String capText = captchaProducerMath.createText();
                        capStr = capText.substring(0, capText.lastIndexOf("@"));
                        code = capText.substring(capText.lastIndexOf("@") + 1);
                        image = captchaProducerMath.createImage(capStr);
                    } else if ("char".equals(captchaType)) {
                        capStr = code = captchaProducer.createText();
                        image = captchaProducer.createImage(capStr);
                    }

                    if (Objects.isNull(image)) {
                        return Mono.just(ajax);
                    }

                    return reactiveRedisUtils.setCacheObject(verifyKey, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION))
                            .then(convertImageToBase64(image))
                            .map(img -> {
                                ajax.put("uuid", uuid);
                                ajax.put("img", img);
                                return ajax;
                            });
                });
    }

    /**
     * 将BufferedImage转换为Base64编码的字符串
     *
     * @param image 验证码图片
     * @return Base64编码的图片字符串
     */
    private Mono<String> convertImageToBase64(BufferedImage image) {
        return Mono.fromCallable(() -> {
                    try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()) {
                        // 写入图片到字节流（格式为JPG）
                        ImageIO.write(image, "jpg", os);
                        // 对字节数组进行Base64编码
                        return Base64.encode(os.toByteArray());
                    }
                })
                // 切换到IO线程池，避免阻塞WebFlux的事件循环线程
                .subscribeOn(Schedulers.boundedElastic());
    }

}
