# 对讲 PTT（安卓）

内置网页对讲（WebRTC + 公共 MQTT 信令 broker.emqx.io），原生外壳负责：
- 麦克风权限、本地 https 安全上下文（WebViewAssetLoader）
- 前台服务 + WakeLock：锁屏/后台继续收听
- 音量下键 = 按住说话；返回键 = 退到后台不退出

## 编译 APK（二选一）

### A. GitHub Actions（不用装任何东西）
1. GitHub 新建仓库（可私有）
2. 上传本目录全部内容（含 .github 文件夹）
3. 仓库 → Actions → build-apk → 等 5 分钟 → 下载 ptt-apk.zip → 里面 app-debug.apk

### B. Android Studio（Windows）
1. 安装 Android Studio（国内需开 VPN 下载 SDK/Gradle）
2. File → Open → 选本目录 → 等 Gradle Sync（提示缺 wrapper 就点 OK 让它生成）
3. Build → Build App Bundle(s) / APK(s) → Build APK(s)
4. 输出：app/build/outputs/apk/debug/app-debug.apk

## 使用
两台手机装 APK → 填同一房间号 → 加入 → 按住大按钮或音量下键说话。
「仅 IPv6」默认关闭：先 IPv6 直连，不行再走 STUN 打洞（IPv4/IPv6）。
小米/华为等：设置里把「对讲」的电池优化设为「无限制」，否则后台可能被杀。
