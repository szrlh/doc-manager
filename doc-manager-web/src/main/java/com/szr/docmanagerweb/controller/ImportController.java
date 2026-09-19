package com.szr.docmanagerweb.controller;


import com.szr.docmanagerweb.dto.ImportConfirmDTO;
import com.szr.docmanagerweb.dto.ImportResultDTO;
import com.szr.docmanagerweb.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文档导入控制器
 *
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    /**
     * 导入 Markdown/TXT 文件，解析为片段并返回分类建议
     *
     * @param file 上传的文件
     * @return 导入结果（包含片段及分类建议）
     */
    @PostMapping
    public List<ImportResultDTO> importFile(@RequestParam("file") MultipartFile file) {
        return importService.importDocument(file);
    }

    @PostMapping("/confirm")
    public Long confirmImport(@RequestBody ImportConfirmDTO confirmDTO) {
        return importService.confirmImport(confirmDTO);
    }
}
