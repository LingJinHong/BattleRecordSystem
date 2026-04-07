package com.majiang.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.majiang.backend.common.ApiException;
import com.majiang.backend.common.ApiResponse;
import com.majiang.backend.common.ApiResultCode;
import com.majiang.backend.dto.CreateAppUserBattleRequest;
import com.majiang.backend.dto.UpdateAppUserBattleRequest;
import com.majiang.backend.entity.AppUserBattle;
import com.majiang.backend.service.AppUserBattleService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/user-battle")
public class AppUserBattleController {

    private final AppUserBattleService appUserBattleService;

    public AppUserBattleController(AppUserBattleService appUserBattleService) {
        this.appUserBattleService = appUserBattleService;
    }

    // 新增
    @PostMapping
    public ApiResponse<AppUserBattle> create(@RequestBody @Valid CreateAppUserBattleRequest request) {
        LocalDateTime now = LocalDateTime.now();
        AppUserBattle entity = new AppUserBattle();
        entity.setUserId(request.getUserId());
        entity.setIncomeExpenseType(request.getIncomeExpenseType());
        entity.setScore(request.getScore());
        entity.setVenueFee(request.getVenueFee());
        entity.setTeamId(request.getTeamId());
        entity.setMatchDate(request.getMatchDate());
        entity.setRemark(request.getRemark());
        entity.setCreateTime(now);
        entity.setUpdateTime(now);

        boolean saved = appUserBattleService.save(entity);
        if (!saved) {
            throw new ApiException(ApiResultCode.INTERNAL_ERROR, "新增失败");
        }
        return ApiResponse.ok(entity);
    }

    // 根据ID查询
    @GetMapping("/{id}")
    public ApiResponse<AppUserBattle> getById(@PathVariable Long id) {
        AppUserBattle entity = appUserBattleService.getById(id);
        if (entity == null) {
            throw new ApiException(ApiResultCode.NOT_FOUND, "数据不存在");
        }
        return ApiResponse.ok(entity);
    }

    // 根据 userId + matchDate 查询（
    @GetMapping("/by-user-id-matchDate")
    public ApiResponse<IPage<AppUserBattle>> getByUserIdAndCreateTime(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size
    ) {
        Page<AppUserBattle> p = new Page<>(page, size);
        LambdaQueryWrapper<AppUserBattle> wrapper = new LambdaQueryWrapper<AppUserBattle>()
                .eq(AppUserBattle::getUserId, userId)
                .ge(AppUserBattle::getMatchDate, startTime)
                .le(AppUserBattle::getMatchDate, endTime);

                wrapper.orderByDesc(AppUserBattle::getId);

                IPage<AppUserBattle> result = appUserBattleService.page(p, wrapper);
                return ApiResponse.ok(result);
    }

    // 列表查询（分页 + 可选过滤）
    @GetMapping
    public ApiResponse<IPage<AppUserBattle>> list(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String incomeExpenseType,
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) String remark,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size
    ) {
        Page<AppUserBattle> p = new Page<>(page, size);
        LambdaQueryWrapper<AppUserBattle> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(AppUserBattle::getUserId, userId);
        }
        if (StringUtils.hasText(incomeExpenseType)) {
            wrapper.eq(AppUserBattle::getIncomeExpenseType, incomeExpenseType);
        }
        if (teamId != null) {
            wrapper.eq(AppUserBattle::getTeamId, teamId);
        }
        if (StringUtils.hasText(remark)) {
            wrapper.like(AppUserBattle::getRemark, remark);
        }
        if (startTime != null) {
            wrapper.ge(AppUserBattle::getMatchDate, startTime);
        }
        if (endTime != null) {
            wrapper.le(AppUserBattle::getMatchDate, endTime);
        }
        wrapper.orderByDesc(AppUserBattle::getId);

        IPage<AppUserBattle> result = appUserBattleService.page(p, wrapper);
        return ApiResponse.ok(result);
    }

    // 修改
    @PutMapping("/{id}")
    public ApiResponse<AppUserBattle> update(@PathVariable Long id, @RequestBody @Valid UpdateAppUserBattleRequest request) {
        AppUserBattle entity = appUserBattleService.getById(id);
        if (entity == null) {
            throw new ApiException(ApiResultCode.NOT_FOUND, "数据不存在");
        }

        if (request.getUserId() != null) {
            entity.setUserId(request.getUserId());
        }
        entity.setIncomeExpenseType(request.getIncomeExpenseType());
        entity.setVenueFee(request.getVenueFee());
        entity.setScore(request.getScore());
        entity.setTeamId(request.getTeamId());
        entity.setMatchDate(request.getMatchDate());
        entity.setRemark(request.getRemark());
        entity.setUpdateTime(LocalDateTime.now());

        boolean updated = appUserBattleService.updateById(entity);
        if (!updated) {
            throw new ApiException(ApiResultCode.INTERNAL_ERROR, "修改失败");
        }
        return ApiResponse.ok(entity);
    }

    // 删除
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        AppUserBattle entity = appUserBattleService.getById(id);
        if (entity == null) {
            throw new ApiException(ApiResultCode.NOT_FOUND, "数据不存在");
        }
        boolean removed = appUserBattleService.removeById(id);
        return ApiResponse.ok(removed);
    }
}

