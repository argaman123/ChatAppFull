import {Injectable, NgZone} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ElectronService {
  ipcRenderer;
  constructor(private zone: NgZone) {
    if ((window as any).require) {
      try {
        const electron = (window as any).require('electron')
        this.ipcRenderer = electron.ipcRenderer;
      } catch (error) {
        throw error;
      }
    } else {
      console.warn('Could not load electron');
    }
  }

  on(name: string, func: Function){
    try {
      this.ipcRenderer.on(name, (event: any, content: string) => {
        this.zone.run(() => {
          func(event, content)
        })
      })
    } catch (e) {
      return
    }
  }

}
