#!/bin/bash

# 高级打包脚本 - DevLog_SuperMan 应用
# 支持多种构建选项和平台

set -e  # 如果任何命令失败则退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_header() {
    echo -e "${BLUE}===================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}===================================${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# 默认参数
BUILD_TYPE="release"
TARGET_PLATFORM="current"
CLEAN_BUILD="false"
VERBOSE="false"

# 解析命令行参数
while [[ $# -gt 0 ]]; do
    case $1 in
        -t|--target)
            TARGET_PLATFORM="$2"
            shift 2
            ;;
        -b|--build-type)
            BUILD_TYPE="$2"
            shift 2
            ;;
        -c|--clean)
            CLEAN_BUILD="true"
            shift
            ;;
        -v|--verbose)
            VERBOSE="true"
            shift
            ;;
        -h|--help)
            print_header "帮助信息 - DevLog_SuperMan 打包脚本"
            echo "用法: $0 [选项]"
            echo ""
            echo "选项:"
            echo "  -t, --target PLATFORM    指定目标平台 (current, macos-x64, macos-arm64, windows-x64, linux-x64)"
            echo "  -b, --build-type TYPE    构建类型 (release, debug) [默认: release]"
            echo "  -c, --clean             清理之前的构建文件 [默认: false]"
            echo "  -v, --verbose           启用详细输出 [默认: false]"
            echo "  -h, --help              显示此帮助信息"
            echo ""
            echo "示例:"
            echo "  $0                              # 使用默认设置构建"
            echo "  $0 -c                           # 清理并构建"
            echo "  $0 -t macos-x64 -b release      # 为 macOS x64 构建发布版"
            echo "  $0 -t windows-x64               # 为 Windows x64 构建"
            exit 0
            ;;
        *)
            print_error "未知参数: $1"
            echo "使用 $0 --help 查看可用选项"
            exit 1
            ;;
    esac
done

print_header "DevLog_SuperMan 应用打包脚本"

# 获取项目根目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR"

echo "项目根目录: $PROJECT_ROOT"
echo "构建类型: $BUILD_TYPE"
echo "目标平台: $TARGET_PLATFORM"
echo "清理构建: $CLEAN_BUILD"
echo ""

# 进入项目目录
cd "$PROJECT_ROOT"

# 确保 gradlew 具有执行权限
chmod +x ./gradlew

# 显示系统信息
print_info "系统信息:"
uname -a
echo ""

if [ "$CLEAN_BUILD" = "true" ]; then
    print_info "清理之前的构建文件..."
    ./gradlew clean
    echo ""
fi

# 根据目标平台设置 Gradle 任务
GRADLE_TASK=""
case $TARGET_PLATFORM in
    "current")
        GRADLE_TASK=":composeApp:packageDistributionForCurrentOS"
        ;;
    "macos-x64"|"macos-arm64")
        # 对于特定的 macOS 架构，我们仍然使用相同的任务，但可能会有不同输出
        GRADLE_TASK=":composeApp:packageDistributionForCurrentOS"
        ;;
    "windows-x64")
        # Windows 上没有特定的 packageDistributionForWindows 任务，使用通用任务
        GRADLE_TASK=":composeApp:packageDistributionForCurrentOS"
        ;;
    "linux-x64")
        GRADLE_TASK=":composeApp:packageDistributionForCurrentOS"
        ;;
    *)
        print_warning "未知平台: $TARGET_PLATFORM，使用当前平台构建"
        GRADLE_TASK=":composeApp:packageDistributionForCurrentOS"
        ;;
esac

# 执行构建
print_info "开始构建应用..."
if [ "$VERBOSE" = "true" ]; then
    ./gradlew $GRADLE_TASK --info
else
    ./gradlew $GRADLE_TASK
fi

# 查找构建输出
DIST_DIR="$PROJECT_ROOT/composeApp/build/compose/binaries"
APP_DIR="$PROJECT_ROOT/composeApp/build/compose/app"

print_info "正在查找构建输出..."

# 检查是否有可执行的应用程序
if [ -d "$APP_DIR" ]; then
    echo ""
    print_info "应用程序目录内容:"
    ls -la "$APP_DIR"
fi

# 查找生成的安装包
if [ -d "$DIST_DIR" ]; then
    echo ""
    print_info "发行版目录内容:"
    find "$DIST_DIR" -type d -not -path "*/.*" | head -10
    find "$DIST_DIR" -type f -not -path "*/.*" | head -20
    
    echo ""
    # 查找所有安装包文件
    PKG_FILES=$(find "$DIST_DIR" -type f \( -name "*.dmg" -o -name "*.msi" -o -name "*.deb" -o -name "*.rpm" -o -name "*.pkg" -o -name "*.zip" -o -name "*.tar.gz" -o -name "*.tar.xz" \) 2>/dev/null || true)
    
    if [ -n "$PKG_FILES" ]; then
        echo ""
        print_success "发现安装包文件:"
        echo "$PKG_FILES"
        echo ""
        print_success "✅ 打包成功完成!"
        echo ""
        echo -e "${GREEN}安装包位置:${NC}"
        for pkg in $PKG_FILES; do
            echo "  - $pkg"
        done
    else
        print_warning "未找到标准安装包格式 (.dmg/.msi/.deb 等)"
        
        # 查找其他可能的输出
        OTHER_BINS=$(find "$DIST_DIR" -name "*" -type f -not -name "*.log" -not -name "*.sha256" -not -path "*/.*" 2>/dev/null || true)
        if [ -n "$OTHER_BINS" ]; then
            echo ""
            print_info "其他可能的构建输出文件:"
            echo "$OTHER_BINS"
        fi
        
        # 尝试查找可执行文件
        EXECUTABLES=$(find "$DIST_DIR" -name "*DevLog_SuperMan*" -type f -executable 2>/dev/null || true)
        if [ -n "$EXECUTABLES" ]; then
            echo ""
            print_info "发现可执行文件:"
            echo "$EXECUTABLES"
        fi
    fi
else
    print_error "构建输出目录不存在: $DIST_DIR"
    print_info "检查是否构建成功，以及 build.gradle.kts 中的配置是否正确"
fi

# 创建一个符号链接到最新的构建
LATEST_LINK="$PROJECT_ROOT/latest-build"
if [ -e "$LATEST_LINK" ]; then
    rm "$LATEST_LINK"
fi

if [ -d "$DIST_DIR" ] && [ -n "$(ls -A $DIST_DIR)" ]; then
    ln -s "$DIST_DIR" "$LATEST_LINK" 2>/dev/null || true
    print_info "最新构建链接: $LATEST_LINK"
fi

echo ""
print_header "构建摘要"
echo "时间: $(date)"
echo "项目: DevLog_SuperMan"
echo "构建类型: $BUILD_TYPE"
echo "目标平台: $TARGET_PLATFORM"
echo "状态: 完成"
echo ""
print_info "如需运行应用，请执行: ./gradlew :composeApp:run"