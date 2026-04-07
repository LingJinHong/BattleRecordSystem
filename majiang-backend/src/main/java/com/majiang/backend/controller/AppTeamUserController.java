package com.majiang.backend.controller;

import com.majiang.backend.entity.AppTeamUser;
import com.majiang.backend.service.AppTeamUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.majiang.backend.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/team-user")
public class AppTeamUserController {
    @Autowired
    private AppTeamUserService appTeamUserService;

    @PostMapping("/add")
    public ApiResponse<String> addTeamUser(@RequestBody AppTeamUser appTeamUser) {
        boolean result  = appTeamUserService.save(appTeamUser);
        return result ? ApiResponse.ok("添加成功") : ApiResponse.fail(-1,"添加失败");
    }

    @DeleteMapping("/remove")
    public ApiResponse<String> removeTeamUser(@RequestParam Long teamId, @RequestParam Long userId) {
        QueryWrapper<AppTeamUser> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        boolean result = appTeamUserService.remove(wrapper);
        return result ? ApiResponse.ok("移除成功") : ApiResponse.fail(-1,"移除失败");
    }

    @GetMapping("/by-team")
    public ApiResponse<List<AppTeamUser>> getUsersByTeamId(@RequestParam Long teamId) {
        QueryWrapper<AppTeamUser> wrapper = new QueryWrapper<>();
        wrapper.eq("team_id", teamId);
        return ApiResponse.ok(appTeamUserService.list(wrapper));
    }

    @GetMapping("/by-user")
    public ApiResponse<List<AppTeamUser>> getTeamsByUserId(@RequestParam Long userId) {
        QueryWrapper<AppTeamUser> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return ApiResponse.ok(appTeamUserService.list(wrapper));
    }
}
