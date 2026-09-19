const fs = require('fs');
const path = require('path');

// 后端模块根目录（doc-manager/doc-manager-web）
const backendDir = path.join(__dirname, '..', '..', 'doc-manager-web');
// 源 jar 文件
const source = path.join(backendDir, 'target', 'doc-manager-web-0.0.1-SNAPSHOT.jar');
// 目标目录（resources 目录）
const targetDir = path.join(__dirname, '..', 'resources');
// 目标文件完整路径
const target = path.join(targetDir, 'backend.jar');

// 确保目标目录存在（而不是文件）
if (!fs.existsSync(targetDir)) {
  fs.mkdirSync(targetDir, { recursive: true });
}

// 复制并重命名
if (fs.existsSync(source)) {
  fs.copyFileSync(source, target);
  console.log('后端 jar 复制成功:', target);
} else {
  console.error('源 jar 文件不存在，请先构建后端项目：mvn clean package');
  process.exit(1);
}