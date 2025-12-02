import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CiHuiService {

  showAll = signal(false)
  learnMode = signal(false)

  setLearnMode(value: boolean) {
    this.learnMode.set(value);
  }
  
  setShowAll(value: boolean) {
    this.showAll.set(value);
  }
  constructor() { }
}
