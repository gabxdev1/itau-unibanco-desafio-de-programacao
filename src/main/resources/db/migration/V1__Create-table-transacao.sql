create table transacao
(
    id        bigint         not null auto_increment,
    data_hora datetime(6)    not null,
    valor     decimal(38, 2) not null,
    primary key (id)
) engine = InnoDB;