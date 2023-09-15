package com.fy.chatserver.communicate;

/**
 * @author zolmk
 */
public interface Constants {
    interface NotificationCode {
        /**
         * Note peer to register.
         */
        int NOTE_PEER_REGISTER = 0;

        /**
         * reply for register.
         */
        int REPLY_PEER_REGISTER = 1;
    }

    interface GroupOpCode {
        /**
         * Create Op
         */
        int CREATE = 0;

        /**
         * Join Op
         */
        int JOIN = 1;

        /**
         * Quit Op
         */
        int QUIT = 2;

        /**
         * Dissolve Op
         */
        int DISSOLVE = 3;

        /**
         * Invite Op
         */
        int INVITE = 4;
    }





}
