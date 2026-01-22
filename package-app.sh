#!/bin/bash

# DevLog_SuperMan 应用打包脚本
# 用于构建和打包 Compose Multiplatform Desktop 应用

set -e  # 如果任何命令失败则退出

echo "==================================="
echo "开始打包 DevLog_SuperMan 应用..."
echo "==================================="

# 检查是否是可执行文件
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR"

echo "项目根目录: $PROJECT_ROOT"

# 进入项目目录
cd "$PROJECT_ROOT"

# 确保 gradlew 具有执行权限
chmod +x ./gradlew

echo "正在清理之前的构建文件..."
./gradlew clean

echo "正在构建应用并生成发行版安装包..."

# 执行打包任务 - 这将为当前操作系统构建应用并创建安装包
./gradlew :composeApp:packageDistributionForCurrentOS

echo "正在查找生成的安装包..."

# 查找生成的安装包
DIST_DIR="$PROJECT_ROOT/composeApp/build/compose/binaries"
if [ -d "$DIST_DIR" ]; then
    echo "发现构建输出目录: $DIST_DIR"
    
    # 查找所有生成的包
    ALL_PACKAGES=$(find "$DIST_DIR" -type f \( -name "*.dmg" -o -name "*.msi" -o -name "*.deb" -o -name "*.rpm" -o -name "*.pkg" -o -name "*.tar.gz" -o -name "*.zip" -o -name "*.exe" \) 2>/dev/null || true)
    
    if [ -n "$ALL_PACKAGES" ]; then
        echo "==================================="
        echo "✅ 打包成功!"
        PACKAGE_COUNT=$(echo "$ALL_PACKAGES" | wc -l | tr -d ' ')
        echo "找到 $PACKAGE_COUNT 个安装包:"
        echo "$ALL_PACKAGES"
        echo "==================================="
    else
        echo "⚠️  未找到标准格式的安装包文件。"
        
        # 查找应用程序目录
        APP_DIRS=$(find "$DIST_DIR" -type d -name "*.app" -o -name "*exe" -o -name "*AppRun" 2>/dev/null || true)
        if [ -n "$APP_DIRS" ]; then
            echo "但发现了应用程序目录:"
            echo "$APP_DIRS"
        fi
        
        # 列出整个输出目录的内容
        echo ""
        echo "构建输出目录内容:"
        find "$DIST_DIR" -type f -not -path "*/\.*" | sort
    fi
else
    echo "⚠️  构建输出目录不存在: $DIST_DIR"
    echo "可能构建过程中出现了问题。"
fi

echo ""
echo "💡 提示："
echo "  - 对于 macOS: 查找 .dmg 文件"
echo "  - 对于 Windows: 查找 .msi 文件"  
echo "  - 对于 Linux: 查找 .deb 或 .tar.gz 文件"
echo ""
echo "如需为特定平台构建，请运行："
echo "  macOS: ./gradlew :composeApp:createDistributable (应用) 或 ./gradlew :composeApp:packageDistribution (安装包)"
echo "  Windows: ./gradlew :composeApp:createDistributable 或 ./gradlew :composeApp:packageDistribution"
echo "  Linux: ./gradlew :composeApp:createDistributable 或 ./gradlew :composeApp:packageDistribution"