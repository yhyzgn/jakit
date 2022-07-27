package com.yhy.jakit.starter.crypto.component.enums;

import com.yhy.jakit.starter.crypto.component.decoder.Base64Decoder;
import com.yhy.jakit.starter.crypto.component.decoder.Base64URLDecoder;
import com.yhy.jakit.starter.crypto.component.decoder.Decoder;
import com.yhy.jakit.starter.crypto.component.decoder.HexDecoder;
import com.yhy.jakit.starter.crypto.component.encoder.Base64Encoder;
import com.yhy.jakit.starter.crypto.component.encoder.Base64URLEncoder;
import com.yhy.jakit.starter.crypto.component.encoder.Encoder;
import com.yhy.jakit.starter.crypto.component.encoder.HexEncoder;

/**
 * 编码器和解码器
 * <p>
 * Created on 2021-05-31 17:20
 *
 * @author 颜洪毅
 * @version 1.0.0
 * @since 1.0.0
 */
public enum Format {
    BASE64(new Base64Encoder(), new Base64Decoder()),
    BASE64URL(new Base64URLEncoder(), new Base64URLDecoder()),
    HEX(new HexEncoder(), new HexDecoder()),
    ;

    private final Encoder encoder;
    private final Decoder decoder;

    Format(Encoder encoder, Decoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public Encoder encoder() {
        return encoder;
    }

    public Decoder decoder() {
        return decoder;
    }
}
