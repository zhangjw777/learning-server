package com.learning.file.controller;

import com.learning.common.entity.Result;
import com.learning.common.entity.ResultStatus;
import com.learning.file.service.FileService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件控制器
 *
 * @author 张家伟
 * @date 2025/04/06
 */
@RestController
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("videos")
    public Result<String> uploadVideo(@RequestParam MultipartFile multipartFile) throws Exception {
        return Result.of(ResultStatus.SUCCESS, fileService.uploadVideo(multipartFile));
    }

    @PostMapping("cover-pictures")
    public Result<String> uploadCoverPicture(@RequestParam MultipartFile multipartFile) throws Exception {
        return Result.of(ResultStatus.SUCCESS, fileService.uploadCoverPicture(multipartFile));
    }

    @PostMapping("profile-pictures")
    public Result<String> uploadProfilePicture(@RequestParam MultipartFile multipartFile) throws Exception {
        return Result.of(ResultStatus.SUCCESS, fileService.uploadProfilePicture(multipartFile));
    }

    @PostMapping("certificate-pictures")
    public Result<String> uploadCertificatePicture(@RequestParam MultipartFile multipartFile) throws Exception {
        return Result.of(ResultStatus.SUCCESS, fileService.uploadCertificatePicture(multipartFile));
    }
    @PostMapping("roadmap-pictures")
    public Result<String> uploadRoadmapPicture(@RequestParam MultipartFile multipartFile) throws Exception {
        return Result.of(ResultStatus.SUCCESS, fileService.uploadRoadmapPicture(multipartFile));
    }

}
