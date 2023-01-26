const {app, BrowserWindow, ipcMain, Notification, Menu, Tray, session} = require('electron')
const path = require("path")


let mainWindow
let lastNotification

function isProduction(){
  return !(process.env.BUILD ? (process.env.BUILD.trim() === "false") : false)
}

function createWindow() {

  session.defaultSession.webRequest.onHeadersReceived(
    { urls: ['*://*/*'] }, (details, callback) => {
      const cookies = details.responseHeaders['Set-Cookie'];
      if(cookies) {
        const newCookie = Array.from(cookies)
          .map(cookie => cookie.concat('; SameSite=None; Secure=HttpOnly'));
        details.responseHeaders['Set-Cookie'] = [...newCookie];
        callback({
          responseHeaders: details.responseHeaders,
        });
      } else {
        callback({ cancel: false });
      }
    });
  const assetsPath = app.isPackaged ? path.join(process.resourcesPath, "assets") : "assets";

  mainWindow = new BrowserWindow({
    width: 800,
    height: 600,
    //transparent:true,
    title: "Chat JS",
    icon: path.join(assetsPath, "icon.png"),
    frame: false,
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: false,
    }
  })

  mainWindow.on('close', function (event) {
    if(!app.isQuiting){
      event.preventDefault();
      mainWindow.hide();
    }
    return false;
  });


  mainWindow.on("focus", () => {
    replaceLastNotification()
  })

  if (!isProduction()) {
    app.setAppUserModelId(process.execPath)
    mainWindow.loadURL('http://localhost:4200').then()
    mainWindow.webContents.openDevTools()
  } else {
    mainWindow.loadFile(path.join(__dirname, 'dist/ChatApp/index.html'));
    mainWindow.removeMenu()
    // mainWindow.webContents.openDevTools()
  }
  mainWindow.on("maximize", e => {
    mainWindow.webContents.send("fullscreenChange", true)
  })
  mainWindow.on("unmaximize", e => {
    mainWindow.webContents.send("fullscreenChange", false)
  })
  ipcMain.on("close", e => {
    mainWindow.close()
  })
  ipcMain.on("fullscreen", (e, fullscreen) => {
    fullscreen ? mainWindow.unmaximize() : mainWindow.maximize();
  })
  ipcMain.on("minimize", e => {
    mainWindow.minimize()
  })

  let contextMenu = Menu.buildFromTemplate([
    { label: 'Show App', click: () => {
        mainWindow.show();
      } },
    { label: 'Quit', click: () => {
        app.isQuiting = true;
        app.quit();
      } }
  ]);

  let appIcon = new Tray(path.join(assetsPath, "icon.png"));
  appIcon.setToolTip('Chat JS');
  appIcon.setContextMenu(contextMenu);
}

app.on('ready', createWindow)

app.on('window-all-closed', function () {
  if (process.platform !== 'darwin') app.quit()
})

app.on('activate', function () {
  if (mainWindow === null) createWindow()
})

ipcMain.on("notification", (event, content) => {
  if (!mainWindow.isFocused()) {
    let notification = new Notification(content)
    notification.addListener("click", () => {
      mainWindow.show()
      replaceLastNotification()
    })
    replaceLastNotification(notification)
    notification.show()
  }
})

function replaceLastNotification(to = undefined){
  lastNotification?.close()
  lastNotification = to
}

