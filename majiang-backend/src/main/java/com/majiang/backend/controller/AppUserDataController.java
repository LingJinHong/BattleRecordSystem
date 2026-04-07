package com.majiang.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.majiang.backend.common.ApiException;
import com.majiang.backend.common.ApiResponse;
import com.majiang.backend.common.ApiResultCode;
import com.majiang.backend.dto.CreateAppUserDataRequest;
import com.majiang.backend.dto.UpdateAppUserDataRequest;
import com.majiang.backend.entity.AppUserData;
import com.majiang.backend.service.AppUserDataService;
import com.majiang.backend.service.WechatService;
import com.majiang.backend.upload.AvatarStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/user-data")
@RequiredArgsConstructor
public class AppUserDataController {

    private final AppUserDataService appUserDataService;
    private final AvatarStorageService avatarStorageService;
    private final WechatService wechatService;

    /**
     * 新增用户数据。
     * 支持传入 avatar 网络地址，后端会下载并落盘保存。
     */
    @PostMapping
    public ApiResponse<AppUserData> create(@RequestBody @Valid CreateAppUserDataRequest request) {
        LocalDateTime now = LocalDateTime.now();
        AppUserData entity = new AppUserData();
        entity.setOpenid(request.getOpenid());
        entity.setTitle(request.getTitle());
        entity.setContent(request.getContent());
        entity.setCreateTime(now);
        entity.setUpdateTime(now);

        String avatarUrl = avatarStorageService.saveAvatarFromUrl(request.getAvatarUrl());
        entity.setAvatarUrl(avatarUrl);

        boolean saved = appUserDataService.save(entity);
        if (!saved) {
            throw new ApiException(ApiResultCode.INTERNAL_ERROR, "新增失败");
        }
        entity.setOpenid(null);
        return ApiResponse.ok(entity);
    }

    /**
     * 根据微信小程序登录 code 获取 openid。
     * 前端可直接传入 wx.login() 返回的 code。
     */
    @GetMapping("/by-code")
    public ApiResponse<String> getByCode(@RequestParam String code) {
        return ApiResponse.ok(wechatService.getOpenidByCode(code));
    }

    /**
     * 查询单条用户数据。
     * 推荐路由：GET /api/user-data/{id}
     * 兼容路由：GET /api/user-data/by-id?id={id}
     */
    @GetMapping({"/{id}", "/by-id"})
    public ApiResponse<AppUserData> getById(
            @PathVariable(required = false) Long id,
            @RequestParam(required = false) Long idParam
    ) {
        Long queryId = id != null ? id : idParam;
        if (queryId == null) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "id不能为空");
        }
        AppUserData entity = appUserDataService.getById(queryId);
        if (entity == null) {
            throw new ApiException(ApiResultCode.NOT_FOUND, "数据不存在");
        }
        entity.setOpenid(null);
        return ApiResponse.ok(entity);
    }

    /**
     * 按 openid 查询用户数据列表。
     * 推荐路由：GET /api/user-data/openid/{openid}
     * 兼容路由：GET /api/user-data/by-openid?openid={openid}
     */
    @GetMapping({"/openid/{openid}", "/by-openid"})
    public ApiResponse<List<AppUserData>> getByOpenid(
            @PathVariable(required = false) String openid,
            @RequestParam(name = "openid", required = false) String openidParam
    ) {
        String queryOpenid = StringUtils.hasText(openid) ? openid : openidParam;
        if (!StringUtils.hasText(queryOpenid)) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "openid不能为空");
        }
        LambdaQueryWrapper<AppUserData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppUserData::getOpenid, queryOpenid).orderByDesc(AppUserData::getId);
        List<AppUserData> list = appUserDataService.list(wrapper);
        return ApiResponse.ok(list);
    }

    @GetMapping
    public ApiResponse<IPage<AppUserData>> list(
            @RequestParam(required = false) String openid,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size
    ) {
        Page<AppUserData> p = new Page<>(page, size);
        LambdaQueryWrapper<AppUserData> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(openid)) {
            wrapper.eq(AppUserData::getOpenid, openid);
        }
        if (StringUtils.hasText(title)) {
            wrapper.like(AppUserData::getTitle, title);
        }
        wrapper.orderByDesc(AppUserData::getId);

        IPage<AppUserData> result = appUserDataService.page(p, wrapper);
        return ApiResponse.ok(result);
    }

    @PutMapping("/{id}")
    public ApiResponse<AppUserData> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateAppUserDataRequest request
    ) {
        AppUserData entity = appUserDataService.getById(id);
        if (entity == null) {
            throw new ApiException(ApiResultCode.NOT_FOUND, "数据不存在");
        }

        entity.setTitle(request.getTitle());
        entity.setContent(request.getContent());
        String avatarUrl = avatarStorageService.saveAvatarFromUrl(request.getAvatar());
        if (avatarUrl != null) {
            entity.setAvatarUrl(avatarUrl);
        }
        entity.setUpdateTime(LocalDateTime.now());

        boolean updated = appUserDataService.updateById(entity);
        if (!updated) {
            throw new ApiException(ApiResultCode.INTERNAL_ERROR, "修改失败");
        }
        return ApiResponse.ok(entity);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        AppUserData entity = appUserDataService.getById(id);
        if (entity == null) {
            throw new ApiException(ApiResultCode.NOT_FOUND, "数据不存在");
        }
        boolean removed = appUserDataService.removeById(id);
        return ApiResponse.ok(removed);
    }
}
