import {Component} from '@angular/core';
import {LoginDataService} from "../../services/login-data.service";
import {AuthService} from "../../services/auth.service";
import {ElectronService} from "../../services/electron.service";

@Component({
  selector: 'app-menu-bar',
  templateUrl: './menu-bar.component.html',
  styleUrls: ['./menu-bar.component.scss']
})
export class MenuBarComponent {
  fullscreen = false
  constructor(private electron: ElectronService, public loginData: LoginDataService, private auth: AuthService) {
    electron.on("fullscreenChange", (event :any, fullscreen: boolean) => {
      this.fullscreen = fullscreen
    })
  }
  onClose(){
    this.electron.ipcRenderer.send('close')
  }
  onFullscreen() {
    this.electron.ipcRenderer.send('fullscreen', this.fullscreen)
  }
  onMinimize(){
    this.electron.ipcRenderer.send('minimize')
  }

  onSignOut() {
    this.auth.logout().subscribe(() => {})
  }

}
