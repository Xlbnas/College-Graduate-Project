#!/usr/bin/env bash
# 校园二手交易平台 — 本机一键启动（后端 + 前端）
# 用法：在项目根目录执行  chmod +x scripts/dev-start.sh && ./scripts/dev-start.sh
# 不设 set -u：毕设本机脚本以易跑为主；且 macOS 默认 Bash 3.2 在「$变量」紧贴全角「（」时易解析异常，需用 ${var} 分隔。

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BACKEND="$ROOT/backend"
FRONTEND="$ROOT/frontend"
LOGDIR="$ROOT/scripts/logs"
APP_YML="$BACKEND/src/main/resources/application.yml"
BACK_PID_FILE="$LOGDIR/backend.pid"
FRONT_PID_FILE="$LOGDIR/frontend.pid"
WAITER_PID_FILE="$LOGDIR/waiter.pid"

mkdir -p "$LOGDIR"

info()  { echo "[信息] $*"; }
warn()  { echo "[提示] $*" >&2; }
fatal() { echo "[错误] $*" >&2; exit 1; }

cleanup() {
  if [[ -f "$BACK_PID_FILE" ]]; then
    local p
    p=$(cat "$BACK_PID_FILE" 2>/dev/null || true)
    if [[ -n "${p:-}" ]] && kill -0 "$p" 2>/dev/null; then
      info "正在停止后端进程 $p …"
      kill "$p" 2>/dev/null || true
      sleep 1
      kill -9 "$p" 2>/dev/null || true
    fi
    rm -f "$BACK_PID_FILE"
  fi
  if [[ -f "$FRONT_PID_FILE" ]]; then
    local p2
    p2=$(cat "$FRONT_PID_FILE" 2>/dev/null || true)
    if [[ -n "${p2:-}" ]] && kill -0 "$p2" 2>/dev/null; then
      info "正在停止前端进程 $p2 …"
      kill "$p2" 2>/dev/null || true
      sleep 1
      kill -9 "$p2" 2>/dev/null || true
    fi
    rm -f "$FRONT_PID_FILE"
  fi
  if [[ -f "$WAITER_PID_FILE" ]]; then
    local w
    w=$(cat "$WAITER_PID_FILE" 2>/dev/null || true)
    if [[ -n "${w:-}" ]] && kill -0 "$w" 2>/dev/null; then
      kill "$w" 2>/dev/null || true
    fi
    rm -f "$WAITER_PID_FILE"
  fi
}

trap 'echo ""; warn "收到中断信号，正在清理子进程…"; cleanup; exit 130' INT TERM

need_cmd() {
  command -v "$1" >/dev/null 2>&1 || fatal "未找到命令「$1」。请先安装：$2"
}

need_cmd java "macOS 可执行：brew install openjdk@17，并配置 JAVA_HOME"
need_cmd mvn  "macOS 可执行：brew install maven"
need_cmd node "https://nodejs.org/ 安装 LTS，或使用 brew install node"
need_cmd npm  "随 Node.js 一同安装"

if [[ ! -f "$APP_YML" ]]; then
  fatal "找不到配置文件：$APP_YML"
fi

# 从 application.yml 粗略解析数据源（与当前项目格式一致）
DB_USER=$(grep -E '^\s+username:\s*' "$APP_YML" | head -1 | sed -E 's/^[[:space:]]*username:[[:space:]]*//')
DB_PASS=$(grep -E '^\s+password:\s*' "$APP_YML" | head -1 | sed -E 's/^[[:space:]]*password:[[:space:]]*"?([^"]*)"?[[:space:]]*$/\1/')
DB_URL_LINE=$(grep 'jdbc:mysql://' "$APP_YML" | grep -vE '^\s*#' | head -1 | sed -E 's/^[[:space:]]*url:[[:space:]]*//')
# 解析 host / port / database 名（默认 127.0.0.1:3306 / campus_trade）
DB_HOST="127.0.0.1"
DB_PORT="3306"
DB_NAME="campus_trade"
if [[ "$DB_URL_LINE" =~ jdbc:mysql://([^:/]+):([0-9]+)/([^?]+) ]]; then
  DB_HOST="${BASH_REMATCH[1]}"
  DB_PORT="${BASH_REMATCH[2]}"
  DB_NAME="${BASH_REMATCH[3]}"
elif [[ "$DB_URL_LINE" =~ jdbc:mysql://([^/]+)/([^?]+) ]]; then
  DB_HOST="${BASH_REMATCH[1]}"
  DB_NAME="${BASH_REMATCH[2]}"
fi

# 旧库若未执行过 real_name 迁移，MyBatis 会查不存在的列，接口统一变成「系统繁忙」。
ensure_real_name_columns() {
  local cnt_user cnt_admin
  cnt_user=$(mysql -N -s -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -e \
    "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='${DB_NAME}' AND TABLE_NAME='tb_user' AND COLUMN_NAME='real_name';" 2>/dev/null || echo 0)
  cnt_admin=$(mysql -N -s -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -e \
    "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='${DB_NAME}' AND TABLE_NAME='tb_admin' AND COLUMN_NAME='real_name';" 2>/dev/null || echo 0)
  cnt_user=$(echo "${cnt_user}" | tr -d ' \n\r')
  cnt_admin=$(echo "${cnt_admin}" | tr -d ' \n\r')
  if [[ "${cnt_user}" == "1" && "${cnt_admin}" == "1" ]]; then
    return 0
  fi
  info "检测到表缺少 real_name 列（旧库），正在自动执行 ALTER …"
  if [[ "${cnt_user}" != "1" ]]; then
    mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -e \
      "USE \`${DB_NAME}\`; ALTER TABLE tb_user ADD COLUMN real_name VARCHAR(50) NOT NULL DEFAULT '' COMMENT '展示姓名' AFTER username;" \
      || fatal "为 tb_user 添加 real_name 失败，请查看上方 mysql 报错。"
  fi
  if [[ "${cnt_admin}" != "1" ]]; then
    mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -e \
      "USE \`${DB_NAME}\`; ALTER TABLE tb_admin ADD COLUMN real_name VARCHAR(50) NOT NULL DEFAULT '' COMMENT '展示姓名' AFTER username;" \
      || fatal "为 tb_admin 添加 real_name 失败，请查看上方 mysql 报错。"
  fi
  mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" --default-character-set=utf8mb4 "$DB_NAME" -e "
UPDATE tb_user SET real_name='张三' WHERE username='zhangsan';
UPDATE tb_user SET real_name='李四' WHERE username='lisi';
UPDATE tb_user SET real_name='王五' WHERE username='wangwu';
UPDATE tb_user SET real_name='赵六' WHERE username='zhaoliu';
UPDATE tb_user SET real_name='孙七' WHERE username='sunqi';
UPDATE tb_user SET real_name='测试用户一' WHERE username='user1';
UPDATE tb_admin SET real_name='系统管理员' WHERE username='admin';
UPDATE tb_admin SET real_name='审核员乙' WHERE username='auditor02';
" || warn "写入展示名默认值时出现警告，可稍后手动执行 database/schema_alter_user_real_name.sql 中的 UPDATE。"
  info "real_name 列已就绪。"
}

# 旧库或手工录入的商品 image_url 为空时，首页会全是占位图；按分类填 frontend/public/demo 下的演示图。
ensure_product_demo_images() {
  local empty_cnt
  empty_cnt=$(mysql -N -s -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -e \
    "USE \`${DB_NAME}\`; SELECT COUNT(*) FROM tb_product WHERE image_url IS NULL OR TRIM(image_url)='';" 2>/dev/null || echo 0)
  empty_cnt=$(echo "${empty_cnt}" | tr -d ' \n\r')
  if [[ "${empty_cnt}" == "0" ]]; then
    return 0
  fi
  info "检测到 ${empty_cnt} 条商品 image_url 为空，正在写入 /demo/*.svg 演示路径 …"
  mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" --default-character-set=utf8mb4 "$DB_NAME" -e "
UPDATE tb_product SET image_url = CASE category_id
  WHEN 1 THEN '/demo/item-1.svg'
  WHEN 2 THEN '/demo/item-2.svg'
  WHEN 3 THEN '/demo/item-3.svg'
  WHEN 4 THEN '/demo/item-4.svg'
  WHEN 5 THEN '/demo/item-5.svg'
  ELSE '/demo/item-6.svg'
END
WHERE image_url IS NULL OR TRIM(image_url) = '';
" || fatal "补全商品 image_url 失败。"
  info "商品演示图路径已补全。"
}

# 若目标端口已被监听，则根据 lsof 得到的 PID 自动结束进程（先 SIGTERM，仍存活再 SIGKILL）。
check_port() {
  local port="$1"
  local name="$2"
  if ! command -v lsof >/dev/null 2>&1; then
    warn "未找到 lsof，无法自动清理端口 ${port}（${name}）。若启动失败请安装 lsof 或手动结束占用进程。"
    return 0
  fi
  local pids
  pids=$(lsof -tiTCP:"$port" -sTCP:LISTEN 2>/dev/null || true)
  if [[ -z "${pids:-}" ]]; then
    return 0
  fi
  info "端口 ${port}（${name}）被占用，正在结束监听进程：$(echo "${pids}" | tr '\n' ' ' | sed 's/[[:space:]]*$//')"
  local pid
  for pid in $(echo "${pids}" | sort -u); do
    [[ -n "${pid}" ]] || continue
    kill "${pid}" 2>/dev/null || true
  done
  sleep 1
  for pid in $(echo "${pids}" | sort -u); do
    [[ -n "${pid}" ]] || continue
    if kill -0 "${pid}" 2>/dev/null; then
      info "进程 ${pid} 未退出，发送 SIGKILL …"
      kill -9 "${pid}" 2>/dev/null || true
    fi
  done
  sleep 1
  if lsof -tiTCP:"$port" -sTCP:LISTEN >/dev/null 2>&1; then
    fatal "端口 ${port} 仍被占用，请手动检查后重试。"
  fi
  info "端口 ${port} 已释放。"
}

check_port 8080 "后端 Spring Boot"
check_port 8081 "前端 Vue 开发服务"

if command -v mysql >/dev/null 2>&1; then
  info "检测 MySQL 连接（${DB_HOST}:${DB_PORT}，库 ${DB_NAME}，用户 ${DB_USER}）…"
  if ! mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -e "USE \`${DB_NAME}\`; SELECT 1" >/dev/null 2>&1; then
    warn "无法连接 MySQL 或无法打开库「${DB_NAME}」。请依次检查："
    warn "  1) MySQL 是否已启动：可执行  /opt/homebrew/opt/mysql@8.0/bin/mysql.server start  （路径因安装方式而异）"
    warn "  2) 是否监听 TCP：在 mysql 内执行  SHOW VARIABLES LIKE 'skip_networking';  应为 OFF；SHOW VARIABLES LIKE 'port';  应为 3306"
    warn "  3) 若曾用 --skip-grant-tables 启动过，请先正常停库再启动，否则 JDBC 无法连上。"
    warn "  4) 账号密码是否与 $APP_YML 一致；若修改过密码请同步修改该文件。"
    warn "  5) 是否已导入 database/schema.sql 与 database/data.sql（或执行过迁移脚本）。"
    read -r -p "仍要继续启动后端？(y/N) " ans2 || true
    if [[ "${ans2:-}" != "y" && "${ans2:-}" != "Y" ]]; then
      exit 1
    fi
  else
    info "MySQL 连接正常。"
    ensure_real_name_columns
    ensure_product_demo_images
  fi
else
  warn "未找到 mysql 客户端，已跳过数据库连通性检测。"
  warn "若后端启动后接口报「无法连接 MySQL」，请先安装客户端或确认服务已启动。"
fi

if [[ ! -d "$FRONTEND/node_modules" ]]; then
  info "首次运行，正在 npm install …"
  (cd "$FRONTEND" && npm install) || fatal "npm install 失败，请查看上方报错。"
fi

cleanup

info "启动后端（日志：$LOGDIR/backend.log）…"
# 注意：后台进程必须在写入 $! 的同一 shell 中启动；若写成 (cd … &); echo $! 则外层 $! 未定义（set -u 会报错）。
(
  cd "$BACKEND" || exit 1
  nohup mvn -q -DskipTests spring-boot:run >"$LOGDIR/backend.log" 2>&1 &
  echo $! >"$BACK_PID_FILE"
)

info "等待后端就绪（最多约 90 秒）…"
ok=0
for i in $(seq 1 45); do
  if curl -sf "http://127.0.0.1:8080/api/categories" >/dev/null 2>&1; then
    ok=1
    break
  fi
  sleep 2
done
if [[ "$ok" != "1" ]]; then
  warn "后端在预期时间内未响应 /api/categories。"
  warn "请查看日志：tail -n 80 $LOGDIR/backend.log"
  warn "常见原因：端口被占用、数据库未启动、依赖下载失败。"
  read -r -p "是否仍启动前端？(y/N) " ans3 || true
  if [[ "${ans3:-}" != "y" && "${ans3:-}" != "Y" ]]; then
    cleanup
    exit 1
  fi
else
  info "后端已就绪：http://127.0.0.1:8080/api"
fi

info "启动前端（日志：$LOGDIR/frontend.log）…"
(
  cd "$FRONTEND" || exit 1
  nohup npm run serve >"$LOGDIR/frontend.log" 2>&1 &
  echo $! >"$FRONT_PID_FILE"
)

info "等待前端就绪（最多约 60 秒）…"
ok2=0
for j in $(seq 1 30); do
  if curl -sf "http://127.0.0.1:8081/" >/dev/null 2>&1; then
    ok2=1
    break
  fi
  sleep 2
done
if [[ "$ok2" != "1" ]]; then
  warn "前端可能仍在编译，请稍后在浏览器打开：http://localhost:8081"
  warn "若长时间无响应，请查看：tail -n 80 $LOGDIR/frontend.log"
else
  info "前端已就绪。"
fi

echo ""
info "访问地址：  http://localhost:8081"
info "后端接口：  http://127.0.0.1:8080/api"
info "停止方式：  在本终端按 Ctrl+C，或执行  kill \$(cat $BACK_PID_FILE) \$(cat $FRONT_PID_FILE)"
info "查看日志：  tail -f $LOGDIR/backend.log   /   tail -f $LOGDIR/frontend.log"
echo ""
info "本脚本将保持运行；按 Ctrl+C 可停止后端与前端进程。"
while true; do sleep 3600; done &
WAIT_BG=$!
echo "$WAIT_BG" >"$WAITER_PID_FILE"
wait "$WAIT_BG"
