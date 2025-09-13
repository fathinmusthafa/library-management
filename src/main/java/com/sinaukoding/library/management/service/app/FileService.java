package com.sinaukoding.library.management.service.app;

import com.sinaukoding.library.management.model.enums.TipeUpload;
import com.sinaukoding.library.management.model.response.BaseResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    BaseResponse<?> upload(MultipartFile files, TipeUpload tipeUpload);

    Resource loadFileAsResource(String pathFile);
}
