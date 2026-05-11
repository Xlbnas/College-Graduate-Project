package com.campus.trade.common;

/**
 * 解析 JWT 后的当前登录主体（论文示例中的 UserContext）。
 */
public final class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(Long userId, String role) {
        USER_ID.set(userId);
        ROLE.set(role);
    }

    public static void clear() {
        USER_ID.remove();
        ROLE.remove();
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static String getRole() {
        return ROLE.get();
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(ROLE.get());
    }

    /**
     * 学生端接口需在 USER 角色下访问。
     */
    public static boolean isUser() {
        return "USER".equals(ROLE.get());
    }
}
