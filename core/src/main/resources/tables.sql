-- 创建数据库
create database chat_server;

use chat_server;

/*
    用户状态表
 */

 create if not exists table user_state(
     uid varchar(50) not null primary key,
     online tinyint not null ,
     last_dt datetime not null
 )engine=InnoDB;

create procedure proc_add_if_absent(in u_uid varchar(50), in online tinyint, in last_dt datetime)
begin
    declare u_count tinyint default 0;
    select count(1) c into u_count from user_state where uid=u_uid;
    if u_count > 0 then
        update user_state set `online`=online, `last_dt`=last_dt where `uid`=u_uid;
    else
        insert into user_state(`uid`, `online`, `last_dt`) value(u_uid, online, last_dt);
end if;
end

