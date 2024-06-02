package com.feiyu.msgserver.discovery;

import java.util.List;

/**
 * join group / create group / quit group / release group / invite user to group
 * @author zhufeifei 2023/9/15
 **/

public interface GroupFinder {
    /**
     * create a group
     * @param uid user id
     * @param isPublish weather the group is public
     * @return group id
     */
    String create(String uid, boolean isPublish);

    /**
     * join a group
     * @param uid user id
     * @param gid group id
     */
    void join(String uid, String gid);

    /**
     * quit from the group
     * @param uid user id
     * @param gid group id
     * @return weather the quit op is success
     */
    boolean quit(String uid, String gid);

    /**
     * dissolve the group.
     * @param uid user id
     * @param gid group id
     * @return weather the op is success
     */
    boolean dissolve(String uid, String gid);

    /**
     * invite the use to group
     * @param uid user id
     * @param toId invited user id
     * @param gid group id
     */
    void invite(String uid, String toId, String gid);

    /**
     * add the user who become an administrator
     * @param uid user id
     * @param toId the target user id.
     * @param gid group id
     * @return weather the op is success
     */
    boolean addToAdmin(String uid, String toId, String gid);

    /**
     * remove the user from administrator list.
     * @param uid user id
     * @param toId the target user id
     * @param gid group id
     * @return weather the op is success
     */
    boolean removeFromAdmin(String uid, String toId, String gid);

    /**
     * remove a user from group list.
     * @param uid user id
     * @param toId the target id
     * @param gid group id
     * @return weather the op is success
     */
    boolean remove(String uid, String toId, String gid);

    /**
     * list all users in group.
     * @param uid user id
     * @param gid group id
     * @return all users in group
     */
    List<String> list(String uid, String gid);
}
