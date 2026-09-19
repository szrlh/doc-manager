package com.szr.docmanagerweb.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 前端路由回退控制器
 * 将所有非 API 且不含点号的路径转发到 index.html，
 * 以支持 Vue Router 的 history 模式或应对静态资源未找到的情况。
 * 注意：本项目中前端使用 hash 模式，此控制器为保险起见添加。
 *
 * @author hao liu
 * @version 1.0
 * @since 2026/09/05
 */
@Controller
public class ForwardController {

    /**
     * 匹配根路径以及所有不含点号的路径（如 /documents, /sections 等），
     * 转发到 index.html。
     * 含点号的路径（如 /assets/xxx.js）不会匹配，避免影响静态资源访问。
     */
    @RequestMapping(value = {"/", "/{path:[^.]*}"})
    public String forward() {
        return "forward:/index.html";
    }
}
