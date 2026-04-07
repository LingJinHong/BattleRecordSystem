package com.majiang.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.majiang.backend.common.ApiException;
import com.majiang.backend.common.ApiResponse;
import com.majiang.backend.common.ApiResultCode;
import com.majiang.backend.dto.CreateAppTeamDataRequest;
import com.majiang.backend.dto.UpdateAppTeamDataRequest;
import com.majiang.backend.entity.AppTeamData;
import com.majiang.backend.service.AppTeamDataService;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/user-team")
public class AppTeamDataController {

    private final AppTeamDataService appTeamDataService;

    public AppTeamDataController(AppTeamDataService appTeamDataService) {
        this.appTeamDataService = appTeamDataService;
    }

    // 新增
    @PostMapping
    public ApiResponse<AppTeamData> create(@RequestBody @Valid CreateAppTeamDataRequest request) {
        LocalDateTime now = LocalDateTime.now();
        AppTeamData entity = new AppTeamData();
        entity.setUserId(request.getUserId());
        entity.setTeamName(request.getTeamName());
        entity.setCreateTime(now);
        entity.setUpdateTime(now);

        boolean saved = appTeamDataService.save(entity);
        if (!saved) {
            throw new ApiException(ApiResultCode.INTERNAL_ERROR, "新增失败");
        }
        return ApiResponse.ok(entity);
    }

    // 列表查询（分页 + 可选过滤）
    @GetMapping
    public ApiResponse<IPage<AppTeamData>> list(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String teamName,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size
    ) {
        Page<AppTeamData> p = new Page<>(page, size);
        LambdaQueryWrapper<AppTeamData> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(userId)) {
            wrapper.eq(AppTeamData::getUserId, userId);
        }
        if (StringUtils.hasText(teamName)) {
            wrapper.like(AppTeamData::getTeamName, teamName);
        }
        wrapper.orderByDesc(AppTeamData::getId);

        IPage<AppTeamData> result = appTeamDataService.page(p, wrapper);
        return ApiResponse.ok(result);
    }

    // 修改
    @PutMapping("/{id}")
    public ApiResponse<AppTeamData> update(@PathVariable Long id, @RequestBody UpdateAppTeamDataRequest request) {
        AppTeamData entity = appTeamDataService.getById(id);
        if (entity == null) {
            throw new ApiException(ApiResultCode.NOT_FOUND, "数据不存在");
        }

        if (request.getUserId() != null) {
            entity.setUserId(request.getUserId());
        }

        if (StringUtils.hasText(request.getTeamName())) {
            entity.setTeamName(request.getTeamName());
        }
        entity.setUpdateTime(LocalDateTime.now());

        boolean updated = appTeamDataService.updateById(entity);
        if (!updated) {
            throw new ApiException(ApiResultCode.INTERNAL_ERROR, "修改失败");
        }
        return ApiResponse.ok(entity);
    }

    // 删除
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        AppTeamData entity = appTeamDataService.getById(id);
        if (entity == null) {
            throw new ApiException(ApiResultCode.NOT_FOUND, "数据不存在");
        }
        boolean removed = appTeamDataService.removeById(id);
        return ApiResponse.ok(removed);
    }
}

