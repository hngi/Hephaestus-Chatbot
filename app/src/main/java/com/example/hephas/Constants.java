package com.example.hephas;

class Constants {
    
    private static String userName = "";

    private static Integer botAvatar;

    public static Integer getBotAvatarLarge() {
        return botAvatarLarge;
    }

    public static void setBotAvatarLarge(Integer botAvatarLarge) {Constants.botAvatarLarge = botAvatarLarge;
    }

    private static Integer botAvatarLarge;

    public static Integer getBotAvatar() {
        return botAvatar;
    }

    public static void setBotAvatar(Integer botAvatar) {
        Constants.botAvatar = botAvatar;
    }

    static String getUserName() {
        return userName;
    }

    static void setUserName(String username) {
        userName = username;
    }
}