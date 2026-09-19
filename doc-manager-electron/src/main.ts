import { app, BrowserWindow, dialog } from 'electron';
import { spawn, ChildProcess } from 'child_process';
import * as path from 'path';
import * as fs from 'fs';

let mainWindow: BrowserWindow | null = null;
let backendProcess: ChildProcess | null = null;
let isQuitting = false;

const BACKEND_PORT = 8080; // 后端端口，需与后端配置一致
const BACKEND_STARTUP_DELAY = 5000; // 后端启动等待时间（毫秒）

function killBackendProcess(): void {
  if (backendProcess) {
    // 在 Windows 上，如果 Java 进程没有立即退出，可以使用 taskkill 强制结束进程树
    if (process.platform === 'win32') {
      try {
        // 使用 taskkill 强制结束进程及其子进程
        const { exec } = require('child_process');
        exec(`taskkill /pid ${backendProcess.pid} /T /F`);
      } catch (e) {
        // 忽略错误
      }
    } else {
      try {
        process.kill(-backendProcess.pid!, 'SIGTERM');
      } catch (e) {
        backendProcess.kill('SIGTERM');
      }
    }
    backendProcess = null;
  }
}

/**
 * 启动 Spring Boot 后端进程
 * 注意：开发模式（通过 npm start 或 electron .）Electron 可执行文件位于 node_modules/electron/dist/ 内
 * 打包的时候，资源为安装目录下的 resources 文件夹
 */
function startBackend(): void {
  let jarPath: string;

  if (app.isPackaged) {
    // 打包后：资源位于 process.resourcesPath/resources/
    jarPath = path.join(process.resourcesPath, 'backend.jar');
  } else {
    // 开发模式：项目根目录下的 resources/
    jarPath = path.join(app.getAppPath(), 'resources', 'backend.jar');
  }

  console.log('Backend jar path:', jarPath);

  if (!fs.existsSync(jarPath)) {
    dialog.showErrorBox('错误', `找不到后端 jar 文件：${jarPath}`);
    app.quit();
    return;
  }

  // 日志文件路径（用户数据目录可写）
  const logFile = path.join(app.getPath('userData'), 'backend.log');
  const logStream = fs.createWriteStream(logFile, { flags: 'a' });
  logStream.write(`[${new Date().toISOString()}] Starting backend...\n`);

  // 启动 Java 进程 使用进程组
  backendProcess = spawn('java', ['-jar', jarPath], {
    env: { ...process.env, DOC_MANAGER_HOME: path.join(app.getPath('home'), 'doc-manager') },
    stdio: ['ignore', 'pipe', 'pipe'],  // 保持输出重定向
    windowsHide: true,  // 隐藏 Windows 控制台窗口
  });

  // 将后端输出写入日志
  backendProcess.stdout?.on('data', (data: Buffer) => logStream.write(data));
  backendProcess.stderr?.on('data', (data: Buffer) => logStream.write(data));

  backendProcess.on('error', (err) => {
    logStream.write(`Failed to start backend: ${err.message}\n`);
    dialog.showErrorBox('错误', `后端启动失败：${err.message}\n日志文件：${logFile}`);
    app.quit();
  });

  backendProcess.on('close', (code) => {
    logStream.write(`Backend exited with code ${code}\n`);
    logStream.end();
    if (!isQuitting) {
      dialog.showErrorBox('后端停止', `后端进程意外退出，代码 ${code}。\n日志文件：${logFile}`);
      app.quit();
    }
  });
}

/**
 * 等待后端就绪（使用固定延时，避免依赖健康检查端点）
 */
function waitForBackendReady(callback: () => void): void {
  console.log(`Waiting ${BACKEND_STARTUP_DELAY}ms for backend to start...`);
  setTimeout(callback, BACKEND_STARTUP_DELAY);
}

/**
 * 创建主窗口
 */
function createWindow(): void {
  mainWindow = new BrowserWindow({
    width: 1400,
    height: 800,
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
      preload: path.join(__dirname, 'preload.js') // 编译后为 preload.js
    }
  });

  // 加载后端托管的页面
  mainWindow.loadURL(`http://localhost:${BACKEND_PORT}`);

  // 拦截 Ctrl+R 刷新快捷键，防止页面加载错误
  mainWindow.webContents.on('before-input-event', (event, input) => {
    if (input.control && input.key.toLowerCase() === 'r') {
      event.preventDefault(); // 阻止默认刷新
    }
  });

  mainWindow.on('closed', () => {
    mainWindow = null;
  });
}

// 应用生命周期
app.whenReady().then(() => {
  startBackend();
  waitForBackendReady(() => {
    createWindow();
  });

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createWindow();
    }
  });
});

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

// 应用退出前执行
app.on('before-quit', () => {
  isQuitting = true;
  killBackendProcess();
});

app.on('will-quit', () => {
  killBackendProcess();
});