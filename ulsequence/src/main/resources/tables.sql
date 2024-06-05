CREATE DATABASE ul_sequence;
USE ul_sequence;
CREATE TABLE node_info (
    id int auto increment primary       COMMIT '节点主键ID',
    node_id varchar(50) not null        COMMIT '节点ID',
    start_uid long not null             COMMIT '节点负责分配的起始用户ID',
    end_uid long not null               COMMIT '节点负责分配的结尾用户ID',
    grow_step int                       COMMIT '增长步长',
    seg_size int                        COMMIT '共享段的大小',
    seg_cnt int                         COMMIT '节点负责管理的段数量',
    create_date datetime                COMMIT '创建时间',
    update_date datetime                COMMIT '更新时间'
)ENGINE=InnoDB;

CREATE INDEX index_node_info_node_id ON node_info (node_id);

CREATE TABLE seg_info (
    id int auto increment primary       COMMIT '段信息ID',
    nid int not null                    COMMIT '节点主键ID',
    max_seg long not null               COMMIT '号段的下一个最大序列号',
    first long not null                 COMMIT '号段首个用户ID',
    end long not null                   COMMIT '号段结尾用户ID',
    create_date datetime                COMMIT '创建时间',
    update_date datetime                COMMIT '更新时间'
)ENGINE=InnoDB;

CREATE INDEX index_seg_info_nid ON seg_info (nid);