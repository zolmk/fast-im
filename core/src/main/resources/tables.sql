create database fast_im;

create table client_info (
    id bigint(20) auto_increment primary key        comment '客户端信息ID',
    cl_id bigint(64) not null                       comment '客户端ID',
    dvc_name varchar(200)                           comment '客户端设备名称',
    dvc_sn varchar(200)                             comment '设备序列号',
    sync bool default false                         comment '消息漫游开关',
    seq bigint(64) not null                         comment '最新序列号',
    bounds int default 0                            comment '同步界限，同步设置值天数内的消息',
    create_time timestamp default NOW()              comment '创建时间',
    update_time timestamp default NOW()              comment '更新时间',
    unique index cl_id_sn_idx(cl_id, dvc_sn)        comment '客户端ID-设备序列号索引'
) engine = InnoDB default charset =utf8mb4          comment '客户端信息表';

create table client_status (
    cl_id bigint(64) not null primary key       comment '客户端ID',
    status int default false                    comment '状态，0-离线、>1-在线',
    create_time timestamp default NOW()          comment '创建时间',
    update_time timestamp default NOW()          comment '更新时间'
) engine = InnoDB default charset = utf8mb4     comment '客户端状态表';

create table msg (
    id bigint(64) not null primary key           comment '消息ID',
    sender bigint(64) not null                  comment '消息来源',
    receiver bigint(64) not null                comment '接收者',
    seq bigint(64) not null                     comment '消息序列号',
    status tinyint default 0                    comment '消息状态，<0-已删除、0-已接收、1-已送达、2-已读',
    content text                                comment '消息内容',
    send_time timestamp                          comment '发送时间',
    type int default 0                          comment '消息类型，业务用',
    create_time timestamp default NOW()          comment '创建时间',
    index msg_sync_idx(receiver, seq)           comment '消息多端同步索引'
) engine = InnoDB default charset = utf8mb4     comment '客户端消息表';


