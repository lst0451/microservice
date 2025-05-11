#!/bin/bash
set -e # 如果任何命令失败，则立即退出

# 使用 psql 工具执行 SQL 命令。
# 这些命令将以 POSTGRES_USER (在 docker-compose.yml 中定义，例如 'lst') 的身份执行。
# $POSTGRES_DB 是在 docker-compose.yml 中定义的主数据库名 (例如 'userdb')，
# 脚本会连接到这个已由官方镜像入口点自动创建的数据库来执行创建其他数据库的命令。

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE productdb;
    CREATE DATABASE orderdb;
    --也可以在这里为这些数据库授予特定权限，尽管默认情况下 "$POSTGRES_USER" 会拥有它们
    -- GRANT ALL PRIVILEGES ON DATABASE productdb TO "$POSTGRES_USER";
    -- GRANT ALL PRIVILEGES ON DATABASE orderdb TO "$POSTGRES_USER";
    \echo "Databases productdb and orderdb created successfully"
EOSQL