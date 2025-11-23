import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CiHuiService {

  showAll = signal(false)
  
  setShowAll(value: boolean) {
    this.showAll.set(value);
  }
  constructor() { }
}
