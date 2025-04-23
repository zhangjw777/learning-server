package com.learning.course.controller;


import com.github.pagehelper.PageInfo;
import com.learning.common.entity.Page;
import com.learning.common.entity.Result;
import com.learning.common.entity.ResultStatus;
import com.learning.course.entity.Certificate;
import com.learning.course.entity.Note;
import com.learning.course.service.ICertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户证书信息表 前端控制器
 * </p>
 *
 * @author 张家伟
 * @since 2025-04-20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/certificates")
public class CertificateController {

    private final ICertificateService certificateService;

    @GetMapping
    public Result<Page<Certificate>> listCertificates(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize,
                                                      @RequestParam(required = false) String username) {
        PageInfo<Certificate> pageInfo = new PageInfo<>();
        if (username == null) {
            pageInfo.setList(certificateService.list());
        } else {
            pageInfo = certificateService.listByUserName(pageNum, pageSize, username);
            if (pageInfo == null)
                return Result.of(ResultStatus.SUCCESS, Page.of(null,0L));
        }
        return Result.of(ResultStatus.SUCCESS, Page.of(pageInfo.getList(), pageInfo.getTotal()));
    }

}
