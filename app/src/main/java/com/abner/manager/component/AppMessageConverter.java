package com.abner.manager.component;

import com.yanzhenjie.andserver.annotation.Converter;
import com.yanzhenjie.andserver.framework.MessageConverter;
import com.yanzhenjie.andserver.framework.body.JsonBody;
import com.yanzhenjie.andserver.http.ResponseBody;
import com.abner.manager.util.JsonUtils;
import com.yanzhenjie.andserver.util.IOUtils;
import com.yanzhenjie.andserver.util.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@Converter
public class AppMessageConverter implements MessageConverter {

    @Override
    public ResponseBody convert(@NonNull Object output, @Nullable MediaType mediaType) {

        return new JsonBody(JsonUtils.successfulJson(output));
    }

    @Nullable
    @Override
    public <T> T convert(@NonNull InputStream stream, @Nullable MediaType mediaType, Type type) throws IOException {
        Charset charset = mediaType == null ? null : mediaType.getCharset();
        if (charset == null) {
            return JsonUtils.parseJson(IOUtils.toString(stream), type);
        }
        return JsonUtils.parseJson(IOUtils.toString(stream, charset), type);
    }
}