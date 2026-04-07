package com.majiang.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.majiang.backend.common.ApiException;
import com.majiang.backend.common.ApiResponse;
import com.majiang.backend.common.ApiResultCode;
import com.majiang.backend.entity.AppFriendUser;
import com.majiang.backend.service.AppFriendUserService;
import com.majiang.backend.service.AppUserDataService;
import com.majiang.backend.entity.AppUserData;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/friend-user")
public class AppFriendUserController {

    private static final Logger log = LoggerFactory.getLogger(AppFriendUserController.class);

    @Autowired
    private AppFriendUserService appFriendUserService;

    @Autowired
    private AppUserDataService appUserDataService;

    @PostMapping("/add")
    public ApiResponse<String> addFriend(@RequestBody AppFriendUser appFriendUser) {
        boolean result = appFriendUserService.save(appFriendUser);
        return result ? ApiResponse.ok("添加成功") : ApiResponse.fail(-1,"添加失败");
    }

    @DeleteMapping("/remove")
    public ApiResponse<String> removeFriend(@RequestParam Long userId, @RequestParam Long friendUserId) {
        LambdaQueryWrapper<AppFriendUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppFriendUser::getUserId, userId).eq(AppFriendUser::getFriendUserId, friendUserId);

        boolean result = appFriendUserService.remove(wrapper);

        if (result) {
            return ApiResponse.ok("移除成功");
        } else {
            // 添加日志记录以方便调试
            log.error("Failed to remove friend with userId: {} and friendUserId: {}", userId, friendUserId);
            return ApiResponse.fail(-1, "移除失败");
        }
    }


    @GetMapping("/by-user")
    public ApiResponse<List<FriendUserInfo>> getFriendsByUserId(@RequestParam Long userId) {

        if (userId == null) {
            throw new ApiException(ApiResultCode.BAD_REQUEST, "userId不能为空");
        }
        // 查询所有好友关系
        LambdaQueryWrapper<AppFriendUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppFriendUser::getUserId, userId);
        List<AppFriendUser> friendList = appFriendUserService.list(wrapper);
        // 提取所有好友的用户ID
        List<Long> friendUserIds = friendList.stream().map(AppFriendUser::getFriendUserId).collect(Collectors.toList());
        if (friendUserIds.isEmpty()) {
            return ApiResponse.ok(List.of());
        }
        // 查询所有好友的用户信息
        List<AppUserData> userDataList = appUserDataService.listByIds(friendUserIds);
        // 只返回 id、title、avatarUrl
        List<FriendUserInfo> result = userDataList.stream().map(u -> new FriendUserInfo(u.getId(), u.getTitle(), u.getAvatarUrl())).collect(Collectors.toList());
        return ApiResponse.ok(result);
    }

    public static class FriendUserInfo {
        private Long id;
        private String title;
        private String avatarUrl;

        public FriendUserInfo(Long id, String title, String avatarUrl) {
            this.id = id;
            this.title = title;
            this.avatarUrl = avatarUrl;
        }

        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getAvatarUrl() { return avatarUrl; }
    }

    @GetMapping("/by-user-friend")
    public ApiResponse<List<AppFriendUser>> getFriendRecords(@RequestParam Long userId, @RequestParam Long friendUserId) {
        LambdaQueryWrapper<AppFriendUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppFriendUser::getUserId, userId);
        wrapper.eq(AppFriendUser::getFriendUserId, friendUserId);
        List<AppFriendUser> friendList = appFriendUserService.list(wrapper);
        return ApiResponse.ok(friendList);
    }
}
