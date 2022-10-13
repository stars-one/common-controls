module.exports = {
    types: [
        { types: ["feat", "feature",":sparkles:",":boom:"], label: "🎉 New Features(新功能)" },
        { types: ["fix", "bugfix",":bug:",":ambulance:"], label: "🐛 Bugfixes(修复Bug)" },
        { types: ["improvements", "enhancement"], label: "🔨 Improvements(功能改善)" },
        { types: ["perf",":zap:"], label: "🏎️ Performance Improvements(性能提升)" },
        { types: ["build", "ci"], label: "🏗️ Build System(构建系统)" },
        { types: ["refactor",":art:",":recycle:",":hammer:"], label: "🪚 Refactors(重构)" },
        { types: ["doc", "docs",":memo:"], label: "📚 Documentation Changes(文档更新)" },
        { types: ["test", "tests",":white_check_mark:"], label: "🔍 Tests(测试)" },
        { types: ["style",":art:"], label: "💅 Code Style Changes(代码风格变更)" },
        { types: ["chore",":wastebasket:",":wastebasket:"], label: "🧹 Chores(代码清理)" },
        { types: ["other"], label: "Other Changes(其他变更)" },
        { types: [":wrench:"], label: "配置文件变更" },
    ],
    excludeTypes: ["other"]
};
