package seker.multimedia.parser;

import java.io.InputStream;

import com.google.gson.Gson;

import seker.common.net.IResponseParser;
import seker.common.utils.StreamUtils;
import seker.multimedia.bean.FileStatuses;

public class FileStatusesParser implements IResponseParser<InputStream, FileStatuses> {
    @Override
    public FileStatuses parseResponse(InputStream is) {
        String json = StreamUtils.streamToString(is);
        return new Gson().fromJson(json, FileStatuses.class);
    }
}
