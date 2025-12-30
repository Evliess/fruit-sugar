import { Component, inject } from '@angular/core';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzButtonModule } from 'ng-zorro-antd/button';

@Component({
  selector: 'app-a-page',
  imports: [NzFormModule, NzInputModule, NzButtonModule],
  templateUrl: './a-page.component.html',
  styleUrl: './a-page.component.css'
})
export class APageComponent {
 
  generate() {
    console.log('Admin page action executed.');
  }

}
