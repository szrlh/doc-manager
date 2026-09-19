package com.szr.docmanagerweb.service;


import com.szr.docmanagerweb.dto.ImportConfirmDTO;
import com.szr.docmanagerweb.dto.ImportResultDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文档导入服务接口
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
public interface ImportService {

    /**
     * 导入文档，拆分片段，返回分类建议
     * @param file 上传的文件
     * @return 导入结果列表
     */
    List<ImportResultDTO> importDocument(MultipartFile file);

    /**
     * 确认导入，持久化文档和片段
     * @param confirmDTO 确认导入的数据
     * @return 导入结果（例如文档ID）
     */
    Long confirmImport(ImportConfirmDTO confirmDTO);
}
